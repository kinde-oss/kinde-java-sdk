package com.kinde.spring;

import com.kinde.spring.config.KindeOAuth2Properties;
import com.kinde.spring.env.KindeOAuth2PropertiesMappingEnvironmentPostProcessor;
import com.kinde.spring.sdk.KindeSdkClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcReactiveOAuth2UserService;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@TestPropertySource(properties = {
        KindeOAuth2PropertiesMappingEnvironmentPostProcessor.KINDE_OAUTH_DOMAIN + "=https://test.kinde.com",
        KindeOAuth2PropertiesMappingEnvironmentPostProcessor.KINDE_OAUTH_CLIENT_ID + "=test_client_id",
        KindeOAuth2PropertiesMappingEnvironmentPostProcessor.KINDE_OAUTH_CLIENT_SECRET + "=test_client_secret",
        KindeOAuth2PropertiesMappingEnvironmentPostProcessor.KINDE_OAUTH_SCOPES + "=profile",
        "spring.security.oauth2.client.registration.kinde.client-id=test_client_id"
})
@SpringBootTest(classes = {ReactiveKindeOAuth2AutoConfigTest.class})
@Import(ReactiveKindeOAuth2AutoConfigTest.MyTestConfig.class)
public class ReactiveKindeOAuth2AutoConfigTest {

    @TestConfiguration
    public static class MyTestConfig {
        @Bean
        public ReactiveKindeOAuth2AutoConfig reactiveKindeOAuth2AutoConfig() {
            return new ReactiveKindeOAuth2AutoConfig();
        }

        @Bean
        public KindeSdkClient kindeSdkClient() {
            return mock(KindeSdkClient.class);
        }

        /**
         * Required because the real {@code KindeSdkClient} autoconfig in the classpath autowires
         * {@link KindeOAuth2Properties}; without a bean of that type, context refresh fails.
         */
        @Bean
        public KindeOAuth2Properties kindeOAuth2Properties() {
            return mock(KindeOAuth2Properties.class);
        }
    }

    @Autowired
    private ReactiveKindeOAuth2AutoConfig autoConfig;

    @Test
    public void oauth2UserServiceProducesReactiveKindeUserService() {
        ReactiveOAuth2UserService<OAuth2UserRequest, OAuth2User> service = autoConfig.oauth2UserService(List.of());

        assertNotNull(service);
        assertInstanceOf(ReactiveKindeOAuth2UserService.class, service);
    }

    @Test
    public void oidcUserServiceProducesReactiveKindeOidcUserService() {
        ReactiveOAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService = autoConfig.oauth2UserService(List.of());

        OidcReactiveOAuth2UserService oidcUserService = autoConfig.oidcUserService(List.of(), oauth2UserService);

        assertNotNull(oidcUserService);
        assertInstanceOf(ReactiveKindeOidcUserService.class, oidcUserService);
    }

    /**
     * Drives the reactive equivalent of the servlet filter-chain factory by mocking
     * {@link ServerHttpSecurity}. The {@code authorizeExchange} customizer fires through a
     * Mockito {@code Answer} so the {@code exchanges.anyExchange().authenticated()} chain
     * executes; the {@code oauth2ResourceServer} customizer fires too so {@code customDecoder()}
     * gets exercised end-to-end.
     */
    @Test
    public void springSecurityFilterChainBuildsTheReactiveChainAndDelegatesToTheDsl() {
        ServerHttpSecurity http = mock(ServerHttpSecurity.class);
        SecurityWebFilterChain expectedChain = mock(SecurityWebFilterChain.class);
        when(http.build()).thenReturn(expectedChain);

        // authorizeExchange(...) -> drive the inner exchanges.anyExchange().authenticated()
        ServerHttpSecurity.AuthorizeExchangeSpec authorizeSpec =
                mock(ServerHttpSecurity.AuthorizeExchangeSpec.class);
        ServerHttpSecurity.AuthorizeExchangeSpec.Access access =
                mock(ServerHttpSecurity.AuthorizeExchangeSpec.Access.class);
        when(authorizeSpec.anyExchange()).thenReturn(access);
        when(access.authenticated()).thenReturn(authorizeSpec);
        when(http.authorizeExchange(any())).thenAnswer(invocation -> {
            Customizer<ServerHttpSecurity.AuthorizeExchangeSpec> customizer = invocation.getArgument(0);
            customizer.customize(authorizeSpec);
            return http;
        });

        // oauth2Login is invoked by Kinde.configureOAuth2WithPkce; oauth2Client by Customizer.withDefaults().
        when(http.oauth2Login(any())).thenReturn(http);
        when(http.oauth2Client(any())).thenReturn(http);

        // oauth2ResourceServer(...) -> drive the inner customDecoder which calls server.jwt(...)
        // and the nested jwt -> jwt.jwtDecoder(decoder) customizer so we can verify the configured
        // ReactiveJwtDecoder is the one actually installed onto the JwtSpec.
        ServerHttpSecurity.OAuth2ResourceServerSpec resourceServerSpec =
                mock(ServerHttpSecurity.OAuth2ResourceServerSpec.class);
        ServerHttpSecurity.OAuth2ResourceServerSpec.JwtSpec jwtSpec =
                mock(ServerHttpSecurity.OAuth2ResourceServerSpec.JwtSpec.class);
        when(jwtSpec.jwtDecoder(any())).thenReturn(jwtSpec);
        when(resourceServerSpec.jwt(any())).thenAnswer(invocation -> {
            Customizer<ServerHttpSecurity.OAuth2ResourceServerSpec.JwtSpec> inner = invocation.getArgument(0);
            inner.customize(jwtSpec);
            return resourceServerSpec;
        });
        when(http.oauth2ResourceServer(any())).thenAnswer(invocation -> {
            Customizer<ServerHttpSecurity.OAuth2ResourceServerSpec> customizer = invocation.getArgument(0);
            customizer.customize(resourceServerSpec);
            return http;
        });

        ReactiveJwtDecoder jwtDecoder = mock(ReactiveJwtDecoder.class);
        ReactiveClientRegistrationRepository repo = mock(ReactiveClientRegistrationRepository.class);

        SecurityWebFilterChain chain = autoConfig.springSecurityFilterChain(http, jwtDecoder, repo);

        assertSame(expectedChain, chain, "should return the chain produced by http.build()");
        verify(http).authorizeExchange(any());
        verify(authorizeSpec).anyExchange();
        verify(access).authenticated();
        // Kinde.configureOAuth2WithPkce calls oauth2Login(...) once.
        verify(http, atLeastOnce()).oauth2Login(any());
        verify(http).oauth2Client(any());
        verify(http).oauth2ResourceServer(any());
        verify(resourceServerSpec).jwt(any());
        verify(jwtSpec).jwtDecoder(jwtDecoder);
        verify(http).build();
    }
}
