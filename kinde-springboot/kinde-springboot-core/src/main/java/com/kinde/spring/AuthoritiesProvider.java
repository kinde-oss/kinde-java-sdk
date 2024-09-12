package com.kinde.spring;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;

/**
 * Allows for custom {@link GrantedAuthority}s to be added to the current OAuth Principal. Multiple implementations
 * are allowed, by default OAuth scopes are converted to Authorities with the format {@code SCOPE_<scope-name>} and
 * if a `groups` claim exists in the access or id token, those are converted as well.
 *
 * Example usage:
 *
 * <pre><code>
 *     &#64;Bean
 *     AuthoritiesProvider myCustomAuthoritiesProvider() {
 *         return (user, userRequest) -&gt; lookupExtraAuthoritesByName(user.getAttributes().get("email"));
 *     }
 * </code></pre>
 *
 * @since 1.4.0
 */
public interface AuthoritiesProvider {

    Collection<? extends GrantedAuthority> getAuthorities(OAuth2User user, OAuth2UserRequest userRequest);

    default Collection<? extends GrantedAuthority> getAuthorities(OidcUser user, OidcUserRequest userRequest) {
        return getAuthorities((OAuth2User) user, userRequest);
    }
}
