package com.kinde.spring;

import com.kinde.KindeClient;
import com.kinde.KindeClientSession;
import com.kinde.spring.token.jwt.JwtGenerator;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class UserUtilTest {

    @Test
    public void testWebClientHeaders() throws Exception {
        OAuth2User oAuth2User = Mockito.mock(OAuth2User.class);
        OAuth2UserRequest oAuth2UserRequest = Mockito.mock(OAuth2UserRequest.class);
        OAuth2AccessToken oAuth2AccessToken = Mockito.mock(OAuth2AccessToken.class);
        ClientRegistration clientRegistration = Mockito.mock(ClientRegistration.class);
        ClientRegistration.ProviderDetails providerDetails =  Mockito.mock(ClientRegistration.ProviderDetails.class);
        ClientRegistration.ProviderDetails.UserInfoEndpoint userInfoEndpoint = Mockito.mock(ClientRegistration.ProviderDetails.UserInfoEndpoint.class);
        Collection<AuthoritiesProvider> authoritiesProviderCollection = List.of();
        KindeClient kindeClient = Mockito.mock(KindeClient.class);
        KindeClientSession kindeClientSession = Mockito.mock(KindeClientSession.class);

        when(oAuth2UserRequest.getClientRegistration())
                .thenReturn(clientRegistration);
        when(oAuth2UserRequest.getAccessToken())
                .thenReturn(oAuth2AccessToken);
        when(clientRegistration.getRegistrationId())
                .thenReturn("kinde1");
        when(providerDetails.getUserInfoEndpoint())
                .thenReturn(userInfoEndpoint);
        when(userInfoEndpoint.getUserNameAttributeName())
                .thenReturn("KindeUserInfo");
        when(clientRegistration.getProviderDetails())
                .thenReturn(providerDetails);
        when(oAuth2AccessToken.getTokenValue())
                .thenReturn(JwtGenerator.generateAccessToken());
        when(kindeClient.initClientSession(any()))
                .thenReturn(kindeClientSession);


        // test false route
        UserUtil.decorateUser(oAuth2User, oAuth2UserRequest, authoritiesProviderCollection, kindeClient);

        when(clientRegistration.getRegistrationId())
                .thenReturn("kinde");
        UserUtil.decorateUser(oAuth2User, oAuth2UserRequest, authoritiesProviderCollection, kindeClient);

    }

}
