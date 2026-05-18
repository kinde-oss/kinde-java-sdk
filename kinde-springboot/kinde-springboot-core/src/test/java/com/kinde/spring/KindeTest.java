package com.kinde.spring;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Spring Security 7 enforces the {@link Customizer}-based DSL on
 * {@link HttpSecurity}/{@link ServerHttpSecurity}. The pre-upgrade tests stubbed the now-removed
 * chained methods (e.g. {@code oauth2Login()}, {@code authorizationEndpoint()}); we now invoke the
 * customizer that {@code Kinde#configureOAuth2WithPkce} passes in to drive the same configuration paths.
 */
public class KindeTest {

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    public void testConfigureOAuth2WithPkce() throws Exception {
        HttpSecurity httpSecurity = Mockito.mock(HttpSecurity.class);
        ClientRegistrationRepository clientRegistrationRepository = Mockito.mock(ClientRegistrationRepository.class);
        OAuth2LoginConfigurer<HttpSecurity> oAuth2LoginConfigurer = Mockito.mock(OAuth2LoginConfigurer.class);
        OAuth2LoginConfigurer.AuthorizationEndpointConfig authorizationEndpointConfig =
                Mockito.mock(OAuth2LoginConfigurer.AuthorizationEndpointConfig.class);

        when(httpSecurity.oauth2Login(any(Customizer.class))).thenAnswer(invocation -> {
            Customizer<OAuth2LoginConfigurer<HttpSecurity>> customizer = invocation.getArgument(0);
            customizer.customize(oAuth2LoginConfigurer);
            return httpSecurity;
        });
        when(oAuth2LoginConfigurer.authorizationEndpoint(any(Customizer.class))).thenAnswer(invocation -> {
            Customizer<OAuth2LoginConfigurer.AuthorizationEndpointConfig> customizer = invocation.getArgument(0);
            customizer.customize(authorizationEndpointConfig);
            return oAuth2LoginConfigurer;
        });
        when(authorizationEndpointConfig.authorizationRequestResolver(any())).thenReturn(authorizationEndpointConfig);

        Kinde.configureOAuth2WithPkce(httpSecurity, clientRegistrationRepository);

        verify(authorizationEndpointConfig).authorizationRequestResolver(any());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testConfigureOAuth2WithPkceServerSecurity() {
        ServerHttpSecurity serverHttpSecurity = Mockito.mock(ServerHttpSecurity.class);
        ReactiveClientRegistrationRepository reactiveClientRegistrationRepository =
                Mockito.mock(ReactiveClientRegistrationRepository.class);
        ServerHttpSecurity.OAuth2LoginSpec oAuth2LoginSpec = Mockito.mock(ServerHttpSecurity.OAuth2LoginSpec.class);

        when(serverHttpSecurity.oauth2Login(any(Customizer.class))).thenAnswer(invocation -> {
            Customizer<ServerHttpSecurity.OAuth2LoginSpec> customizer = invocation.getArgument(0);
            customizer.customize(oAuth2LoginSpec);
            return serverHttpSecurity;
        });
        when(oAuth2LoginSpec.authorizationRequestResolver(any())).thenReturn(oAuth2LoginSpec);

        Kinde.configureOAuth2WithPkce(serverHttpSecurity, reactiveClientRegistrationRepository);

        verify(oAuth2LoginSpec).authorizationRequestResolver(any());
    }
}
