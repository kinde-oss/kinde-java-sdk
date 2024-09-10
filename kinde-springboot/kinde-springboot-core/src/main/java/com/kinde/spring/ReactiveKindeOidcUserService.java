/*
 * Copyright 2019-Present Okta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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