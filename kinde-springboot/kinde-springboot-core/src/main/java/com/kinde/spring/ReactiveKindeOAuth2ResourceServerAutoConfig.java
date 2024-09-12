package com.kinde.spring;

import com.kinde.spring.config.KindeOAuth2Properties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.reactive.ReactiveOAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.web.reactive.function.client.WebClient;

@AutoConfiguration
@AutoConfigureBefore(ReactiveOAuth2ResourceServerAutoConfiguration.class)
@ConditionalOnKindeResourceServerProperties
@EnableConfigurationProperties({KindeOAuth2Properties.class, OAuth2ResourceServerProperties.class})
@ConditionalOnClass({ EnableWebFluxSecurity.class, BearerTokenAuthenticationToken.class, ReactiveJwtDecoder.class })
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
class ReactiveKindeOAuth2ResourceServerAutoConfig {

    @Bean
    @ConditionalOnMissingBean
    ReactiveJwtDecoder jwtDecoder(OAuth2ResourceServerProperties oAuth2ResourceServerProperties, KindeOAuth2Properties kindeOAuth2Properties) {

        NimbusReactiveJwtDecoder.JwkSetUriReactiveJwtDecoderBuilder builder =
            NimbusReactiveJwtDecoder.withJwkSetUri(oAuth2ResourceServerProperties.getJwt().getJwkSetUri());
        builder.webClient(webClient());
        NimbusReactiveJwtDecoder decoder = builder.build();
        decoder.setJwtValidator(TokenUtil.jwtValidator(oAuth2ResourceServerProperties.getJwt().getIssuerUri(), kindeOAuth2Properties.getAudience()));
        return decoder;
    }

    private WebClient webClient() {
        return WebClientUtil.createWebClient();
    }
}