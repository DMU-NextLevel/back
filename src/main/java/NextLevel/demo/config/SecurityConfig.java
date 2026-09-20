package NextLevel.demo.config;

import NextLevel.demo.config.security.filter.AccessTokenFilter;
import NextLevel.demo.config.security.filter.RefreshTokenFilter;
import NextLevel.demo.config.security.filter.UserHistoryFilter;
import NextLevel.demo.exception.CustomException;
import NextLevel.demo.exception.ErrorCode;
import NextLevel.demo.oauth.NullAuthorizedClientRepository;
import NextLevel.demo.oauth.OAuthFailureHandler;
import NextLevel.demo.oauth.OAuthSuccessHandler;
import NextLevel.demo.oauth.SocialLoginService;
import NextLevel.demo.role.UserRole;
import NextLevel.demo.user.repository.UserHistoryRepository;
import NextLevel.demo.user.repository.UserRepository;
import NextLevel.demo.user.service.LoginService;
import NextLevel.demo.util.jwt.JWTUtil;
import jakarta.persistence.EntityManager;
import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.util.matcher.RequestMatcherEntry;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JWTUtil jwtUtil;
    private final LoginService loginService;
    private final SocialLoginService socialLoginService;
    private final UserHistoryRepository userHistoryRepository;
    private final UserRepository userRepository;
    private final EntityManager entityManager;

    private final HandlerExceptionResolver handlerExceptionResolver;

    @Autowired
    public SecurityConfig(
        JWTUtil jwtUtil,
        LoginService loginService,
        SocialLoginService socialLoginService,
        UserHistoryRepository userHistoryRepository,
        UserRepository userRepository,
        EntityManager entityManager,

        @Qualifier("handlerExceptionResolver") HandlerExceptionResolver handlerExceptionResolver) {
        this.jwtUtil = jwtUtil;
        this.loginService = loginService;
        this.socialLoginService = socialLoginService;
        this.handlerExceptionResolver = handlerExceptionResolver;
        this.userHistoryRepository = userHistoryRepository;
        this.userRepository = userRepository;
        this.entityManager = entityManager;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        OAuthSuccessHandler oAuthSuccessHandler = new OAuthSuccessHandler(loginService, jwtUtil);
        OAuthFailureHandler oAuthFailureHandler = new OAuthFailureHandler();

        http
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable()) // CSRF 비활성화 (필요에 따라 활성화 가능)
            .formLogin(form -> form.disable()) // 폼 로그인 비활성화
            .httpBasic(httpBasic -> httpBasic.disable()) // 기본 로그인 비활성화
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 사용 안함

            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login/**").permitAll()
                .requestMatchers("/public/**").permitAll()
                .requestMatchers("/payment/**").permitAll()
                .requestMatchers("/social/**").hasRole("SOCIAL")
                .requestMatchers("/api1/**").access(new AuthorizationManager<RequestAuthorizationContext>() {
                    @Override
                    public AuthorizationResult authorize(
                            Supplier<Authentication> authentication,
                            RequestAuthorizationContext object
                    ) {
                        Authentication auth = authentication.get();
                        if (auth == null || auth instanceof AnonymousAuthenticationToken)
                            return new ErrorCodeAuthorizationResult(ErrorCode.NO_AUTHENTICATED);

                        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();

                        if (authorities.containsAll(UserRole.USER.getAuthorities()))
                            return new AuthorizationDecision(true);

                        if (authorities.containsAll(UserRole.SOCIAL.getAuthorities()))
                            return new ErrorCodeAuthorizationResult(ErrorCode.NEED_ADDITIONAL_DATA);

                        throw new CustomException(ErrorCode.SIBAL_WHAT_IS_IT, "not social, admin, user, anonymous");
                    }
                    @Override
                    public AuthorizationDecision check(
                            Supplier<Authentication> authentication,
                            RequestAuthorizationContext object
                    ) {
                        throw new UnsupportedOperationException("use authorize()");
                    }
                })
                .requestMatchers("/admin/**").access(new AuthorizationManager<RequestAuthorizationContext>() {
                    @Override
                    public AuthorizationResult authorize(
                            Supplier<Authentication> authentication,
                            RequestAuthorizationContext object
                    ) {
                        Authentication auth = authentication.get();
                        if (auth == null || auth instanceof AnonymousAuthenticationToken)
                            return new ErrorCodeAuthorizationResult(ErrorCode.NO_AUTHENTICATED);

                        if (auth.getAuthorities().containsAll(UserRole.ADMIN.getAuthorities()))
                            return new AuthorizationDecision(true);

                        return new ErrorCodeAuthorizationResult(ErrorCode.NOT_ADMIN);
                    }

                    @Override
                    public AuthorizationDecision check(
                            Supplier<Authentication> authentication,
                            RequestAuthorizationContext object
                    ) {
                        throw new UnsupportedOperationException("use authorize()");
                    }
                })
                .anyRequest().denyAll() // 그 외 요청은 모두 거절
            )

            .oauth2Login(oauth2 -> oauth2
                .authorizedClientRepository(new NullAuthorizedClientRepository())
                .userInfoEndpoint(user -> user.userService(socialLoginService))
                .successHandler(oAuthSuccessHandler)
                .failureHandler(oAuthFailureHandler)
            )

            // userHistory -> access -> refresh -> logout
            .addFilterBefore(userHistoryFilter(), LogoutFilter.class) // 3 번째
            .addFilterBefore(refreshTokenFilter(), UserHistoryFilter.class) // 2 번쨰
            .addFilterBefore(accessTokenFilter(), RefreshTokenFilter.class) // 1 번째

            .exceptionHandling((exceptions) -> exceptions
                .authenticationEntryPoint((request, response, authenticationException)-> {
                    authenticationException.printStackTrace();
                    if(authenticationException instanceof InsufficientAuthenticationException)
                        // denyAll()
                        handlerExceptionResolver.resolveException(request, response, null, new CustomException(ErrorCode.NO_AUTHENTICATED));
                    else if(authenticationException instanceof CustomException)
                        handlerExceptionResolver.resolveException(request, response, null, (CustomException)authenticationException);
                    else
                        handlerExceptionResolver.resolveException(request, response, null, new CustomException(ErrorCode.SIBAL_WHAT_IS_IT, authenticationException.getMessage()));
                })
                .accessDeniedHandler((request, response, accessDeniedException)-> {
                    if(
                        accessDeniedException instanceof AuthorizationDeniedException
                        && ((AuthorizationDeniedException)accessDeniedException).getAuthorizationResult() instanceof ErrorCodeAuthorizationResult
                    ) {
                        ErrorCode errorCode = ((ErrorCodeAuthorizationResult) ((AuthorizationDeniedException)accessDeniedException).getAuthorizationResult()).getErrorCode();
                        handlerExceptionResolver.resolveException(request, response, null, new CustomException(errorCode));
                    } else
                        handlerExceptionResolver.resolveException(request, response, null, new CustomException(ErrorCode.SIBAL_WHAT_IS_IT, accessDeniedException.getMessage()));
                })
            )

            ;

        SecurityFilterChain filterChain = http.build();
        replaceAuthorizationManager(filterChain);

        return filterChain;
    }

    private void replaceAuthorizationManager(SecurityFilterChain chain) throws Exception {
        for (Filter filter : chain.getFilters()) {
            if (!(filter instanceof AuthorizationFilter authorizationFilter))
                continue;

            AuthorizationManager<HttpServletRequest> origin = authorizationFilter.getAuthorizationManager();
            Field mappingsField = origin.getClass().getDeclaredField("mappings");
            mappingsField.setAccessible(true);
            List<RequestMatcherEntry<AuthorizationManager<RequestAuthorizationContext>>> mappings =
                (List<RequestMatcherEntry<AuthorizationManager<RequestAuthorizationContext>>>) mappingsField.get(origin);

            Field managerField = AuthorizationFilter.class.getDeclaredField("authorizationManager");
            managerField.setAccessible(true);
            managerField.set(authorizationFilter, new DelegatingAuthorizationManager(mappings));
        }
    }

    @Bean
    public AccessTokenFilter accessTokenFilter() {
        return new AccessTokenFilter(jwtUtil);
    }
    @Bean
    public RefreshTokenFilter refreshTokenFilter() {
        return new RefreshTokenFilter(jwtUtil, userRepository);
    }
    @Bean
    public UserHistoryFilter userHistoryFilter() { return new UserHistoryFilter(userHistoryRepository, entityManager); }
}
