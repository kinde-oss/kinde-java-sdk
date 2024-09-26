package com.kinde.spring;

import com.kinde.spring.sdk.KindeSdkClient;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.List;

import static org.mockito.Mockito.when;

public class ReactiveKindeOidcUserServiceTest {

    @Test
    public void testLoadUser() {
        ReactiveOAuth2UserService reactiveOAuth2UserService = Mockito.mock(ReactiveOAuth2UserService.class);
        KindeSdkClient kindeSdkClient = Mockito.mock(KindeSdkClient.class);
        ReactiveKindeOidcUserService reactiveKindeOidcUserService = new ReactiveKindeOidcUserService(
                List.of(), reactiveOAuth2UserService, kindeSdkClient);

        OidcUserRequest userRequest = Mockito.mock(OidcUserRequest.class);
        ClientRegistration clientRegistration = Mockito.mock(ClientRegistration.class);
        when(userRequest.getClientRegistration()).thenReturn(clientRegistration);
        ClientRegistration.ProviderDetails providerDetails = Mockito.mock(ClientRegistration.ProviderDetails.class);
        when(clientRegistration.getProviderDetails()).thenReturn(providerDetails);
        ClientRegistration.ProviderDetails.UserInfoEndpoint userInfoEndpoint = Mockito.mock(ClientRegistration.ProviderDetails.UserInfoEndpoint.class);
        when(providerDetails.getUserInfoEndpoint()).thenReturn(userInfoEndpoint);
        reactiveKindeOidcUserService.loadUser(userRequest);
    }
}
