package com.kinde.spring;

import com.kinde.spring.config.KindeOAuth2Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.DelegatingReactiveAuthenticationManager;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginReactiveAuthenticationManager;
import org.springframework.security.oauth2.client.endpoint.WebClientReactiveAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.oidc.authentication.OidcAuthorizationCodeReactiveAuthenticationManager;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcReactiveOAuth2UserService;
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.ResponseStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URISyntaxException;

@AutoConfiguration
@ConditionalOnKindeClientProperties
@AutoConfigureAfter(ReactiveKindeOAuth2AutoConfig.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@ConditionalOnClass({ Flux.class, EnableWebFluxSecurity.class, ClientRegistration.class })
@ConditionalOnBean(ReactiveKindeOAuth2AutoConfig.class)
class ReactiveKindeOAuth2ServerHttpServerAutoConfig {

    @Bean
    BeanPostProcessor authManagerServerHttpSecurityBeanPostProcessor(@Qualifier("oauth2UserService") ReactiveOAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService,
                                                                     @Qualifier("oidcUserService") OidcReactiveOAuth2UserService oidcUserService,
                                                                     @Autowired(required = false) OidcClientInitiatedServerLogoutSuccessHandler logoutSuccessHandler) {
        return new KindeOAuth2LoginServerBeanPostProcessor(oAuth2UserService, oidcUserService, logoutSuccessHandler);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = "okta.oauth2.post-logout-redirect-uri")
    OidcClientInitiatedServerLogoutSuccessHandler oidcClientInitiatedServerLogoutSuccessHandler(KindeOAuth2Properties oktaOAuth2Properties,
                                                                                                ReactiveClientRegistrationRepository repository) throws URISyntaxException {
        OidcClientInitiatedServerLogoutSuccessHandler logoutSuccessHandler = new OidcClientInitiatedServerLogoutSuccessHandler(repository);
        String logoutUri = oktaOAuth2Properties.getPostLogoutRedirectUri();
        logoutSuccessHandler.setPostLogoutRedirectUri((logoutUri.startsWith("/") ? "{baseUrl}" : "") + logoutUri);
        return logoutSuccessHandler;
    }

    /*
     * Fix for https://github.com/spring-projects/spring-security/issues/6484
     */
    private static ReactiveAuthenticationManager reactiveAuthenticationManager(ReactiveOAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService,
                                                                               OidcReactiveOAuth2UserService oidcUserService) {
        WebClientReactiveAuthorizationCodeTokenResponseClient client = new WebClientReactiveAuthorizationCodeTokenResponseClient();
        ReactiveAuthenticationManager result = new OAuth2LoginReactiveAuthenticationManager(client, oAuth2UserService) {
            @Override
            public Mono<Authentication> authenticate(Authentication authentication) {
                return wrapOnErrorMap(super.authenticate(authentication));
            }
        };

        boolean oidcAuthenticationProviderEnabled = ClassUtils.isPresent(
                "org.springframework.security.oauth2.jwt.JwtDecoder", ReactiveKindeOAuth2ServerHttpServerAutoConfig.class.getClassLoader());
        if (oidcAuthenticationProviderEnabled) {
            OidcAuthorizationCodeReactiveAuthenticationManager oidc = new OidcAuthorizationCodeReactiveAuthenticationManager(client, oidcUserService) {
                @Override
                public Mono<Authentication> authenticate(Authentication authentication) {
                    return wrapOnErrorMap(super.authenticate(authentication));
                }
            };
            result = new DelegatingReactiveAuthenticationManager(oidc, result);
        }
        return result;
    }

    private static Mono<Authentication> wrapOnErrorMap(Mono<Authentication> authentication) {
        return authentication.onErrorMap(ReactiveKindeOAuth2ServerHttpServerAutoConfig::shouldWrapException,
                                         e -> new UnknownOAuthException("An error occurred while attempting to authenticate: ", e));
    }

    private static boolean shouldWrapException(Throwable e) {
        return e instanceof IllegalStateException
            || e instanceof JwtException
            || e instanceof AuthenticationException;
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    static class UnknownOAuthException extends AuthenticationException {
        UnknownOAuthException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    static class KindeOAuth2LoginServerBeanPostProcessor implements BeanPostProcessor {

        private final ReactiveOAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService;
        private final OidcReactiveOAuth2UserService oidcUserService;
        private final OidcClientInitiatedServerLogoutSuccessHandler logoutSuccessHandler;

        KindeOAuth2LoginServerBeanPostProcessor(ReactiveOAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService,
                                               OidcReactiveOAuth2UserService oidcUserService,
                                               @Nullable OidcClientInitiatedServerLogoutSuccessHandler logoutSuccessHandler) {
            this.oAuth2UserService = oAuth2UserService;
            this.oidcUserService = oidcUserService;
            this.logoutSuccessHandler = logoutSuccessHandler;

        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) {
            if (bean instanceof ServerHttpSecurity) {
                ServerHttpSecurity httpSecurity = (ServerHttpSecurity) bean;
                httpSecurity.oauth2Login().authenticationManager(reactiveAuthenticationManager(oAuth2UserService, oidcUserService));

                if (logoutSuccessHandler != null) {
                    httpSecurity.logout().logoutSuccessHandler(logoutSuccessHandler);
                }
            }
            return bean;
        }
    }
}