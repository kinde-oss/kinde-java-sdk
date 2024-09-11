package com.kinde.oauth.service;

import com.kinde.KindeClient;
import com.kinde.KindeClientBuilder;
import com.kinde.oauth.model.KindeProfile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashSet;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient;

/**
 * Service class to handle interactions with the Kinde OAuth2 authorization service
 * and retrieve user information.
 */
@Service
public class KindeService {

    private final OAuth2AuthorizedClientService authorizedClientService;
    private final WebClient userProfileClient;

    /**
     * Constructor to initialize the KindeService.
     *
     * @param authorizedClientService the service for managing authorized OAuth2 clients.
     * @param userProfileClient       the {@link WebClient} used to fetch the user profile.
     */
    public KindeService(OAuth2AuthorizedClientService authorizedClientService,
                        WebClient userProfileClient) {
        this.authorizedClientService = authorizedClientService;
        this.userProfileClient = userProfileClient;
    }

    /**
     * Loads the user dashboard by fetching the OAuth2 authorized client and retrieving
     * the user profile data.
     *
     * @return a {@link KindeProfile} object containing user details.
     */
    public KindeProfile loadDashboard() {

        OAuth2AuthorizedClient authorizedClient = getOAuth2AuthorizedClient(SecurityContextHolder.getContext().getAuthentication());

        // Extracting user profile via Spring Webflux client
        String userprofile = this.userProfileClient
                .get()
                .attributes(oauth2AuthorizedClient(authorizedClient))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        DefaultOidcUser principal = (DefaultOidcUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return KindeProfile.builder()
                .accessToken(authorizedClient.getAccessToken().getTokenValue())
                .fullName(principal.getUserInfo().getFullName())
                .idToken(principal.getIdToken().getTokenValue())
                .roles(new HashSet<>(principal.getAuthorities()))
                .userProfile(userprofile)
                .build();
    }

    /**
     * Retrieves the OAuth2 authorized client for the authenticated user.
     *
     * @param authentication the current user's {@link Authentication} object.
     * @return the {@link OAuth2AuthorizedClient} for the user.
     */
    private OAuth2AuthorizedClient getOAuth2AuthorizedClient(Authentication authentication) {
        return authorizedClientService.loadAuthorizedClient("kinde", authentication.getName());
    }
}

