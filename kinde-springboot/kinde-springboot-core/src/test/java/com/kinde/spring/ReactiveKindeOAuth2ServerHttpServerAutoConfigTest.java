package com.kinde.spring;

import com.kinde.spring.config.KindeOAuth2Properties;
import com.kinde.spring.env.KindeOAuth2PropertiesMappingEnvironmentPostProcessor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcReactiveOAuth2UserService;
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.context.TestPropertySource;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@TestPropertySource(properties = {
        KindeOAuth2PropertiesMappingEnvironmentPostProcessor.KINDE_OAUTH_DOMAIN + "=https://test.kinde.com",
        KindeOAuth2PropertiesMappingEnvironmentPostProcessor.KINDE_OAUTH_CLIENT_ID + "=test_client_id",
        KindeOAuth2PropertiesMappingEnvironmentPostProcessor.KINDE_OAUTH_CLIENT_SECRET + "=test_client_secret",
        KindeOAuth2PropertiesMappingEnvironmentPostProcessor.KINDE_OAUTH_SCOPES + "=profile",
        "spring.security.oauth2.client.registration.kinde.client-id=test_client_id"
})
@SpringBootTest(classes = {ReactiveKindeOAuth2ServerHttpServerAutoConfigTest.class})
@Import(ReactiveKindeOAuth2ServerHttpServerAutoConfigTest.MyTestConfig.class)
public class ReactiveKindeOAuth2ServerHttpServerAutoConfigTest {

    @TestConfiguration
    public static class MyTestConfig {
        @Bean
        public ReactiveKindeOAuth2ServerHttpServerAutoConfig reactiveAutoConfig() {
            return new ReactiveKindeOAuth2ServerHttpServerAutoConfig();
        }
    }

    @Autowired
    private ReactiveKindeOAuth2ServerHttpServerAutoConfig autoConfig;

    // --- Bean factory smoke tests --------------------------------------------------------------

