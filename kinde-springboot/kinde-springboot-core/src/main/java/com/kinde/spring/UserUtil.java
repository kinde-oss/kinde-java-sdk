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

import com.kinde.KindeClient;
import com.kinde.KindeClientSession;
import com.kinde.token.AccessToken;
import com.kinde.user.UserInfo;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.util.StringUtils;

import java.util.*;

final class UserUtil {

    private UserUtil() {}

    static OAuth2User decorateUser(OAuth2User user, OAuth2UserRequest userRequest, Collection<AuthoritiesProvider> authoritiesProviders, KindeClient kindeClient) {

        // Only post process requests from the "Okta" reg
        if (!"kinde".equalsIgnoreCase(userRequest.getClientRegistration().getRegistrationId())) {
            return user;
        }

        Map<String,Object> attributes = new HashMap<>(user.getAttributes());
        KindeClientSession kindeClientSession = kindeClient.initClientSession(AccessToken.init(userRequest.getAccessToken().getTokenValue(),true));
        UserInfo userInfo = kindeClientSession.retrieveUserInfo();

        attributes.put("KindeUserInfo",userInfo);

        // start with authorities from super
        Set<GrantedAuthority> authorities = new HashSet<>(user.getAuthorities());
        // add each set of authorities from providers
        authoritiesProviders.stream()
            .map(authoritiesProvider -> authoritiesProvider.getAuthorities(user, userRequest))
            .filter(Objects::nonNull)
            .forEach(authorities::addAll);

        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();



        return new DefaultOAuth2User(authorities, attributes, userNameAttributeName);
    }

    static OidcUser decorateUser(OidcUser user, OidcUserRequest userRequest, Collection<AuthoritiesProvider> authoritiesProviders, KindeClient kindeClient) {

        // Only post process requests from the "Okta" reg
        if (!"kinde".equals(userRequest.getClientRegistration().getRegistrationId())) {
            return user;
        }

        // start with authorities from super
        Set<GrantedAuthority> authorities = new HashSet<>(user.getAuthorities());
        // add each set of authorities from providers
        authoritiesProviders.stream()
            .map(authoritiesProvider -> authoritiesProvider.getAuthorities(user, userRequest))
            .filter(Objects::nonNull)
            .forEach(authorities::addAll);

        String userNameAttributeName = userRequest.getClientRegistration()
            .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        return StringUtils.hasText(userNameAttributeName)
                ? new DefaultOidcUser(authorities, user.getIdToken(), user.getUserInfo(), userNameAttributeName)
                : new DefaultOidcUser(authorities, user.getIdToken(), user.getUserInfo());
    }
}