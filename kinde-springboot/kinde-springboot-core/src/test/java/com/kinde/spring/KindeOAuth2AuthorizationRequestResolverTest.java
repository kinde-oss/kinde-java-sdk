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
}
