package com.kinde.spring;

import com.kinde.stepup.AcrValue;
import com.kinde.stepup.AuthenticationContext;
import com.kinde.stepup.StepUp;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

import java.util.Map;

/**
 * Step-up authentication (RFC 9470) checks for Spring Security.
 *
 * <p>Protect an API route so that it needs MFA no older than five minutes:
 *
 * <pre>{@code
 * http.authorizeHttpRequests(requests -> requests
 *         .requestMatchers("/api/transfers/**").access(KindeStepUp.requires(AcrValue.MFA, 300))
 *         .anyRequest().authenticated());
 * }</pre>
 *
 * <p>or check inside a controller with {@link #require(Jwt, AcrValue, Integer)}. Both read
 * the {@code acr} and {@code auth_time} claims of the token Spring Security has already
 * verified, on every request. When the check fails on a resource server configured by the
 * Kinde starter, the response is the RFC 9470 challenge (see
 * {@link KindeStepUpAccessDeniedHandler}).
 */
public final class KindeStepUp {

    private KindeStepUp() {
    }

    /** How the caller authenticated, from a verified access token. */
    public static AuthenticationContext authenticationContext(Jwt jwt) {
        return AuthenticationContext.fromClaims(null, jwt == null ? null : jwt.getClaims(), true);
    }

    /** How the signed-in user authenticated, from their ID token. */
    public static AuthenticationContext authenticationContext(OidcUser user) {
        Map<String, Object> claims = user == null || user.getIdToken() == null ? null : user.getIdToken().getClaims();
        return AuthenticationContext.fromClaims(claims, null, true);
    }

    /**
     * How the current authentication was performed. Supports JWT resource-server
     * authentications and OIDC logins; anything else gives an empty context.
     */
    public static AuthenticationContext authenticationContext(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return AuthenticationContext.empty();
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof Jwt) {
            return authenticationContext((Jwt) principal);
        }
        if (principal instanceof OidcUser) {
            return authenticationContext((OidcUser) principal);
        }
        return AuthenticationContext.empty();
    }

    /**
     * Requires the access token to carry the given authentication context.
     *
     * @param requiredAcr required context, or null. {@link AcrValue#PHR} also meets {@link AcrValue#MFA}.
     * @param maxAge maximum age of the last authentication in seconds, or null
     * @throws InsufficientUserAuthenticationException if the requirement is not met
     */
    public static void require(Jwt jwt, AcrValue requiredAcr, Integer maxAge) {
        require(authenticationContext(jwt), requiredAcr, maxAge);
    }

    /**
     * Requires the signed-in user to have authenticated with the given context.
     *
     * @throws InsufficientUserAuthenticationException if the requirement is not met
     */
    public static void require(OidcUser user, AcrValue requiredAcr, Integer maxAge) {
        require(authenticationContext(user), requiredAcr, maxAge);
    }

    private static void require(AuthenticationContext context, AcrValue requiredAcr, Integer maxAge) {
        if (!context.satisfies(requiredAcr, maxAge)) {
            throw new InsufficientUserAuthenticationException(requiredAcr, maxAge);
        }
    }

    /**
     * An authorization manager for {@code authorizeHttpRequests(...).access(...)} that requires
     * an authenticated caller with the given authentication context.
     *
     * <p>An unauthenticated caller is denied in the usual way. An authenticated caller whose
     * context is too weak or too old is rejected with an
     * {@link InsufficientUserAuthenticationException}.
     *
     * @throws IllegalArgumentException if neither a context nor a maximum age is given, or
     *         {@code maxAge} is negative
     */
    public static AuthorizationManager<RequestAuthorizationContext> requires(AcrValue requiredAcr, Integer maxAge) {
        if (requiredAcr == null && maxAge == null) {
            throw new IllegalArgumentException("A step-up requirement needs an acr value, a max age, or both");
        }
        if (maxAge != null) {
            StepUp.normalizeMaxAge(maxAge);
        }
        return (authentication, context) -> {
            Authentication current = authentication.get();
            if (current == null || !current.isAuthenticated()) {
                return new AuthorizationDecision(false);
            }
            require(authenticationContext(current), requiredAcr, maxAge);
            return new AuthorizationDecision(true);
        };
    }
}
