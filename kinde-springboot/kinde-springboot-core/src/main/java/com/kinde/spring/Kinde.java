package com.kinde.spring;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.server.DefaultServerOAuth2AuthorizationRequestResolver;

import static org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestCustomizers.withPkce;

public class Kinde {


    /**
     * Configures the {@code http} with an OAuth2 Login, that supports PKCE. The default Spring Security implementation
     * only enables PKCE for public clients.
     * <p>
     * <b>NOTE:</b> Enabling PKCE will be required for all clients (public and confidential) in the future OAuth 2.1 spec.
     *
     * @param http the HttpSecurity to configure
     * @param clientRegistrationRepository the repository bean, this should be injected into the calling method.
     * @return the {@code http} to allow method chaining
     * @throws Exception
     */
    public static HttpSecurity configureOAuth2WithPkce(HttpSecurity http, ClientRegistrationRepository clientRegistrationRepository) throws Exception {
        // Create a request resolver that enables PKCE
        DefaultOAuth2AuthorizationRequestResolver authorizationRequestResolver = new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository, "/oauth2/authorization");
        authorizationRequestResolver.setAuthorizationRequestCustomizer(withPkce());
        // enable oauth2 login that uses PKCE
        http.oauth2Login()
                .authorizationEndpoint()
                .authorizationRequestResolver(authorizationRequestResolver);

        return http;
    }


    public static ServerHttpSecurity configureOAuth2WithPkce(ServerHttpSecurity http, ReactiveClientRegistrationRepository clientRegistrationRepository) {
        // Create a request resolver that enables PKCE
        DefaultServerOAuth2AuthorizationRequestResolver authorizationRequestResolver = new DefaultServerOAuth2AuthorizationRequestResolver(clientRegistrationRepository);
        authorizationRequestResolver.setAuthorizationRequestCustomizer(withPkce());
        // enable oauth2 login that uses PKCE
        http.oauth2Login().authorizationRequestResolver(authorizationRequestResolver);

        return http;
    }
}
