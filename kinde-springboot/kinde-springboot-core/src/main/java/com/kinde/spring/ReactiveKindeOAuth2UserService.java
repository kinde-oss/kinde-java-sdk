package com.kinde.spring;

import com.kinde.spring.sdk.KindeSdkClient;
import org.springframework.security.oauth2.client.userinfo.DefaultReactiveOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import reactor.core.publisher.Mono;

import java.util.Collection;

final class ReactiveKindeOAuth2UserService extends DefaultReactiveOAuth2UserService {

    private final Collection<AuthoritiesProvider> authoritiesProviders;
    private KindeSdkClient kindeSdkClient;

    ReactiveKindeOAuth2UserService(Collection<AuthoritiesProvider> authoritiesProviders, KindeSdkClient kindeSdkClient) {
        this.authoritiesProviders = authoritiesProviders;
        this.kindeSdkClient = kindeSdkClient;
        setWebClient(WebClientUtil.createWebClient());
    }

    @Override
    public Mono<OAuth2User> loadUser(OAuth2UserRequest userRequest) {
        return super.loadUser(userRequest).map(user -> UserUtil.decorateUser(user, userRequest, authoritiesProviders,null));
    }
}