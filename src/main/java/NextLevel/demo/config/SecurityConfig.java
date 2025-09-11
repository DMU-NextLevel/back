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
import NextLevel.demo.user.repository.UserHistoryRepository;
import NextLevel.demo.user.repository.UserRepository;
import NextLevel.demo.user.service.LoginService;
import NextLevel.demo.util.jwt.JWTUtil;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
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
                .requestMatchers("/login/**", "/public/**", "/payment/**", "/socket/**").permitAll()
                .requestMatchers("/api1/**").hasRole("USER")
                .requestMatchers("/social/**").hasRole("SOCIAL")
                .requestMatchers("/admin/**").hasRole("ADMIN")
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
                    handlerExceptionResolver.resolveException(request, response, null,
                        new CustomException(ErrorCode.NO_AUTHENTICATED));
                })
                .accessDeniedHandler((request, response, accessDeniedException)-> {
                    accessDeniedException.printStackTrace();
                    handlerExceptionResolver.resolveException(request, response, null, new CustomException(ErrorCode.NEED_ADDITIONAL_DATA));
                })
            )

            ;

        return http.build();
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
