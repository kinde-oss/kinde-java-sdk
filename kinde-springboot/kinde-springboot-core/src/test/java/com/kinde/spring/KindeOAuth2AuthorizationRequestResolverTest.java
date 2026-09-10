package com.kinde.spring;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import static org.junit.jupiter.api.Assertions.*;

class KindeOAuth2AuthorizationRequestResolverTest {

    private KindeOAuth2AuthorizationRequestResolver resolver;

    @BeforeEach
    void setUp() {
        ClientRegistration registration = ClientRegistration.withRegistrationId("kinde")
                .clientId("test-client-id")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                .authorizationUri("https://example.kinde.com/oauth2/auth")
                .tokenUri("https://example.kinde.com/oauth2/token")
                .build();
        ClientRegistrationRepository clientRegistrationRepository =
                new InMemoryClientRegistrationRepository(registration);
        resolver = new KindeOAuth2AuthorizationRequestResolver(
                clientRegistrationRepository, "/oauth2/authorization");
    }

    @Test
    @DisplayName("Resolve with invitation_code adds invitation params to authorization request")
    void resolveWithInvitationCodeAddsParams() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/oauth2/authorization/kinde");
        request.setServletPath("/oauth2/authorization/kinde");
        request.setParameter("invitation_code", "inv_abc123");

        OAuth2AuthorizationRequest authRequest = resolver.resolve(request);

        assertNotNull(authRequest);
        assertEquals("inv_abc123", authRequest.getAdditionalParameters().get("invitation_code"));
        assertEquals("true", authRequest.getAdditionalParameters().get("is_invitation"));

