package com.kinde.oauth.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuration class for setting up a {@link WebClient} to interact with
 * OAuth2-protected resources.
 */
@Configuration
public class WebClientConfiguration {

    @Value("${user-profile-uri}")
    String userprofileUri;

    /**
     * Creates a {@link WebClient} bean specifically for accessing the user profile
     * resource, configured with OAuth2 authorization support.
     *
     * @param authorizedClientManager the {@link OAuth2AuthorizedClientManager} used to
     *                                manage OAuth2 authorized clients.
     * @return a configured {@link WebClient} instance.
     */
    @Bean
    @Qualifier(value = "userProfile")
    WebClient userProfileClient(OAuth2AuthorizedClientManager authorizedClientManager) {

        ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2 = new ServletOAuth2AuthorizedClientExchangeFilterFunction(
                authorizedClientManager);
        oauth2.setDefaultOAuth2AuthorizedClient(true);

        return WebClient.builder()
                .baseUrl(this.userprofileUri)
                .apply(oauth2.oauth2Configuration())
                .build();
    }
}