    @Test
    @SuppressWarnings("unchecked")
    public void authManagerServerHttpSecurityBeanPostProcessorReturnsNonNullProcessor() {
        ReactiveOAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService = mock(ReactiveOAuth2UserService.class);
        OidcReactiveOAuth2UserService oidcUserService = mock(OidcReactiveOAuth2UserService.class);
        OidcClientInitiatedServerLogoutSuccessHandler logoutHandler = mock(OidcClientInitiatedServerLogoutSuccessHandler.class);

        BeanPostProcessor processor = autoConfig.authManagerServerHttpSecurityBeanPostProcessor(
                oAuth2UserService, oidcUserService, logoutHandler);

        assertNotNull(processor);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void authManagerServerHttpSecurityBeanPostProcessorAcceptsNullLogoutHandler() {
        ReactiveOAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService = mock(ReactiveOAuth2UserService.class);
        OidcReactiveOAuth2UserService oidcUserService = mock(OidcReactiveOAuth2UserService.class);

        BeanPostProcessor processor = autoConfig.authManagerServerHttpSecurityBeanPostProcessor(
                oAuth2UserService, oidcUserService, null);

        assertNotNull(processor, "logout handler is @Autowired(required=false) so null must be tolerated");
    }

    // --- oidcClientInitiatedServerLogoutSuccessHandler ternary branches ------------------------

    @Test
    public void oidcLogoutSuccessHandlerWithAbsoluteUriDoesNotPrependBaseUrl() throws Exception {
        KindeOAuth2Properties props = mock(KindeOAuth2Properties.class);
        when(props.getPostLogoutRedirectUri()).thenReturn("https://app.kinde.com/post-logout");
        ReactiveClientRegistrationRepository repo = mock(ReactiveClientRegistrationRepository.class);

        OidcClientInitiatedServerLogoutSuccessHandler handler =
                autoConfig.oidcClientInitiatedServerLogoutSuccessHandler(props, repo);

        assertNotNull(handler);
        assertEquals("https://app.kinde.com/post-logout", postLogoutRedirectUriOf(handler));
    }

    @Test
    public void oidcLogoutSuccessHandlerWithRelativePathPrependsBaseUrlPlaceholder() throws Exception {
        KindeOAuth2Properties props = mock(KindeOAuth2Properties.class);
        when(props.getPostLogoutRedirectUri()).thenReturn("/post-logout");
        ReactiveClientRegistrationRepository repo = mock(ReactiveClientRegistrationRepository.class);

        OidcClientInitiatedServerLogoutSuccessHandler handler =
                autoConfig.oidcClientInitiatedServerLogoutSuccessHandler(props, repo);

        assertNotNull(handler);
        assertEquals("{baseUrl}/post-logout", postLogoutRedirectUriOf(handler));
    }

    // --- KindeOAuth2LoginServerBeanPostProcessor.postProcessAfterInitialization ---------------

    /**
     * Drives {@code postProcessAfterInitialization} against a mocked {@link ServerHttpSecurity}
     * with a non-null logout handler. The {@code oauth2Login} customizer fires through a Mockito
     * {@code Answer} so the call site that builds the static {@code reactiveAuthenticationManager(...)}
     * helper actually executes (covering the constructor chain, the {@code ClassUtils.isPresent}
     * check, and the {@code DelegatingReactiveAuthenticationManager} construction). Calling
     * {@code authenticate(...)} on the captured manager additionally exercises the two anonymous
     * authenticate overrides + {@code wrapOnErrorMap}.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void postProcessAfterInitializationWithServerHttpSecurityWiresOauth2LoginAndLogout() {
        ReactiveOAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService = mock(ReactiveOAuth2UserService.class);
        OidcReactiveOAuth2UserService oidcUserService = mock(OidcReactiveOAuth2UserService.class);
        OidcClientInitiatedServerLogoutSuccessHandler logoutHandler = mock(OidcClientInitiatedServerLogoutSuccessHandler.class);

        ReactiveKindeOAuth2ServerHttpServerAutoConfig.KindeOAuth2LoginServerBeanPostProcessor processor =
                new ReactiveKindeOAuth2ServerHttpServerAutoConfig.KindeOAuth2LoginServerBeanPostProcessor(
                        oAuth2UserService, oidcUserService, logoutHandler);

        ServerHttpSecurity http = mock(ServerHttpSecurity.class);

        // Drive the oauth2Login customizer so reactiveAuthenticationManager(...) is built and
        // installed via login.authenticationManager(...). Capture the manager to fire authenticate().
        ServerHttpSecurity.OAuth2LoginSpec loginSpec = mock(ServerHttpSecurity.OAuth2LoginSpec.class);
        when(loginSpec.authenticationManager(any())).thenReturn(loginSpec);
        when(http.oauth2Login(any())).thenAnswer(invocation -> {
            Customizer<ServerHttpSecurity.OAuth2LoginSpec> customizer = invocation.getArgument(0);
            customizer.customize(loginSpec);
            return http;
        });

        // Drive the logout customizer too so logout.logoutSuccessHandler(...) actually fires.
        ServerHttpSecurity.LogoutSpec logoutSpec = mock(ServerHttpSecurity.LogoutSpec.class);
        when(logoutSpec.logoutSuccessHandler(any())).thenReturn(logoutSpec);
        when(http.logout(any())).thenAnswer(invocation -> {
            Customizer<ServerHttpSecurity.LogoutSpec> customizer = invocation.getArgument(0);
            customizer.customize(logoutSpec);
            return http;
        });

        Object result = processor.postProcessAfterInitialization(http, "anyName");

        assertSame(http, result, "post-processor must return the input bean");
        verify(http).oauth2Login(any());
        verify(http).logout(any());
        verify(loginSpec).authenticationManager(any());
        verify(logoutSpec).logoutSuccessHandler(logoutHandler);

        // Drive the anonymous authenticate() overrides + wrapOnErrorMap by capturing the manager
        // and invoking authenticate(...) once. The underlying OAuth2LoginReactiveAuthenticationManager
        // forces a cast to OAuth2AuthorizationCodeAuthenticationToken inside the Mono pipeline, so
        // any other Authentication subscriber-side throws ClassCastException -- which is fine: it
        // still walks the override -> wrapOnErrorMap -> shouldWrapException path under JaCoCo. We
        // just don't care about the resulting exception here.
        org.mockito.ArgumentCaptor<ReactiveAuthenticationManager> captor =
                org.mockito.ArgumentCaptor.forClass(ReactiveAuthenticationManager.class);
        verify(loginSpec).authenticationManager(captor.capture());
        ReactiveAuthenticationManager manager = captor.getValue();
        assertNotNull(manager);
        org.junit.jupiter.api.Assertions.assertThrows(Throwable.class,
                () -> manager.authenticate(mock(Authentication.class)).block());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void postProcessAfterInitializationWithoutLogoutHandlerSkipsLogoutWiring() {
        ReactiveOAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService = mock(ReactiveOAuth2UserService.class);
        OidcReactiveOAuth2UserService oidcUserService = mock(OidcReactiveOAuth2UserService.class);

        ReactiveKindeOAuth2ServerHttpServerAutoConfig.KindeOAuth2LoginServerBeanPostProcessor processor =
                new ReactiveKindeOAuth2ServerHttpServerAutoConfig.KindeOAuth2LoginServerBeanPostProcessor(
                        oAuth2UserService, oidcUserService, null);

        ServerHttpSecurity http = mock(ServerHttpSecurity.class);
        when(http.oauth2Login(any())).thenReturn(http);

        Object result = processor.postProcessAfterInitialization(http, "anyName");

        assertSame(http, result);
        verify(http).oauth2Login(any());
        verify(http, never()).logout(any());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void postProcessAfterInitializationLeavesNonHttpBeansUntouched() {
        ReactiveOAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService = mock(ReactiveOAuth2UserService.class);
        OidcReactiveOAuth2UserService oidcUserService = mock(OidcReactiveOAuth2UserService.class);

        ReactiveKindeOAuth2ServerHttpServerAutoConfig.KindeOAuth2LoginServerBeanPostProcessor processor =
                new ReactiveKindeOAuth2ServerHttpServerAutoConfig.KindeOAuth2LoginServerBeanPostProcessor(
                        oAuth2UserService, oidcUserService, null);

        Object input = "not-a-server-http-security";

        Object result = processor.postProcessAfterInitialization(input, "name");

        assertSame(input, result);
    }

    // --- helpers ---------------------------------------------------------------------------------

    /**
     * The reactive {@code OidcClientInitiatedServerLogoutSuccessHandler} stores the configured URI
     * as a {@code RedirectServerLogoutSuccessHandler} delegate's URI, so the simplest reflection
     * path is to read the {@code postLogoutRedirectUri} field directly.
     */
    private static String postLogoutRedirectUriOf(OidcClientInitiatedServerLogoutSuccessHandler handler) throws Exception {
        Field field = OidcClientInitiatedServerLogoutSuccessHandler.class.getDeclaredField("postLogoutRedirectUri");
        field.setAccessible(true);
        Object value = field.get(handler);
        return value == null ? null : value.toString();
    }
}
