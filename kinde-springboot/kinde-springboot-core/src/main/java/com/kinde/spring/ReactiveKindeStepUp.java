package com.kinde.spring;

import com.kinde.stepup.AcrValue;
import com.kinde.stepup.StepUp;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import reactor.core.publisher.Mono;

/**
 * Step-up authentication (RFC 9470) checks for reactive (WebFlux) Spring Security. The
 * servlet counterpart is {@link KindeStepUp}, whose {@code authenticationContext(...)} and
 * {@code require(...)} methods work in reactive code too.
 *
 * <pre>{@code
 * http.authorizeExchange(exchanges -> exchanges
 *         .pathMatchers("/api/transfers/**").access(ReactiveKindeStepUp.requires(AcrValue.MFA, 300))
 *         .anyExchange().authenticated());
 * }</pre>
 *
 * <p>When the check fails on a resource server configured by the Kinde starter, the response
 * is the RFC 9470 challenge (see {@link KindeStepUpServerAccessDeniedHandler}).
 */
public final class ReactiveKindeStepUp {

    private ReactiveKindeStepUp() {
    }

    /**
     * An authorization manager for {@code authorizeExchange(...).access(...)} that requires an
     * authenticated caller with the given authentication context.
     *
     * <p>An unauthenticated caller is denied in the usual way. An authenticated caller whose
     * context is too weak or too old is rejected with an
     * {@link InsufficientUserAuthenticationException}.
     *
     * @param requiredAcr required context, or null. {@link AcrValue#PHR} also meets {@link AcrValue#MFA}.
     * @param maxAge maximum age of the last authentication in seconds, or null
     * @throws IllegalArgumentException if neither a context nor a maximum age is given, or
     *         {@code maxAge} is negative
     */
    public static ReactiveAuthorizationManager<AuthorizationContext> requires(AcrValue requiredAcr, Integer maxAge) {
        if (requiredAcr == null && maxAge == null) {
            throw new IllegalArgumentException("A step-up requirement needs an acr value, a max age, or both");
        }
        if (maxAge != null) {
            StepUp.normalizeMaxAge(maxAge);
        }
        return (authentication, context) -> authentication
                .filter(Authentication::isAuthenticated)
                .<AuthorizationResult>map(current -> {
                    if (!KindeStepUp.authenticationContext(current).satisfies(requiredAcr, maxAge)) {
                        throw new InsufficientUserAuthenticationException(requiredAcr, maxAge);
                    }
                    return new AuthorizationDecision(true);
                })
                .defaultIfEmpty(new AuthorizationDecision(false));
    }
}
