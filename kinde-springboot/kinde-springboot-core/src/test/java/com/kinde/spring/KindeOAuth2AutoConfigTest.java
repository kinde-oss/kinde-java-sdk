package com.kinde.spring;

import com.kinde.KindeClient;
import com.kinde.config.KindeConfig;
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
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.TestPropertySource;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
@SpringBootTest(classes = {KindeOAuth2AutoConfigTest.class})
@Import(KindeOAuth2AutoConfigTest.MyTestConfig.class)
public class KindeOAuth2AutoConfigTest {

    /**
     * Minimal Spring context for the autoconfig under test. {@link KindeOAuth2AutoConfig} is
     * instantiated directly (instead of being picked up via auto-configuration) so we can test
     * its bean-factory methods in isolation, while the {@link KindeSdkClient} chain is mocked
     * end-to-end so individual tests can re-stub {@code logoutRedirectUri()} per scenario.
     */
    @TestConfiguration
    public static class MyTestConfig {

        @Bean
        public ClientRegistrationRepository clientRegistrationRepository() {
            return mock(ClientRegistrationRepository.class);
        }

        @Bean
        public KindeConfig kindeConfig() {
            return mock(KindeConfig.class);
        }

        @Bean
        public KindeClient kindeClient(KindeConfig kindeConfig) {
            KindeClient kindeClient = mock(KindeClient.class);
            when(kindeClient.kindeConfig()).thenReturn(kindeConfig);
            return kindeClient;
        }

        @Bean
        public KindeSdkClient kindeSdkClient(KindeClient kindeClient) {
            KindeSdkClient kindeSdkClient = mock(KindeSdkClient.class);
            when(kindeSdkClient.getClient()).thenReturn(kindeClient);
            return kindeSdkClient;
        }

        /**
         * Required to satisfy the {@code @Autowired KindeOAuth2Properties} field on
         * {@link KindeSdkClient}'s real autoconfig, which Spring Boot picks up from the
         * classpath even though we provide a mocked {@link KindeSdkClient} bean below.
         */
        @Bean
        public KindeOAuth2Properties kindeOAuth2Properties() {
            return mock(KindeOAuth2Properties.class);
        }

        @Bean
        public KindeOAuth2AutoConfig kindeOAuth2AutoConfig(KindeSdkClient kindeSdkClient) {
            KindeOAuth2AutoConfig autoConfig = new KindeOAuth2AutoConfig();
            autoConfig.setKindeSdkClient(kindeSdkClient);
            return autoConfig;
        }
    }

    @Autowired
    private KindeOAuth2AutoConfig kindeOAuth2AutoConfig;

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    @Autowired
    private KindeConfig kindeConfig;

    // --- oidcLogoutSuccessHandler (covers both branches of the {baseUrl} ternary) --------------

    @Test
    public void oidcLogoutSuccessHandlerWithAbsoluteUriDoesNotPrependBaseUrl() throws Exception {
        when(kindeConfig.logoutRedirectUri()).thenReturn("http://localhost:8080/");

        OidcClientInitiatedLogoutSuccessHandler handler =
                kindeOAuth2AutoConfig.oidcLogoutSuccessHandler(clientRegistrationRepository);

        assertNotNull(handler);
        assertEquals("http://localhost:8080/", postLogoutRedirectUriOf(handler),
                "Absolute logout URIs must be passed through verbatim (no {baseUrl} prefix)");
    }

    @Test
    public void oidcLogoutSuccessHandlerWithRelativePathPrependsBaseUrlPlaceholder() throws Exception {
        when(kindeConfig.logoutRedirectUri()).thenReturn("/post-logout");

        OidcClientInitiatedLogoutSuccessHandler handler =
                kindeOAuth2AutoConfig.oidcLogoutSuccessHandler(clientRegistrationRepository);

        assertNotNull(handler);
        assertEquals("{baseUrl}/post-logout", postLogoutRedirectUriOf(handler),
                "Relative logout paths must be prefixed with {baseUrl} so Spring resolves them");
    }

    // --- user-service factories ---------------------------------------------------------------

    @Test
    public void oAuth2UserServiceProducesKindeOAuth2UserService() {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> service =
                kindeOAuth2AutoConfig.oAuth2UserService(List.of());

        assertNotNull(service);
        assertInstanceOf(KindeOAuth2UserService.class, service);
    }

