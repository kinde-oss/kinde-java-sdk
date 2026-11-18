package com.kinde.spring;

import com.kinde.spring.sdk.KindeSdkClient;
import org.springframework.security.oauth2.client.userinfo.DefaultReactiveOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

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
        if (!UserUtil.isKindeRegistration(userRequest)) {
            return super.loadUser(userRequest);
        }
        // Decorating a Kinde user builds the SDK client and calls the user-info endpoint, both
        // blocking, so it runs on the bounded elastic scheduler instead of the event loop
        return super.loadUser(userRequest)
                .flatMap(user -> Mono.fromCallable(() -> decorate(user, userRequest))
                        .subscribeOn(Schedulers.boundedElastic()));
    }

    OAuth2User decorate(OAuth2User user, OAuth2UserRequest userRequest) {
        if (!UserUtil.isKindeRegistration(userRequest)) {
            // Never build the Kinde client for another provider's login
            return user;
        }
        return UserUtil.decorateUser(user, userRequest, authoritiesProviders, kindeSdkClient.getClient());
    }
}