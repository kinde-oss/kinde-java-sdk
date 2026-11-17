package com.kinde.spring;

import com.kinde.stepup.AcrValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReactiveKindeStepUpTest {

    private ReactiveKindeOAuth2AuthorizationRequestResolver resolver;

    @BeforeEach
    void setUp() {
        ClientRegistration registration = ClientRegistration.withRegistrationId("kinde")
                .clientId("test-client-id")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                .authorizationUri("https://example.kinde.com/oauth2/auth")
                .tokenUri("https://example.kinde.com/oauth2/token")
                .build();
        resolver = new ReactiveKindeOAuth2AuthorizationRequestResolver(
                new InMemoryReactiveClientRegistrationRepository(registration));
    }

    private static MockServerWebExchange exchange(String query) {
        return MockServerWebExchange.from(
                MockServerHttpRequest.get("http://localhost/oauth2/authorization/kinde" + query));
    }

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

    private static Mono<AuthorizationResult> authorize(
            ReactiveAuthorizationManager<AuthorizationContext> manager, Mono<Authentication> authentication) {
        return manager.authorize(authentication, new AuthorizationContext(exchange("")));
    }

    @Test
    @DisplayName("the reactive resolver keeps PKCE and forwards step-up parameters")
    void resolverForwardsStepUpParams() {
        OAuth2AuthorizationRequest request = resolver.resolve(exchange("?acr_values=MFA&max_age=0300")).block();

        assertNotNull(request);
        assertEquals("mfa", request.getAdditionalParameters().get("acr_values"));
        assertEquals("300", request.getAdditionalParameters().get("max_age"));
        assertNotNull(request.getAdditionalParameters().get("code_challenge"));
        assertTrue(request.getAuthorizationRequestUri().contains("acr_values=mfa"));
        assertTrue(request.getAuthorizationRequestUri().contains("max_age=300"));
    }

    @Test
    @DisplayName("the reactive resolver forwards invitation_code and connection_id")
    void resolverForwardsInvitationAndConnection() {
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("http://localhost/oauth2/authorization/kinde")
                        .queryParam("invitation_code", "inv_1")
                        .queryParam("connection_id", " conn_1 "));
        OAuth2AuthorizationRequest request = resolver.resolve(exchange).block();

        assertNotNull(request);
        assertEquals("inv_1", request.getAdditionalParameters().get("invitation_code"));
        assertEquals("true", request.getAdditionalParameters().get("is_invitation"));
        assertEquals("conn_1", request.getAdditionalParameters().get("connection_id"));
    }

    @Test
    @DisplayName("the reactive resolver adds nothing when no Kinde parameters are present")
    void resolverWithoutKindeParams() {
        OAuth2AuthorizationRequest request = resolver.resolve(exchange(""), "kinde").block();

        assertNotNull(request);
        assertNull(request.getAdditionalParameters().get("acr_values"));
        assertNull(request.getAdditionalParameters().get("max_age"));
        assertNull(request.getAdditionalParameters().get("invitation_code"));
        assertNull(request.getAdditionalParameters().get("connection_id"));
    }

    @Test
    @DisplayName("the reactive resolver rejects invalid step-up parameters with 400")
    void resolverRejectsInvalidStepUpParams() {
        ResponseStatusException unknownAcr = assertThrows(ResponseStatusException.class,
                () -> resolver.resolve(exchange("?acr_values=gold")).block());
        assertEquals(HttpStatus.BAD_REQUEST, unknownAcr.getStatusCode());
        assertThrows(ResponseStatusException.class,
                () -> resolver.resolve(exchange("?max_age=undefined")).block());
    }

    @Test
    @DisplayName("the reactive resolver ignores requests for other paths")
    void resolverIgnoresOtherPaths() {
        MockServerWebExchange other = MockServerWebExchange.from(
                MockServerHttpRequest.get("http://localhost/api/things?acr_values=mfa"));
        assertNull(resolver.resolve(other).block());
    }

    @Test
    @DisplayName("the reactive manager grants a token that meets the requirement")
    void managerGrants() {
        var manager = ReactiveKindeStepUp.requires(AcrValue.MFA, 300, true);
        AuthorizationResult result = authorize(manager,
                Mono.just(new JwtAuthenticationToken(jwt("phr", now() - 5), List.of()))).block();
        assertNotNull(result);
        assertTrue(result.isGranted());
    }

    @Test
    @DisplayName("the reactive manager rejects a weaker or older token with the step-up exception")
    void managerRejects() {
        var manager = ReactiveKindeStepUp.requires(AcrValue.MFA, 300, true);
        InsufficientUserAuthenticationException weak = assertThrows(
                InsufficientUserAuthenticationException.class,
                () -> authorize(manager, Mono.just(new JwtAuthenticationToken(jwt(null, now()), List.of()))).block());
        assertEquals(AcrValue.MFA, weak.getRequiredAcr());
        assertEquals(300, weak.getRequiredMaxAge());
        assertThrows(InsufficientUserAuthenticationException.class,
                () -> authorize(manager, Mono.just(new JwtAuthenticationToken(jwt("mfa", now() - 3600), List.of()))).block());
    }

    @Test
    @DisplayName("without step-up support the reactive manager does not trust auth_time")
    void managerDoesNotTrustAuthTimeWithoutSupport() {
        var manager = ReactiveKindeStepUp.requires(AcrValue.MFA, 300, false);
        assertThrows(InsufficientUserAuthenticationException.class,
                () -> authorize(manager, Mono.just(new JwtAuthenticationToken(jwt("mfa", now()), List.of()))).block());

        AuthorizationResult acrOnly = authorize(ReactiveKindeStepUp.requires(AcrValue.MFA, null, false),
                Mono.just(new JwtAuthenticationToken(jwt("mfa", now()), List.of()))).block();
        assertNotNull(acrOnly);
        assertTrue(acrOnly.isGranted());
    }

    @Test
    @DisplayName("the reactive manager denies an unauthenticated caller without a challenge")
    void managerDeniesUnauthenticated() {
        var manager = ReactiveKindeStepUp.requires(AcrValue.MFA, null, true);
        AuthorizationResult result = authorize(manager, Mono.empty()).block();
        assertNotNull(result);
        assertFalse(result.isGranted());
    }

    @Test
    @DisplayName("a reactive requirement must ask for something valid")
    void managerValidatesRequirement() {
        assertThrows(IllegalArgumentException.class, () -> ReactiveKindeStepUp.requires(null, null, true));
        assertThrows(IllegalArgumentException.class, () -> ReactiveKindeStepUp.requires(null, -1, true));
    }

    @Test
    @DisplayName("the reactive handler answers a step-up failure with the RFC 9470 challenge")
    void handlerSendsChallenge() {
        MockServerWebExchange exchange = exchange("");
        new KindeStepUpServerAccessDeniedHandler()
                .handle(exchange, new InsufficientUserAuthenticationException(AcrValue.PHR, 60)).block();

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
        assertEquals(
                "Bearer error=\"insufficient_user_authentication\", "
                        + "error_description=\"A different authentication level is required\", "
                        + "acr_values=\"phr\", max_age=\"60\"",
                exchange.getResponse().getHeaders().getFirst("WWW-Authenticate"));
    }

    @Test
    @DisplayName("the reactive handler leaves every other access denial to its delegate")
    void handlerDelegates() {
        MockServerWebExchange exchange = exchange("");
        boolean[] delegated = {false};
        new KindeStepUpServerAccessDeniedHandler((ex, denied) -> {
            delegated[0] = true;
            return Mono.empty();
        }).handle(exchange, new AccessDeniedException("no")).block();

        assertTrue(delegated[0]);
        assertNull(exchange.getResponse().getHeaders().getFirst("WWW-Authenticate"));
    }
}
