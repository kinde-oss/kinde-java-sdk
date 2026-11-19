package com.kinde.spring;

import com.kinde.stepup.AcrValue;
import com.kinde.stepup.AuthenticationContext;
import com.kinde.stepup.StepUp;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

import java.util.Collection;
import java.util.Map;

/**
 * Step-up authentication (RFC 9470) checks for Spring Security.
 *
 * <p>Protect an API route so that it needs MFA no older than five minutes:
 *
 * <pre>{@code
 * boolean stepUpSupported = KindeStepUp.isStepUpSupported(
 *         clientRegistrationRepository.findByRegistrationId("kinde"));
 * http.authorizeHttpRequests(requests -> requests
 *         .requestMatchers("/api/transfers/**").access(KindeStepUp.requires(AcrValue.MFA, 300, stepUpSupported))
 *         .anyRequest().authenticated());
 * }</pre>
 *
 * <p>or check inside a controller with {@link #require(Jwt, AcrValue, Integer, boolean)}. Both
 * read the {@code acr} and {@code auth_time} claims of the token Spring Security has already
 * verified, on every request. When the check fails on a resource server configured by the
 * Kinde starter, the response is the RFC 9470 challenge (see
 * {@link KindeStepUpAccessDeniedHandler}).
 *
 * <p>Every check takes {@code stepUpSupported}: whether the Kinde environment's discovery
 * document lists {@code acr_values_supported}. Without step-up support {@code auth_time} may
 * be the token's issue time instead of the last active authentication, so it is not trusted
 * and no {@code maxAge} requirement is met.
 */
public final class KindeStepUp {

    private KindeStepUp() {
    }

    /**
     * Whether the Kinde environment of a client registration supports step-up, read from the
     * discovery document Spring resolved for its issuer. A registration without discovery
     * metadata (for example one with hand-written endpoints) gives false.
     */
    public static boolean isStepUpSupported(ClientRegistration registration) {
        if (registration == null) {
            return false;
        }
        Object acrs = registration.getProviderDetails().getConfigurationMetadata().get("acr_values_supported");
        return acrs instanceof Collection && !((Collection<?>) acrs).isEmpty();
    }

    /** How the caller authenticated, from a verified access token. */
    public static AuthenticationContext authenticationContext(Jwt jwt, boolean stepUpSupported) {
        return AuthenticationContext.fromClaims(null, jwt == null ? null : jwt.getClaims(), stepUpSupported);
    }

    /** How the signed-in user authenticated, from their ID token. */
    public static AuthenticationContext authenticationContext(OidcUser user, boolean stepUpSupported) {
        Map<String, Object> claims = user == null || user.getIdToken() == null ? null : user.getIdToken().getClaims();
        return AuthenticationContext.fromClaims(claims, null, stepUpSupported);
    }

    /**
     * How the current authentication was performed. Supports JWT and opaque-token
     * (introspection) resource-server authentications and OIDC logins; anything else gives
     * an empty context.
     */
    public static AuthenticationContext authenticationContext(Authentication authentication, boolean stepUpSupported) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return AuthenticationContext.empty();
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof Jwt) {
            return authenticationContext((Jwt) principal, stepUpSupported);
        }
        if (principal instanceof OidcUser) {
            return authenticationContext((OidcUser) principal, stepUpSupported);
        }
        if (authentication instanceof BearerTokenAuthentication) {
            return AuthenticationContext.fromClaims(
                    null, ((BearerTokenAuthentication) authentication).getTokenAttributes(), stepUpSupported);
        }
        return AuthenticationContext.empty();
    }

    /**
     * Requires the access token to carry the given authentication context.
     *
     * @param requiredAcr required context, or null. {@link AcrValue#PHR} also meets {@link AcrValue#MFA}.
     * @param maxAge maximum age of the last authentication in seconds, or null
     * @param stepUpSupported whether the environment advertises step-up (see {@link #isStepUpSupported})
     * @throws InsufficientUserAuthenticationException if the requirement is not met
     */
    public static void require(Jwt jwt, AcrValue requiredAcr, Integer maxAge, boolean stepUpSupported) {
        require(authenticationContext(jwt, stepUpSupported), requiredAcr, maxAge);
    }

    /**
     * Requires the signed-in user to have authenticated with the given context.
     *
     * @param stepUpSupported whether the environment advertises step-up (see {@link #isStepUpSupported})
     * @throws InsufficientUserAuthenticationException if the requirement is not met
     */
    public static void require(OidcUser user, AcrValue requiredAcr, Integer maxAge, boolean stepUpSupported) {
        require(authenticationContext(user, stepUpSupported), requiredAcr, maxAge);
    }

    static void require(AuthenticationContext context, AcrValue requiredAcr, Integer maxAge) {
        if (!context.satisfies(requiredAcr, maxAge)) {
            throw new InsufficientUserAuthenticationException(requiredAcr, maxAge);
        }
    }

    static void validateRequirement(AcrValue requiredAcr, Integer maxAge) {
        if (requiredAcr == null && maxAge == null) {
            throw new IllegalArgumentException("A step-up requirement needs an acr value, a max age, or both");
        }
        if (maxAge != null) {
            StepUp.normalizeMaxAge(maxAge);
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
     * @param stepUpSupported whether the environment advertises step-up (see {@link #isStepUpSupported})
     * @throws IllegalArgumentException if neither a context nor a maximum age is given, or
     *         {@code maxAge} is negative
     */
    public static AuthorizationManager<RequestAuthorizationContext> requires(
            AcrValue requiredAcr, Integer maxAge, boolean stepUpSupported) {
        validateRequirement(requiredAcr, maxAge);
        return (authentication, context) -> {
            Authentication current = authentication.get();
            if (current == null || !current.isAuthenticated()) {
                return new AuthorizationDecision(false);
            }
            require(authenticationContext(current, stepUpSupported), requiredAcr, maxAge);
            return new AuthorizationDecision(true);
        };
    }
}
