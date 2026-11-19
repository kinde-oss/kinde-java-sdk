package com.kinde.spring;

import com.kinde.KindeClient;
import com.kinde.KindeClientSession;
import com.kinde.spring.env.ReactiveWebApplicationDetection;
import com.kinde.spring.sdk.KindeSdkClient;
import com.kinde.user.UserInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReactiveStarterFixesTest {

    @Test
    @DisplayName("the reactive OAuth2 user service decorates the user with the Kinde client")
    void reactiveUserServiceUsesTheKindeClient() {
        UserInfo userInfo = mock(UserInfo.class);
        KindeClientSession session = mock(KindeClientSession.class);
        when(session.retrieveUserInfo()).thenReturn(userInfo);
        KindeClient kindeClient = mock(KindeClient.class);
        when(kindeClient.initClientSession(any(com.kinde.token.KindeToken.class))).thenReturn(session);
        KindeSdkClient kindeSdkClient = mock(KindeSdkClient.class);
        when(kindeSdkClient.getClient()).thenReturn(kindeClient);

        ClientRegistration registration = ClientRegistration.withRegistrationId("kinde")
                .clientId("client")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                .authorizationUri("https://example.kinde.com/oauth2/auth")
                .tokenUri("https://example.kinde.com/oauth2/token")
                .userInfoUri("https://example.kinde.com/oauth2/v2/user_profile")
                .userNameAttributeName("sub")
                .build();
        OAuth2AccessToken accessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER,
                "not-a-jwt", Instant.now(), Instant.now().plusSeconds(60));
        OAuth2UserRequest request = new OAuth2UserRequest(registration, accessToken);
        OAuth2User user = new DefaultOAuth2User(AuthorityUtils.NO_AUTHORITIES, Map.of("sub", "kp_1"), "sub");

        OAuth2User decorated = new ReactiveKindeOAuth2UserService(List.of(), kindeSdkClient).decorate(user, request);

        verify(kindeSdkClient).getClient();
        assertSame(userInfo, decorated.getAttributes().get("KindeUserInfo"));
        assertEquals("kp_1", decorated.getName());
    }

    @Test
    @DisplayName("a reactive application is detected from the property or the application type")
    void detectsReactiveApplications() {
        SpringApplication reactive = new SpringApplication();
        reactive.setWebApplicationType(WebApplicationType.REACTIVE);
        SpringApplication servlet = new SpringApplication();
        servlet.setWebApplicationType(WebApplicationType.SERVLET);

        assertTrue(ReactiveWebApplicationDetection.isReactive(new MockEnvironment(), reactive));
        assertFalse(ReactiveWebApplicationDetection.isReactive(new MockEnvironment(), servlet));
        assertFalse(ReactiveWebApplicationDetection.isReactive(new MockEnvironment(), null));

        // The property wins: it is applied to the SpringApplication only after post-processing
        MockEnvironment configuredReactive = new MockEnvironment()
                .withProperty("spring.main.web-application-type", "REACTIVE");
        assertTrue(ReactiveWebApplicationDetection.isReactive(configuredReactive, servlet));
        MockEnvironment configuredServlet = new MockEnvironment()
                .withProperty("spring.main.web-application-type", "servlet");
        assertFalse(ReactiveWebApplicationDetection.isReactive(configuredServlet, reactive));
    }

    @Test
    @DisplayName("the reactive OAuth2 user service never builds the Kinde client for another provider")
    void reactiveUserServiceSkipsOtherProviders() {
        KindeSdkClient kindeSdkClient = mock(KindeSdkClient.class);
        ClientRegistration google = ClientRegistration.withRegistrationId("google")
                .clientId("client")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                .authorizationUri("https://accounts.example.com/auth")
                .tokenUri("https://accounts.example.com/token")
                .userInfoUri("https://accounts.example.com/userinfo")
                .userNameAttributeName("sub")
                .build();
        OAuth2AccessToken accessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER,
                "token", Instant.now(), Instant.now().plusSeconds(60));
        OAuth2User user = new DefaultOAuth2User(AuthorityUtils.NO_AUTHORITIES, Map.of("sub", "g_1"), "sub");

        OAuth2User result = new ReactiveKindeOAuth2UserService(List.of(), kindeSdkClient)
                .decorate(user, new OAuth2UserRequest(google, accessToken));

        assertSame(user, result);
        org.mockito.Mockito.verifyNoInteractions(kindeSdkClient);
    }

    private static ClientRegistration registration(String id) {
        return ClientRegistration.withRegistrationId(id)
                .clientId("client")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                .authorizationUri("https://idp.example.com/auth")
                .tokenUri("https://idp.example.com/token")
                .userInfoUri("https://idp.example.com/userinfo")
                .userNameAttributeName("sub")
                .build();
    }

    /** A user service whose user-info call answers {"sub":"..."} without any network. */
    private static ReactiveKindeOAuth2UserService serviceAnswering(KindeSdkClient kindeSdkClient, String subject) {
        ReactiveKindeOAuth2UserService service = new ReactiveKindeOAuth2UserService(List.of(), kindeSdkClient);
        service.setWebClient(org.springframework.web.reactive.function.client.WebClient.builder()
                .exchangeFunction(request -> reactor.core.publisher.Mono.just(
                        org.springframework.web.reactive.function.client.ClientResponse
                                .create(org.springframework.http.HttpStatus.OK)
                                .header("Content-Type", "application/json")
                                .body("{\"sub\":\"" + subject + "\"}")
                                .build()))
                .build());
        return service;
    }

    private static OAuth2UserRequest request(String registrationId) {
        return new OAuth2UserRequest(registration(registrationId), new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER, "not-a-jwt", Instant.now(), Instant.now().plusSeconds(60)));
    }

    @Test
    @DisplayName("loadUser returns another provider's user untouched, without the Kinde client")
    void loadUserForAnotherProvider() {
        KindeSdkClient kindeSdkClient = mock(KindeSdkClient.class);

        OAuth2User user = serviceAnswering(kindeSdkClient, "g_1").loadUser(request("google")).block();

        assertNotNull(user);
        assertEquals("g_1", user.getName());
        assertFalse(user.getAttributes().containsKey("KindeUserInfo"));
        org.mockito.Mockito.verifyNoInteractions(kindeSdkClient);
    }

    @Test
    @DisplayName("loadUser decorates a Kinde user off the calling thread")
    void loadUserForKinde() {
        UserInfo userInfo = mock(UserInfo.class);
        KindeClientSession session = mock(KindeClientSession.class);
        String[] decoratedOn = new String[1];
        when(session.retrieveUserInfo()).thenAnswer(invocation -> {
            decoratedOn[0] = Thread.currentThread().getName();
            return userInfo;
        });
        KindeClient kindeClient = mock(KindeClient.class);
        when(kindeClient.initClientSession(any(com.kinde.token.KindeToken.class))).thenReturn(session);
        KindeSdkClient kindeSdkClient = mock(KindeSdkClient.class);
        when(kindeSdkClient.getClient()).thenReturn(kindeClient);

        OAuth2User user = serviceAnswering(kindeSdkClient, "kp_1").loadUser(request("kinde")).block();

        assertNotNull(user);
        assertEquals("kp_1", user.getName());
        assertSame(userInfo, user.getAttributes().get("KindeUserInfo"));
        assertTrue(decoratedOn[0].startsWith("boundedElastic"), "decorated on " + decoratedOn[0]);
    }

    @Test
    @DisplayName("loadUser propagates a decoration failure through the returned Mono")
    void loadUserPropagatesDecorationFailure() {
        KindeSdkClient kindeSdkClient = mock(KindeSdkClient.class);
        when(kindeSdkClient.getClient()).thenThrow(new IllegalStateException("discovery failed"));

        IllegalStateException failure = assertThrows(IllegalStateException.class,
                () -> serviceAnswering(kindeSdkClient, "kp_1").loadUser(request("kinde")).block());
        assertEquals("discovery failed", failure.getMessage());
    }
}
