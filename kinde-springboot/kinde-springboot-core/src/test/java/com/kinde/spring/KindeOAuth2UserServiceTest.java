package com.kinde.spring;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.kinde.guice.KindeEnvironmentSingleton;
import com.kinde.guice.KindeGuiceSingleton;
import com.kinde.spring.sdk.KindeSdkClient;
import com.kinde.spring.token.jwt.JwtGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.AuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

import java.util.Collection;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.mockito.Mockito.when;

public class KindeOAuth2UserServiceTest {

    private WireMockServer wireMockServer;

    @BeforeEach
    public void setUp() {
        wireMockServer = new WireMockServer(8089); // you can specify the port
        wireMockServer.start();
        WireMock.configureFor("localhost", 8089);

        wireMockServer.stubFor(
                WireMock.get(urlPathMatching("/oauth2/v2/user_profile"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody("""
                                  {
                                  "sub": "1234567890",
                                  "name": "John Doe",
                                  "given_name": "John",
                                  "family_name": "Doe",
                                  "email": "johndoe@example.com",
                                  "email_verified": true,
                                  "picture": "https://example.com/johndoe.jpg",
                                  "locale": "en-US",
                                  "updated_at": 1611693980
                                }
                """)));
    }

    @AfterEach
    public void tearDown() {
        wireMockServer.stop();
    }

    @Test
    public void testLoadUser() {
        Collection<AuthoritiesProvider> authoritiesProviders = List.of();
        KindeSdkClient kindeSdkClient = Mockito.mock(KindeSdkClient.class);
        KindeOAuth2UserService kindeOAuth2UserService = new KindeOAuth2UserService(authoritiesProviders,kindeSdkClient);
        OAuth2UserRequest userRequest = Mockito.mock(OAuth2UserRequest.class);
        ClientRegistration clientRegistration = Mockito.mock(ClientRegistration.class);
        when(userRequest.getClientRegistration()).thenReturn(clientRegistration);
        OAuth2AccessToken oAuth2AccessToken = Mockito.mock(OAuth2AccessToken.class);
        when(userRequest.getAccessToken()).thenReturn(oAuth2AccessToken);
        ClientRegistration.ProviderDetails providerDetails = Mockito.mock(ClientRegistration.ProviderDetails.class);
        when(clientRegistration.getProviderDetails()).thenReturn(providerDetails);
        ClientRegistration.ProviderDetails.UserInfoEndpoint userInfoEndpoint = Mockito.mock(ClientRegistration.ProviderDetails.UserInfoEndpoint.class);
        when(providerDetails.getUserInfoEndpoint()).thenReturn(userInfoEndpoint);
        when(userInfoEndpoint.getUri()).thenReturn("http://localhost:8089/oauth2/v2/user_profile");
        when(userInfoEndpoint.getUserNameAttributeName()).thenReturn("sub");
        when(userInfoEndpoint.getAuthenticationMethod()).thenReturn(AuthenticationMethod.QUERY);
        kindeOAuth2UserService.loadUser(userRequest);
    }


}
