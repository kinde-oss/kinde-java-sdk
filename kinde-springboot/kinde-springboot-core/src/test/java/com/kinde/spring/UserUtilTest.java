package com.kinde.spring;

import com.kinde.KindeClient;
import com.kinde.KindeClientSession;
import com.kinde.spring.token.jwt.JwtGenerator;
import com.kinde.user.UserInfo;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserUtilTest {

    @Test
    public void testDecorateUser() throws Exception {
        OAuth2User oAuth2User = Mockito.mock(OAuth2User.class);
        OAuth2UserRequest oAuth2UserRequest = Mockito.mock(OAuth2UserRequest.class);
        OAuth2AccessToken oAuth2AccessToken = Mockito.mock(OAuth2AccessToken.class);
        ClientRegistration clientRegistration = Mockito.mock(ClientRegistration.class);
        ClientRegistration.ProviderDetails providerDetails =  Mockito.mock(ClientRegistration.ProviderDetails.class);
        ClientRegistration.ProviderDetails.UserInfoEndpoint userInfoEndpoint = Mockito.mock(ClientRegistration.ProviderDetails.UserInfoEndpoint.class);
        Collection<AuthoritiesProvider> authoritiesProviderCollection = List.of();
        KindeClient kindeClient = Mockito.mock(KindeClient.class);
        KindeClientSession kindeClientSession = Mockito.mock(KindeClientSession.class);
        UserInfo userInfo = Mockito.mock(UserInfo.class);


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
        when(kindeClientSession.retrieveUserInfo()).thenReturn(userInfo);


        // test false route
        UserUtil.decorateUser(oAuth2User, oAuth2UserRequest, authoritiesProviderCollection, kindeClient);

        when(clientRegistration.getRegistrationId())
                .thenReturn("kinde");
        UserUtil.decorateUser(oAuth2User, oAuth2UserRequest, authoritiesProviderCollection, kindeClient);

    }

    @Test
    public void testDecorateOidcUser() throws Exception {
        OidcUser oidcUser = Mockito.mock(OidcUser.class);
        OidcIdToken oidcIdToken = Mockito.mock(OidcIdToken.class);
        OidcUserRequest oidcUserRequest = Mockito.mock(OidcUserRequest.class);
        OAuth2AccessToken oAuth2AccessToken = Mockito.mock(OAuth2AccessToken.class);
        ClientRegistration clientRegistration = Mockito.mock(ClientRegistration.class);
        ClientRegistration.ProviderDetails providerDetails =  Mockito.mock(ClientRegistration.ProviderDetails.class);
        ClientRegistration.ProviderDetails.UserInfoEndpoint userInfoEndpoint = Mockito.mock(ClientRegistration.ProviderDetails.UserInfoEndpoint.class);
        Collection<AuthoritiesProvider> authoritiesProviderCollection = List.of();
        KindeClient kindeClient = Mockito.mock(KindeClient.class);
        KindeClientSession kindeClientSession = Mockito.mock(KindeClientSession.class);
        OidcUserInfo oidcUserInfo = Mockito.mock(OidcUserInfo.class);

        when(oidcUserRequest.getClientRegistration())
                .thenReturn(clientRegistration);
        when(oidcUserRequest.getAccessToken())
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
        when(oidcUser.getIdToken())
                .thenReturn(oidcIdToken);
        HashMap<String,Object> claims = new HashMap<>();
        claims.put("KindeUserInfo",oidcUserInfo);
        when(oidcIdToken.getClaims())
                .thenReturn(claims);
        when(oidcUser.getUserInfo())
                .thenReturn(oidcUserInfo);
        when(oidcUser.getAuthorities())
                .thenReturn((Collection)List.of(new SimpleGrantedAuthority("test")));
        when(kindeClient.initClientSession(any()))
                .thenReturn(kindeClientSession);


        // test false route
        UserUtil.decorateUser(oidcUser, oidcUserRequest, authoritiesProviderCollection, kindeClient);

        when(clientRegistration.getRegistrationId())
                .thenReturn("kinde");
        UserUtil.decorateUser(oidcUser, oidcUserRequest, authoritiesProviderCollection, kindeClient);

    }
}
