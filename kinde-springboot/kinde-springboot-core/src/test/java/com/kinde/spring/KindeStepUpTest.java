package com.kinde.spring;

import com.kinde.stepup.AcrValue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class KindeStepUpTest {

    private static Jwt jwt(String acr, Long authTime) {
        Jwt.Builder builder = Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .subject("kp_1")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(600));
        if (acr != null) {
            builder.claim("acr", acr);
        }
        if (authTime != null) {
            builder.claim("auth_time", authTime);
        }
        return builder.build();
    }

    private static long now() {
        return Instant.now().getEpochSecond();
    }

    private static AuthorizationResult authorize(
            AuthorizationManager<RequestAuthorizationContext> manager, Authentication authentication) {
        return manager.authorize(() -> authentication,
                new RequestAuthorizationContext(new MockHttpServletRequest()));
    }

    @Test
    @DisplayName("reads the authentication context from a verified access token")
    void contextFromJwt() {
        var context = KindeStepUp.authenticationContext(jwt("phr", 1_716_800_000L));
        assertEquals(Optional.of(AcrValue.PHR), context.getAcr());
        assertEquals(Optional.of(1_716_800_000L), context.getAuthTime());
        assertEquals(Optional.empty(), KindeStepUp.authenticationContext((Jwt) null).getAcr());
    }

    @Test
    @DisplayName("reads the authentication context from an OIDC user's ID token")
    void contextFromOidcUser() {
        OidcIdToken idToken = OidcIdToken.withTokenValue("id")
                .subject("kp_1")
                .claim("acr", "mfa")
                .claim("auth_time", now())
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(600))
                .build();
        OidcUser user = new DefaultOidcUser(AuthorityUtils.NO_AUTHORITIES, idToken);

        assertDoesNotThrow(() -> KindeStepUp.require(user, AcrValue.MFA, 300));
        assertThrows(InsufficientUserAuthenticationException.class,
                () -> KindeStepUp.require(user, AcrValue.PHR, null));
    }

    @Test
    @DisplayName("require passes when the token meets the requirement and throws otherwise")
    void requireOnJwt() {
        assertDoesNotThrow(() -> KindeStepUp.require(jwt("phr", now() - 10), AcrValue.MFA, 300));

        InsufficientUserAuthenticationException absent = assertThrows(
                InsufficientUserAuthenticationException.class,
                () -> KindeStepUp.require(jwt(null, now()), AcrValue.MFA, null));
        assertEquals(AcrValue.MFA, absent.getRequiredAcr());
        assertNull(absent.getRequiredMaxAge());

        assertThrows(InsufficientUserAuthenticationException.class,
                () -> KindeStepUp.require(jwt("mfa", now() - 3600), AcrValue.MFA, 300));
        assertThrows(InsufficientUserAuthenticationException.class,
                () -> KindeStepUp.require(jwt("mfa", null), AcrValue.MFA, 300));
    }

    @Test
    @DisplayName("the authorization manager grants a token that meets the requirement")
    void managerGrants() {
        AuthorizationManager<RequestAuthorizationContext> manager = KindeStepUp.requires(AcrValue.MFA, 300);
        AuthorizationResult result = authorize(manager, new JwtAuthenticationToken(jwt("mfa", now() - 5), List.of()));
        assertTrue(result.isGranted());
    }

    @Test
    @DisplayName("the authorization manager rejects a weaker token with the step-up exception")
    void managerRejectsWeakToken() {
        AuthorizationManager<RequestAuthorizationContext> manager = KindeStepUp.requires(AcrValue.PHR, null);
        InsufficientUserAuthenticationException ex = assertThrows(
                InsufficientUserAuthenticationException.class,
                () -> authorize(manager, new JwtAuthenticationToken(jwt("mfa", now()), List.of())));
        assertEquals(AcrValue.PHR, ex.getRequiredAcr());
    }

    @Test
    @DisplayName("the authorization manager denies an unauthenticated caller without a challenge")
    void managerDeniesUnauthenticated() {
        AuthorizationManager<RequestAuthorizationContext> manager = KindeStepUp.requires(AcrValue.MFA, null);
        TestingAuthenticationToken unauthenticated = new TestingAuthenticationToken("user", "pw");
        unauthenticated.setAuthenticated(false);
        assertFalse(authorize(manager, unauthenticated).isGranted());
        assertFalse(authorize(manager, null).isGranted());
    }

    @Test
    @DisplayName("a requirement must ask for something valid")
    void managerValidatesRequirement() {
        assertThrows(IllegalArgumentException.class, () -> KindeStepUp.requires(null, null));
        assertThrows(IllegalArgumentException.class, () -> KindeStepUp.requires(AcrValue.MFA, -1));
    }

    @Test
    @DisplayName("the handler answers a step-up failure with the RFC 9470 challenge")
    void handlerSendsChallenge() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        new KindeStepUpAccessDeniedHandler().handle(new MockHttpServletRequest(), response,
                new InsufficientUserAuthenticationException(AcrValue.MFA, 300));

        assertEquals(401, response.getStatus());
        assertEquals(
                "Bearer error=\"insufficient_user_authentication\", "
                        + "error_description=\"A different authentication level is required\", "
                        + "acr_values=\"mfa\", max_age=\"300\"",
                response.getHeader("WWW-Authenticate"));
    }

    @Test
    @DisplayName("the handler leaves every other access denial to its delegate")
    void handlerDelegates() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        boolean[] delegated = {false};
        new KindeStepUpAccessDeniedHandler((req, res, ex) -> delegated[0] = true)
                .handle(new MockHttpServletRequest(), response, new AccessDeniedException("no"));

        assertTrue(delegated[0]);
        assertNull(response.getHeader("WWW-Authenticate"));
    }
}
