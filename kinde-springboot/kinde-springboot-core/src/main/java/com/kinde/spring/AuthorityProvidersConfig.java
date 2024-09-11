package com.kinde.spring;

import com.kinde.spring.config.KindeOAuth2Properties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @since 1.4.0
 */
@Configuration
class AuthorityProvidersConfig {

    @Bean
    AuthoritiesProvider tokenScopesAuthoritiesProvider() {
        return (user, userRequest) -> TokenUtil.tokenScopesToAuthorities(userRequest.getAccessToken());
    }

    @Bean
    AuthoritiesProvider groupClaimsAuthoritiesProvider(KindeOAuth2Properties kindeOAuth2Properties) {
        return (user, userRequest) -> TokenUtil.tokenClaimsToAuthorities(user.getAttributes(), kindeOAuth2Properties.getPermissionsClaim());
    }
}