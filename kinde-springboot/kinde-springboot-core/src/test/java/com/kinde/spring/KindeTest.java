package com.kinde.spring;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class KindeTest {

    @Test
    public void testConfigureOAuth2WithPkce() throws Exception {
        HttpSecurity httpSecurity = Mockito.mock(HttpSecurity.class);
        ClientRegistrationRepository clientRegistrationRepository = Mockito.mock(ClientRegistrationRepository.class);
        OAuth2LoginConfigurer<HttpSecurity> oAuth2LoginConfigurer = Mockito.mock(OAuth2LoginConfigurer.class);
        OAuth2LoginConfigurer.AuthorizationEndpointConfig authorizationEndpointConfig = Mockito.mock(OAuth2LoginConfigurer.AuthorizationEndpointConfig.class);

        when(httpSecurity.oauth2Login())
                .thenReturn(oAuth2LoginConfigurer);
        when(oAuth2LoginConfigurer.authorizationEndpoint())
                .thenReturn(authorizationEndpointConfig);
        when(authorizationEndpointConfig.authorizationRequestResolver(any()))
                .thenReturn(authorizationEndpointConfig);

        Kinde.configureOAuth2WithPkce(httpSecurity, clientRegistrationRepository);
    }

    @Test
    public void testConfigureOAuth2WithPkceServerSecurity() throws Exception {
        ServerHttpSecurity serverHttpSecurity = Mockito.mock(ServerHttpSecurity.class);
        ReactiveClientRegistrationRepository reactiveClientRegistrationRepository = Mockito.mock(ReactiveClientRegistrationRepository.class);
        ServerHttpSecurity.OAuth2LoginSpec oAuth2LoginSpec = Mockito.mock(ServerHttpSecurity.OAuth2LoginSpec.class);
        when(serverHttpSecurity.oauth2Login())
                .thenReturn(oAuth2LoginSpec);
        when(oAuth2LoginSpec.authorizationRequestResolver(any()))
                .thenReturn(oAuth2LoginSpec);

        Kinde.configureOAuth2WithPkce(serverHttpSecurity, reactiveClientRegistrationRepository);
    }


}
