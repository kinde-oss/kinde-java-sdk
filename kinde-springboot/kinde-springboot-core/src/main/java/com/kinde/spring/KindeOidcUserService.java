package com.kinde.spring;

import com.kinde.spring.sdk.KindeSdkClient;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;

final class KindeOidcUserService extends OidcUserService {

    private final Collection<AuthoritiesProvider> authoritiesProviders;
    private KindeSdkClient kindeSdkClient;

    KindeOidcUserService(OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService, Collection<AuthoritiesProvider> authoritiesProviders, KindeSdkClient kindeSdkClient) {
        this.authoritiesProviders = authoritiesProviders;
        this.kindeSdkClient = kindeSdkClient;
        this.setOauth2UserService(oauth2UserService);
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser user = super.loadUser(userRequest);
        return UserUtil.decorateUser(user, userRequest, authoritiesProviders, kindeSdkClient.getClient());
    }
}