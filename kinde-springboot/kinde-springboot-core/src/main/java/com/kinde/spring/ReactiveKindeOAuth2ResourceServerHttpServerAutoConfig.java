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
    BeanPostProcessor kindeOAuth2ResourceServerBeanPostProcessor(KindeOAuth2Properties kindeOAuth2Properties) {
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