    @Test
    public void oidcUserServiceProducesKindeOidcUserService() {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService =
                kindeOAuth2AutoConfig.oAuth2UserService(List.of());

        OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService =
                kindeOAuth2AutoConfig.oidcUserService(oAuth2UserService, List.of());

        assertNotNull(oidcUserService);
        assertInstanceOf(KindeOidcUserService.class, oidcUserService);
    }

    // --- OAuth2SecurityFilterChainConfiguration ------------------------------------------------

    /**
     * Exercises the inner {@code OAuth2SecurityFilterChainConfiguration#oauth2SecurityFilterChain(...)}
     * factory by mocking {@link HttpSecurity} and driving the {@code authorizeHttpRequests} customizer
     * through a Mockito {@code Answer}, so the {@code requests.anyRequest().authenticated()} lambda
     * body actually executes under JaCoCo. The other DSL calls ({@code oauth2Login},
     * {@code oauth2Client}, {@code oauth2ResourceServer}) only need to be verified at the call-site
     * level since their lambda bodies are covered by other tests / are simple {@code Customizer.withDefaults()}.
     */
    @Test
    @SuppressWarnings("rawtypes") // raw types unavoidable on Spring Security's owner-parameterized inner DSL types
    public void oauth2SecurityFilterChainBuildsTheChainAndDelegatesToTheDsl() throws Exception {
        HttpSecurity http = mock(HttpSecurity.class);
        // HttpSecurity#build() returns DefaultSecurityFilterChain, not the SecurityFilterChain
        // interface, so the mock has to match that concrete type.
        DefaultSecurityFilterChain expectedChain = mock(DefaultSecurityFilterChain.class);
        when(http.build()).thenReturn(expectedChain);

        // Drive the inner customizer for authorizeHttpRequests so the
        // `requests.anyRequest().authenticated()` body fires.
        AuthorizeHttpRequestsConfigurer.AuthorizationManagerRequestMatcherRegistry registry =
                mock(AuthorizeHttpRequestsConfigurer.AuthorizationManagerRequestMatcherRegistry.class);
        AuthorizeHttpRequestsConfigurer.AuthorizedUrl authorizedUrl =
                mock(AuthorizeHttpRequestsConfigurer.AuthorizedUrl.class);
        when(registry.anyRequest()).thenReturn(authorizedUrl);
        when(authorizedUrl.authenticated()).thenReturn(registry);
        when(http.authorizeHttpRequests(any())).thenAnswer(invocation -> {
            Customizer<AuthorizeHttpRequestsConfigurer.AuthorizationManagerRequestMatcherRegistry> customizer =
                    invocation.getArgument(0);
            customizer.customize(registry);
            return http;
        });

        // The remaining DSL calls just record the customizer; we verify call counts below.
        when(http.oauth2Login(any())).thenReturn(http);
        when(http.oauth2Client(any())).thenReturn(http);
        when(http.oauth2ResourceServer(any())).thenReturn(http);

        ClientRegistrationRepository repo = mock(ClientRegistrationRepository.class);
        KindeOAuth2AutoConfig.OAuth2SecurityFilterChainConfiguration config =
                new KindeOAuth2AutoConfig.OAuth2SecurityFilterChainConfiguration();

        SecurityFilterChain chain = config.oauth2SecurityFilterChain(http, repo);

        assertSame(expectedChain, chain, "should return the chain produced by http.build()");
        verify(http).authorizeHttpRequests(any());
        verify(registry).anyRequest();
        verify(authorizedUrl).authenticated();
        // oauth2Login is invoked exactly once - by Kinde.configureOAuth2WithPkce - in this chain.
        verify(http, atLeastOnce()).oauth2Login(any());
        verify(http).oauth2Client(any());
        verify(http).oauth2ResourceServer(any());
        verify(http).build();
    }

    /**
     * Reads the package-private {@code postLogoutRedirectUri} field of
     * {@link OidcClientInitiatedLogoutSuccessHandler} via reflection so tests can assert
     * the exact URI that the autoconfig installed on the handler.
     */
    private static String postLogoutRedirectUriOf(OidcClientInitiatedLogoutSuccessHandler handler) throws Exception {
        Field field = OidcClientInitiatedLogoutSuccessHandler.class.getDeclaredField("postLogoutRedirectUri");
        field.setAccessible(true);
        Object value = field.get(handler);
        return value == null ? null : value.toString();
    }
}
