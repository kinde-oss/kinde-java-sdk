package com.kinde.spring;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;

public class Kinde {


    /**
     * Configures the {@code http} with an OAuth2 Login, that supports PKCE, invitation code,
     * and connection ID handling. The default Spring Security implementation only enables PKCE
     * for public clients.
     * <p>
     * When the originating request contains an {@code invitation_code} query parameter,
     * it is forwarded to the authorization endpoint along with {@code is_invitation=true}.
     * When it contains a {@code connection_id} query parameter, that value is forwarded
     * so Kinde can skip the identity picker.
     * <p>
     * <b>NOTE:</b> Enabling PKCE will be required for all clients (public and confidential) in the future OAuth 2.1 spec.
     *
     * @param http the HttpSecurity to configure
     * @param clientRegistrationRepository the repository bean, this should be injected into the calling method.
     * @return the {@code http} to allow method chaining
     * @throws Exception
     */
    public static HttpSecurity configureOAuth2WithPkce(HttpSecurity http, ClientRegistrationRepository clientRegistrationRepository) throws Exception {
        KindeOAuth2AuthorizationRequestResolver authorizationRequestResolver =
                new KindeOAuth2AuthorizationRequestResolver(clientRegistrationRepository, "/oauth2/authorization");
        http.oauth2Login(oauth2 -> oauth2
                .authorizationEndpoint(endpoint -> endpoint.authorizationRequestResolver(authorizationRequestResolver)));

        return http;
    }


    /**
     * Configures the {@code http} with an OAuth2 Login that supports PKCE, invitation code,
     * connection ID and step-up authentication handling, like the servlet-based
     * {@link #configureOAuth2WithPkce(HttpSecurity, ClientRegistrationRepository)} overload.
     * <p>
     * The {@code invitation_code}, {@code connection_id}, {@code acr_values} and {@code max_age}
     * query parameters of the originating request are forwarded to the authorization endpoint
     * (see {@link ReactiveKindeOAuth2AuthorizationRequestResolver}).
     *
     * @param http the ServerHttpSecurity to configure
     * @param clientRegistrationRepository the reactive repository bean
     * @return the {@code http} to allow method chaining
     */
    public static ServerHttpSecurity configureOAuth2WithPkce(ServerHttpSecurity http, ReactiveClientRegistrationRepository clientRegistrationRepository) {
        ReactiveKindeOAuth2AuthorizationRequestResolver authorizationRequestResolver =
                new ReactiveKindeOAuth2AuthorizationRequestResolver(clientRegistrationRepository);
        http.oauth2Login(oauth2 -> oauth2.authorizationRequestResolver(authorizationRequestResolver));

        return http;
    }
}
