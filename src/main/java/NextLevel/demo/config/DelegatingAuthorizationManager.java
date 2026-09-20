package NextLevel.demo.config;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.function.Supplier;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.util.matcher.RequestMatcher.MatchResult;
import org.springframework.security.web.util.matcher.RequestMatcherEntry;

public class DelegatingAuthorizationManager implements AuthorizationManager<HttpServletRequest> {

    private final List<RequestMatcherEntry<AuthorizationManager<RequestAuthorizationContext>>> mappings;

    public DelegatingAuthorizationManager(
            List<RequestMatcherEntry<AuthorizationManager<RequestAuthorizationContext>>> mappings
    ) {
        this.mappings = mappings;
    }

    @Override
    public AuthorizationResult authorize(Supplier<Authentication> authentication, HttpServletRequest request) {
        for (RequestMatcherEntry<AuthorizationManager<RequestAuthorizationContext>> mapping : this.mappings) {
            MatchResult matchResult = mapping.getRequestMatcher().matcher(request);

            if (matchResult.isMatch()) {
                return mapping.getEntry().authorize(
                        authentication,
                        new RequestAuthorizationContext(request, matchResult.getVariables())
                );
            }
        }
        return new AuthorizationDecision(false);
    }

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, HttpServletRequest request) {
        throw new UnsupportedOperationException("use authorize()");
    }
}