        String authUri = authRequest.getAuthorizationRequestUri();
        assertTrue(authUri.contains("invitation_code=inv_abc123"), "Redirect URI should contain invitation_code");
        assertTrue(authUri.contains("is_invitation=true"), "Redirect URI should contain is_invitation=true");
    }

    @Test
    @DisplayName("Resolve without invitation_code does not add invitation params")
    void resolveWithoutInvitationCodeDoesNotAddParams() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/oauth2/authorization/kinde");
        request.setServletPath("/oauth2/authorization/kinde");

        OAuth2AuthorizationRequest authRequest = resolver.resolve(request);

        assertNotNull(authRequest);
        assertNull(authRequest.getAdditionalParameters().get("invitation_code"));
        assertNull(authRequest.getAdditionalParameters().get("is_invitation"));
    }

    @Test
    @DisplayName("Resolve with empty invitation_code does not add invitation params")
    void resolveWithEmptyInvitationCodeDoesNotAddParams() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/oauth2/authorization/kinde");
        request.setServletPath("/oauth2/authorization/kinde");
        request.setParameter("invitation_code", "");

        OAuth2AuthorizationRequest authRequest = resolver.resolve(request);

        assertNotNull(authRequest);
        assertNull(authRequest.getAdditionalParameters().get("invitation_code"));
        assertNull(authRequest.getAdditionalParameters().get("is_invitation"));
    }

    @Test
    @DisplayName("Resolve with whitespace-only invitation_code does not add invitation params")
    void resolveWithWhitespaceOnlyInvitationCodeDoesNotAddParams() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/oauth2/authorization/kinde");
        request.setServletPath("/oauth2/authorization/kinde");
        request.setParameter("invitation_code", "   ");

        OAuth2AuthorizationRequest authRequest = resolver.resolve(request);

        assertNotNull(authRequest);
        assertNull(authRequest.getAdditionalParameters().get("invitation_code"));
        assertNull(authRequest.getAdditionalParameters().get("is_invitation"));
    }

    @Test
    @DisplayName("Resolve returns null for non-authorization request path")
    void resolveReturnsNullForNonAuthorizationPath() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/some/other/path");
        request.setServletPath("/some/other/path");
        request.setParameter("invitation_code", "inv_abc123");

        OAuth2AuthorizationRequest authRequest = resolver.resolve(request);

        assertNull(authRequest);
    }

    @Test
    @DisplayName("Resolve with clientRegistrationId and invitation_code adds params")
    void resolveWithClientRegistrationIdAndInvitationCodeAddsParams() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/oauth2/authorization/kinde");
        request.setServletPath("/oauth2/authorization/kinde");
        request.setParameter("invitation_code", "inv_client_reg");

        OAuth2AuthorizationRequest authRequest = resolver.resolve(request, "kinde");

        assertNotNull(authRequest);
        assertEquals("inv_client_reg", authRequest.getAdditionalParameters().get("invitation_code"));
        assertEquals("true", authRequest.getAdditionalParameters().get("is_invitation"));
    }

    @Test
    @DisplayName("Resolve with clientRegistrationId without invitation_code does not add params")
    void resolveWithClientRegistrationIdWithoutInvitationCodeDoesNotAddParams() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/oauth2/authorization/kinde");
        request.setServletPath("/oauth2/authorization/kinde");

        OAuth2AuthorizationRequest authRequest = resolver.resolve(request, "kinde");

        assertNotNull(authRequest);
        assertNull(authRequest.getAdditionalParameters().get("invitation_code"));
        assertNull(authRequest.getAdditionalParameters().get("is_invitation"));
    }

    @Test
    @DisplayName("Resolve with clientRegistrationId and empty invitation_code does not add params")
    void resolveWithClientRegistrationIdWithEmptyInvitationCodeDoesNotAddParams() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/oauth2/authorization/kinde");
        request.setServletPath("/oauth2/authorization/kinde");
        request.setParameter("invitation_code", "");

        OAuth2AuthorizationRequest authRequest = resolver.resolve(request, "kinde");

        assertNotNull(authRequest);
        assertNull(authRequest.getAdditionalParameters().get("invitation_code"));
        assertNull(authRequest.getAdditionalParameters().get("is_invitation"));
    }

    @Test
    @DisplayName("Resolve with clientRegistrationId and whitespace-only invitation_code does not add params")
    void resolveWithClientRegistrationIdWithWhitespaceOnlyInvitationCodeDoesNotAddParams() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/oauth2/authorization/kinde");
        request.setServletPath("/oauth2/authorization/kinde");
        request.setParameter("invitation_code", "   ");

        OAuth2AuthorizationRequest authRequest = resolver.resolve(request, "kinde");

        assertNotNull(authRequest);
        assertNull(authRequest.getAdditionalParameters().get("invitation_code"));
        assertNull(authRequest.getAdditionalParameters().get("is_invitation"));
    }

    @Test
    @DisplayName("Resolve with connection_id adds connection_id to authorization request")
    void resolveWithConnectionIdAddsParam() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/oauth2/authorization/kinde");
        request.setServletPath("/oauth2/authorization/kinde");
        request.setParameter("connection_id", "conn_abc123");

        OAuth2AuthorizationRequest authRequest = resolver.resolve(request);

        assertNotNull(authRequest);
        assertEquals("conn_abc123", authRequest.getAdditionalParameters().get("connection_id"));
        assertNull(authRequest.getAdditionalParameters().get("invitation_code"));

        String authUri = authRequest.getAuthorizationRequestUri();
        assertTrue(authUri.contains("connection_id=conn_abc123"), "Redirect URI should contain connection_id");
    }

    @Test
    @DisplayName("Resolve with invitation_code and connection_id adds both params")
    void resolveWithInvitationCodeAndConnectionIdAddsBothParams() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/oauth2/authorization/kinde");
        request.setServletPath("/oauth2/authorization/kinde");
        request.setParameter("invitation_code", "inv_abc123");
        request.setParameter("connection_id", "conn_abc123");

        OAuth2AuthorizationRequest authRequest = resolver.resolve(request);

        assertNotNull(authRequest);
        assertEquals("inv_abc123", authRequest.getAdditionalParameters().get("invitation_code"));
        assertEquals("true", authRequest.getAdditionalParameters().get("is_invitation"));
        assertEquals("conn_abc123", authRequest.getAdditionalParameters().get("connection_id"));

        String authUri = authRequest.getAuthorizationRequestUri();
        assertTrue(authUri.contains("invitation_code=inv_abc123"));
        assertTrue(authUri.contains("connection_id=conn_abc123"));
    }

    @Test
    @DisplayName("Resolve with empty connection_id does not add connection_id")
    void resolveWithEmptyConnectionIdDoesNotAddParam() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/oauth2/authorization/kinde");
        request.setServletPath("/oauth2/authorization/kinde");
        request.setParameter("connection_id", "");

        OAuth2AuthorizationRequest authRequest = resolver.resolve(request);

        assertNotNull(authRequest);
        assertNull(authRequest.getAdditionalParameters().get("connection_id"));
    }

    @Test
    @DisplayName("Resolve with whitespace-only connection_id does not add connection_id")
    void resolveWithWhitespaceOnlyConnectionIdDoesNotAddParam() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/oauth2/authorization/kinde");
        request.setServletPath("/oauth2/authorization/kinde");
        request.setParameter("connection_id", "   ");

        OAuth2AuthorizationRequest authRequest = resolver.resolve(request);

        assertNotNull(authRequest);
        assertNull(authRequest.getAdditionalParameters().get("connection_id"));
    }

    @Test
    @DisplayName("Resolve trims connection_id before adding it")
    void resolveTrimsConnectionId() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/oauth2/authorization/kinde");
        request.setServletPath("/oauth2/authorization/kinde");
        request.setParameter("connection_id", "  conn_abc123  ");

        OAuth2AuthorizationRequest authRequest = resolver.resolve(request);

        assertNotNull(authRequest);
        assertEquals("conn_abc123", authRequest.getAdditionalParameters().get("connection_id"));
    }

    @Test
    @DisplayName("Resolve with clientRegistrationId and connection_id adds param")
    void resolveWithClientRegistrationIdAndConnectionIdAddsParam() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/oauth2/authorization/kinde");
        request.setServletPath("/oauth2/authorization/kinde");
        request.setParameter("connection_id", "conn_client_reg");

        OAuth2AuthorizationRequest authRequest = resolver.resolve(request, "kinde");

        assertNotNull(authRequest);
        assertEquals("conn_client_reg", authRequest.getAdditionalParameters().get("connection_id"));
    }
}
