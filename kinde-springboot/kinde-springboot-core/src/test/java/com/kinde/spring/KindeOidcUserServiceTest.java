package com.kinde.spring;

import com.kinde.spring.sdk.KindeSdkClient;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.AuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;

public class KindeOidcUserServiceTest {

    @Test
    public void testLoadUser() {
        OAuth2UserService oauth2UserService = Mockito.mock(OAuth2UserService.class);
        Collection<AuthoritiesProvider> authoritiesProviders = List.of();
        KindeSdkClient kindeSdkClient = Mockito.mock(KindeSdkClient.class);

        KindeOidcUserService kindeOidcUserService = new KindeOidcUserService(oauth2UserService,authoritiesProviders,kindeSdkClient);

        OidcUserRequest oidcUserRequest = Mockito.mock(OidcUserRequest.class);

        ClientRegistration clientRegistration = Mockito.mock(ClientRegistration.class);
        when(oidcUserRequest.getClientRegistration()).thenReturn(clientRegistration);
        OidcIdToken oidcIdToken = Mockito.mock(OidcIdToken.class);
        when(oidcUserRequest.getIdToken()).thenReturn(oidcIdToken);
        OAuth2AccessToken oAuth2AccessToken = Mockito.mock(OAuth2AccessToken.class);
        when(oidcUserRequest.getAccessToken()).thenReturn(oAuth2AccessToken);
        Map<String, Object> claims = new HashMap<>();
        claims.put("permissions",List.of("test1","test2"));
        claims.put("sub","test");
        when(oidcIdToken.getClaims()).thenReturn(claims);

        ClientRegistration.ProviderDetails providerDetails = Mockito.mock(ClientRegistration.ProviderDetails.class);
        when(clientRegistration.getProviderDetails()).thenReturn(providerDetails);
        ClientRegistration.ProviderDetails.UserInfoEndpoint userInfoEndpoint = Mockito.mock(ClientRegistration.ProviderDetails.UserInfoEndpoint.class);
        when(providerDetails.getUserInfoEndpoint()).thenReturn(userInfoEndpoint);
        when(userInfoEndpoint.getUri()).thenReturn("http://localhost:8089/oauth2/v2/user_profile");
        when(userInfoEndpoint.getUserNameAttributeName()).thenReturn("sub");
        when(userInfoEndpoint.getAuthenticationMethod()).thenReturn(AuthenticationMethod.QUERY);
        kindeOidcUserService.loadUser(oidcUserRequest);

    }
}
