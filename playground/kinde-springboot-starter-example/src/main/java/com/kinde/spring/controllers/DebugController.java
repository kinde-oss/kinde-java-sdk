package com.kinde.spring.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Debug helpers protected by the browser session (NOT the Bearer-token chain).
 *
 * <p>{@code GET /debug/token} returns the OAuth2 access token + id token + refresh token for the
 * currently logged-in user. Used to grab a real Kinde access token to feed into curl requests
 * against {@code /api/**} for resource-server testing.
 *
 * <p>Do not ship this in a real application.
 */
@RestController
@RequestMapping("/debug")
public class DebugController {

    @GetMapping("/token")
    public Map<String, String> token(@AuthenticationPrincipal OidcUser user,
                                     @RegisteredOAuth2AuthorizedClient("kinde") OAuth2AuthorizedClient client) {
        Map<String, String> result = new LinkedHashMap<>();
        result.put("access_token", client.getAccessToken().getTokenValue());
        result.put("id_token", user.getIdToken().getTokenValue());
        if (client.getRefreshToken() != null) {
            result.put("refresh_token", client.getRefreshToken().getTokenValue());
        }
        result.put("token_type", client.getAccessToken().getTokenType().getValue());
        if (client.getAccessToken().getExpiresAt() != null) {
            result.put("expires_at", client.getAccessToken().getExpiresAt().toString());
        }
        return result;
    }
}
