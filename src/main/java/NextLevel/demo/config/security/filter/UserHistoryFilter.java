package NextLevel.demo.config.security.filter;

import NextLevel.demo.config.security.CustomAuthentication;
import NextLevel.demo.user.entity.UserEntity;
import NextLevel.demo.user.entity.UserHistoryEntity;
import NextLevel.demo.user.repository.UserHistoryRepository;
import jakarta.persistence.EntityManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
@RequiredArgsConstructor
public class UserHistoryFilter extends CustomTokenFilter {

    private final UserHistoryRepository userHistoryRepository;
    private final EntityManager entityManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication != null && authentication instanceof CustomAuthentication){

            log.info("user history Filter :: "
                    + "authorities = [" + authentication.getAuthorities()+"] "
                    + "ip = ["+getIpFromRequest(request)+"] "
                    + "userId = ["+authentication.getPrincipal()+"] "
                    + "uri = [" + request.getRequestURI()+"] ");

            userHistoryRepository.save(UserHistoryEntity
                .builder()
                .user(entityManager.getReference(UserEntity.class, (Long)authentication.getPrincipal()))
                .date(new Date())
                .ip(getIpFromRequest(request))
                .agent(getAgent(request))
                .uri(request.getRequestURI())
                .build()
            );
        }

        filterChain.doFilter(request, response);
    }
}
