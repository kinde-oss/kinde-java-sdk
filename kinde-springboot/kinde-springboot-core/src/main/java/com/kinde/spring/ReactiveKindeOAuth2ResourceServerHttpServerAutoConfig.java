/*
 * Copyright 2019-Present Okta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kinde.spring;

import com.kinde.spring.config.KindeOAuth2Properties;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;

@AutoConfiguration
@ConditionalOnKindeResourceServerProperties
@AutoConfigureAfter(ReactiveKindeOAuth2ResourceServerAutoConfig.class)
@EnableConfigurationProperties({KindeOAuth2Properties.class, OAuth2ResourceServerProperties.class})
@ConditionalOnClass({ EnableWebFluxSecurity.class, BearerTokenAuthenticationToken.class, ReactiveJwtDecoder.class })
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
class ReactiveKindeOAuth2ResourceServerHttpServerAutoConfig {

    @Bean
    BeanPostProcessor oktaOAuth2ResourceServerBeanPostProcessor(KindeOAuth2Properties kindeOAuth2Properties) {
        return new KindeOAuth2ResourceServerBeanPostProcessor(kindeOAuth2Properties);
    }

    static class KindeOAuth2ResourceServerBeanPostProcessor implements BeanPostProcessor {

        private final KindeOAuth2Properties kindeOAuth2Properties;

        KindeOAuth2ResourceServerBeanPostProcessor(KindeOAuth2Properties kindeOAuth2Properties) {
            this.kindeOAuth2Properties = kindeOAuth2Properties;
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) {
            if (bean instanceof ServerHttpSecurity) {
                final ServerHttpSecurity http = (ServerHttpSecurity) bean;
                http.oauth2ResourceServer().jwt()
                        .jwtAuthenticationConverter(new ReactiveJwtAuthenticationConverterAdapter(
                                new KindeJwtAuthenticationConverter(kindeOAuth2Properties.getPermissionsClaim())));
            }
            return bean;
        }
    }
}