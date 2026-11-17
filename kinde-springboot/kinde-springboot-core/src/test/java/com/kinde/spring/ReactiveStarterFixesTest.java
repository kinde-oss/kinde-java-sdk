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
}
