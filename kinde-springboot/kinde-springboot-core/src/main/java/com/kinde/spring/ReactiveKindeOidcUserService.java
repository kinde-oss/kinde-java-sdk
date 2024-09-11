package com.kinde.spring;

import com.kinde.spring.sdk.KindeSdkClient;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcReactiveOAuth2UserService;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import reactor.core.publisher.Mono;

import java.util.Collection;

final class ReactiveKindeOidcUserService extends OidcReactiveOAuth2UserService {

    private final Collection<AuthoritiesProvider> authoritiesProviders;
    private KindeSdkClient kindeSdkClient;

    ReactiveKindeOidcUserService(Collection<AuthoritiesProvider> authoritiesProviders, KindeSdkClient kindeSdkClient) {
        this(authoritiesProviders, new ReactiveKindeOAuth2UserService(authoritiesProviders,kindeSdkClient),kindeSdkClient);
    }

    ReactiveKindeOidcUserService(Collection<AuthoritiesProvider> authoritiesProviders, ReactiveOAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService, KindeSdkClient kindeSdkClient) {
        this.authoritiesProviders = authoritiesProviders;
        this.kindeSdkClient = kindeSdkClient;
        setOauth2UserService(oauth2UserService);
    }

    @Override
    public Mono<OidcUser> loadUser(OidcUserRequest userRequest) {
        return super.loadUser(userRequest).map(user -> UserUtil.decorateUser(user, userRequest,  authoritiesProviders, kindeSdkClient.getClient()));
    }
}