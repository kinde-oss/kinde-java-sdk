package com.kinde.oauth.service;

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

@Service
public class KindeService {

    private final OAuth2AuthorizedClientService authorizedClientService;
    private final WebClient userProfileClient;

    public KindeService(OAuth2AuthorizedClientService authorizedClientService,
                        WebClient userProfileClient) {
        this.authorizedClientService = authorizedClientService;
        this.userProfileClient = userProfileClient;
    }

    public KindeProfile loadDashboard(Authentication authentication) {

        OAuth2AuthorizedClient authorizedClient = getoAuth2AuthorizedClient(authentication);

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

    private OAuth2AuthorizedClient getoAuth2AuthorizedClient(Authentication authentication) {
        return authorizedClientService.loadAuthorizedClient("kinde", authentication.getName());
    }
}

