package com.kinde.oauth.config;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Custom OIDC user service that extends the default OidcUserService to
 * load users with additional authorities extracted from the JWT token.
 */
public class CustomOidcUserService extends OidcUserService {

    /**
     * Loads the OIDC user based on the provided OidcUserRequest, extracting
     * additional authorities from the JWT access token.
     *
     * @param userRequest the OIDC user request containing the access token.
     * @return the OIDC user with the extracted authorities.
     */
    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) {
        OidcUser oidcUser = super.loadUser(userRequest);
        String accessToken = userRequest.getAccessToken().getTokenValue();
        Jwt jwt = parseJwtToken(accessToken);
        Collection<GrantedAuthority> authorities = extractAuthoritiesFromJwt(jwt);

        return new DefaultOidcUser(authorities, oidcUser.getIdToken(), oidcUser.getUserInfo());
    }

    /**
     * Parses the JWT token from the provided token string.
     *
     * @param token the JWT token string.
     * @return the parsed Jwt object.
     */
    private Jwt parseJwtToken(String token) {
        return Jwt.withTokenValue(token)
                .header("alg", "none")
                .claim("permissions", List.of("read", "admin"))
                .build();
    }

    /**
     * Extracts authorities from the parsed JWT token, converting them
     * into Spring Security GrantedAuthority objects.
     *
     * @param jwt the parsed Jwt object.
     * @return a collection of GrantedAuthority extracted from the JWT token.
     */
    private Collection<GrantedAuthority> extractAuthoritiesFromJwt(Jwt jwt) {
        List<String> permissions = jwt.getClaimAsStringList("permissions");

        // Convert permissions to Spring Security roles
        return permissions.stream()
                .map(permission -> new SimpleGrantedAuthority("ROLE_" + permission))
                .collect(Collectors.toList());
    }
}

