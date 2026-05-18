package com.kinde.spring;

import com.kinde.spring.config.KindeOAuth2Properties;
import org.junit.jupiter.api.Test;
import org.springframework.boot.security.oauth2.client.autoconfigure.OAuth2ClientProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the {@link KindeOAuth2Configurer} {@code init} flow.
 * <p>
 * Spring Security 7's {@code HttpSecurity#oauth2Login()/logout()/oauth2ResourceServer()} API is
 * Customizer-lambda based, so to drive JaCoCo through the configurer's lambda bodies we use
 * Mockito {@code Answer}s that actually invoke the captured {@link Customizer}s against mocked
 * sub-configurers. The short-circuit branches that bail out before touching the DSL are exercised
 * by the simpler "noop" tests at the top.
 */
public class KindeOAuth2ConfigurerTest {

    private static final String VALID_ISSUER = "https://test.kinde.com";
    private static final String VALID_CLIENT_ID = "test-client-id";

    @Test
    public void initWithoutKindeOAuth2PropertiesIsNoop() {
        KindeOAuth2Configurer configurer = new KindeOAuth2Configurer();
        HttpSecurity httpSecurity = mock(HttpSecurity.class);
        ApplicationContext context = mock(ApplicationContext.class);
        when(httpSecurity.getSharedObject(ApplicationContext.class)).thenReturn(context);
        when(context.getBeansOfType(KindeOAuth2Properties.class)).thenReturn(Collections.emptyMap());

        configurer.init(httpSecurity);

        verify(httpSecurity, never()).oauth2Login(any());
    }

    @Test
    public void initWithoutKindeRegistrationIsNoop() {
        KindeOAuth2Configurer configurer = new KindeOAuth2Configurer();
        HttpSecurity httpSecurity = mock(HttpSecurity.class);
        ApplicationContext context = mock(ApplicationContext.class);
        when(httpSecurity.getSharedObject(ApplicationContext.class)).thenReturn(context);

        seedKindeProperties(context, mock(KindeOAuth2Properties.class));
        when(context.getBeansOfType(OAuth2ClientProperties.class)).thenReturn(Collections.emptyMap());

        configurer.init(httpSecurity);

        verify(httpSecurity, never()).oauth2Login(any());
    }

    @Test
    public void initWithMissingKindeProviderIsNoop() {
        Fixture fx = happyPathFixture();
        when(fx.oauth2ClientProperties.getProvider()).thenReturn(Collections.emptyMap());

        new KindeOAuth2Configurer().init(fx.httpSecurity);

        verify(fx.httpSecurity, never()).oauth2Login(any());
    }

    @Test
    public void initWithMissingKindeClientRegistrationIsNoop() {
        Fixture fx = happyPathFixture();
        when(fx.oauth2ClientProperties.getRegistration()).thenReturn(Collections.emptyMap());

        new KindeOAuth2Configurer().init(fx.httpSecurity);

        verify(fx.httpSecurity, never()).oauth2Login(any());
    }

    @Test
    public void initWithBlankIssuerUriIsNoop() {
        Fixture fx = happyPathFixture();
        when(fx.provider.getIssuerUri()).thenReturn("");

        new KindeOAuth2Configurer().init(fx.httpSecurity);

        verify(fx.httpSecurity, never()).oauth2Login(any());
    }

    @Test
    public void initWithBlankClientIdIsNoop() {
        Fixture fx = happyPathFixture();
        when(fx.registration.getClientId()).thenReturn("");

        new KindeOAuth2Configurer().init(fx.httpSecurity);

        verify(fx.httpSecurity, never()).oauth2Login(any());
    }

    @Test
    public void initHappyPathConfiguresOAuth2Login() {
        Fixture fx = happyPathFixture();

        new KindeOAuth2Configurer().init(fx.httpSecurity);

        verify(fx.httpSecurity).oauth2Login(any());
        verify(fx.oauth2LoginConfigurer).tokenEndpoint(any());
        verify(fx.tokenEndpoint).accessTokenResponseClient(any());
        // No redirect URI configured, so redirectionEndpoint should NOT be touched.
        verify(fx.oauth2LoginConfigurer, never()).redirectionEndpoint(any());
        // No logout handler bean, so logout() should NOT be wired.
        verify(fx.httpSecurity, never()).logout(any());
    }

    @Test
    public void initHappyPathHonorsRedirectUriPropertyAndStripsBaseUrlPlaceholder() {
        Fixture fx = happyPathFixture();
        when(fx.environment.getProperty("spring.security.oauth2.client.registration.kinde.redirect-uri"))
                .thenReturn("{baseUrl}/login/oauth2/code/kinde");

        new KindeOAuth2Configurer().init(fx.httpSecurity);

        verify(fx.httpSecurity).oauth2Login(any());
        verify(fx.oauth2LoginConfigurer).redirectionEndpoint(any());
        verify(fx.redirectionEndpoint).baseUri("/login/oauth2/code/kinde");
    }

    @Test
    public void initHappyPathWiresOidcLogoutHandlerWhenBeanPresent() {
        Fixture fx = happyPathFixture();
        OidcClientInitiatedLogoutSuccessHandler handler = mock(OidcClientInitiatedLogoutSuccessHandler.class);
        Map<String, OidcClientInitiatedLogoutSuccessHandler> handlerMap = new HashMap<>();
        handlerMap.put("oidcLogoutSuccessHandler", handler);
        when(fx.context.getBeansOfType(OidcClientInitiatedLogoutSuccessHandler.class)).thenReturn(handlerMap);
        when(fx.context.getBean(OidcClientInitiatedLogoutSuccessHandler.class)).thenReturn(handler);

        new KindeOAuth2Configurer().init(fx.httpSecurity);

        verify(fx.httpSecurity).logout(any());
        verify(fx.logoutConfigurer).logoutSuccessHandler(handler);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void initHappyPathWithJwtResourceServerInvokesJwtBranch() {
        Fixture fx = happyPathFixture();
        OAuth2ResourceServerConfigurer<?> resourceServerConfigurer = mock(OAuth2ResourceServerConfigurer.class);
        // Plant a non-null jwtConfigurer so the configurer's reflection-based getJwtConfigurer()
        // returns non-empty and the JWT branch fires. The field is strongly typed, so the value
        // has to be an actual mock of the (non-static) inner JwtConfigurer.
        OAuth2ResourceServerConfigurer<?>.JwtConfigurer jwtConfigurer =
                mock(OAuth2ResourceServerConfigurer.JwtConfigurer.class);
        setReflectionField(resourceServerConfigurer, "jwtConfigurer", jwtConfigurer);
        when(fx.httpSecurity.getConfigurer(OAuth2ResourceServerConfigurer.class))
                .thenAnswer(inv -> resourceServerConfigurer);

        // Drive the customizer passed to http.oauth2ResourceServer(...) so the nested
        // jwt(jwt -> jwt.jwtAuthenticationConverter(...)) lambda actually executes.
        OAuth2ResourceServerConfigurer<HttpSecurity> rsCustomizerTarget =
                (OAuth2ResourceServerConfigurer<HttpSecurity>) mock(OAuth2ResourceServerConfigurer.class);
        OAuth2ResourceServerConfigurer<HttpSecurity>.JwtConfigurer jwtCustomizerTarget =
                mock(OAuth2ResourceServerConfigurer.JwtConfigurer.class);
        when(rsCustomizerTarget.jwt(any())).thenAnswer(inv -> {
            Customizer<OAuth2ResourceServerConfigurer<HttpSecurity>.JwtConfigurer> inner = inv.getArgument(0);
            inner.customize(jwtCustomizerTarget);
            return rsCustomizerTarget;
        });
        when(jwtCustomizerTarget.jwtAuthenticationConverter(any())).thenReturn(jwtCustomizerTarget);
        when(fx.httpSecurity.oauth2ResourceServer(any())).thenAnswer(inv -> {
            Customizer<OAuth2ResourceServerConfigurer<HttpSecurity>> customizer = inv.getArgument(0);
            customizer.customize(rsCustomizerTarget);
            return fx.httpSecurity;
        });

        new KindeOAuth2Configurer().init(fx.httpSecurity);

        verify(fx.httpSecurity).oauth2ResourceServer(any());
        verify(rsCustomizerTarget).jwt(any());
        verify(jwtCustomizerTarget).jwtAuthenticationConverter(any());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void initHappyPathWithOpaqueTokenResourceServerInvokesOpaqueBranchAndUnsetsJwt() {
        Fixture fx = happyPathFixture();
        // Opaque branch also reads clientId/clientSecret off KindeOAuth2Properties; non-empty
        // values are required to actually call http.oauth2ResourceServer(...).
        when(fx.kindeProperties.getClientId()).thenReturn(VALID_CLIENT_ID);
        when(fx.kindeProperties.getClientSecret()).thenReturn("test-client-secret");

        OAuth2ResourceServerConfigurer<?> resourceServerConfigurer = mock(OAuth2ResourceServerConfigurer.class);
        // jwtConfigurer left null so init() bypasses the JWT branch and falls through to the
        // opaque branch. Plant a non-null opaqueTokenConfigurer so getOpaqueTokenConfigurer()
        // returns non-empty.
        OAuth2ResourceServerConfigurer<?>.OpaqueTokenConfigurer opaqueConfigurer =
                mock(OAuth2ResourceServerConfigurer.OpaqueTokenConfigurer.class);
        setReflectionField(resourceServerConfigurer, "opaqueTokenConfigurer", opaqueConfigurer);

        when(fx.httpSecurity.getConfigurer(OAuth2ResourceServerConfigurer.class))
                .thenAnswer(inv -> resourceServerConfigurer);

        // Drive the customizer for oauth2ResourceServer(...) so we walk through the opaque() call.
        OAuth2ResourceServerConfigurer<HttpSecurity> rsCustomizerTarget =
                (OAuth2ResourceServerConfigurer<HttpSecurity>) mock(OAuth2ResourceServerConfigurer.class);
        OAuth2ResourceServerConfigurer<HttpSecurity>.OpaqueTokenConfigurer opaqueCustomizerTarget =
                mock(OAuth2ResourceServerConfigurer.OpaqueTokenConfigurer.class);
        when(rsCustomizerTarget.opaqueToken(any())).thenAnswer(inv -> {
            Customizer<OAuth2ResourceServerConfigurer<HttpSecurity>.OpaqueTokenConfigurer> inner = inv.getArgument(0);
            inner.customize(opaqueCustomizerTarget);
            return rsCustomizerTarget;
        });
        when(fx.httpSecurity.oauth2ResourceServer(any())).thenAnswer(inv -> {
            Customizer<OAuth2ResourceServerConfigurer<HttpSecurity>> customizer = inv.getArgument(0);
            customizer.customize(rsCustomizerTarget);
            return fx.httpSecurity;
        });

        new KindeOAuth2Configurer().init(fx.httpSecurity);

        verify(fx.httpSecurity).oauth2ResourceServer(any());
        verify(rsCustomizerTarget).opaqueToken(any());
    }

    // --- helpers ---------------------------------------------------------------------

    private void seedKindeProperties(ApplicationContext context, KindeOAuth2Properties kindeOAuth2Properties) {
        Map<String, KindeOAuth2Properties> kindePropsMap = new HashMap<>();
        kindePropsMap.put(KindeOAuth2Properties.class.getName(), kindeOAuth2Properties);
        when(context.getBeansOfType(KindeOAuth2Properties.class)).thenReturn(kindePropsMap);
        when(context.getBean(KindeOAuth2Properties.class)).thenReturn(kindeOAuth2Properties);
    }

    /**
     * Sets a (possibly inherited) private field via reflection. Used to plant a mock value on
     * the {@code jwtConfigurer} field of {@link OAuth2ResourceServerConfigurer} so the
     * configurer's reflection-based lookup finds it and routes into the JWT branch.
     */
    @SuppressWarnings("SameParameterValue")
    private static void setReflectionField(Object target, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = OAuth2ResourceServerConfigurer.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to set " + fieldName + " via reflection", e);
        }
    }

    /**
     * Wires up all the Mockito {@link org.mockito.stubbing.Answer}s required to walk the
     * {@code init()} happy path:
     * <ul>
     *   <li>KindeOAuth2Properties bean + getPermissionsClaim()</li>
     *   <li>OAuth2ClientProperties bean with a valid {@code kinde} provider/registration</li>
     *   <li>{@code http.oauth2Login(...)} invokes its customizer against a mocked
     *       {@link OAuth2LoginConfigurer}, which in turn invokes its inner customizers for
     *       {@code tokenEndpoint} and {@code redirectionEndpoint}</li>
     *   <li>{@code http.logout(...)} invokes its customizer against a mocked
     *       {@link LogoutConfigurer}</li>
     *   <li>{@code http.oauth2ResourceServer(...)} invokes its customizer against a mocked
     *       {@link OAuth2ResourceServerConfigurer} (and its nested jwt() customizer)</li>
     *   <li>No logout handler bean is present and no resource-server configurer is wired,
     *       so individual tests can opt-in to those branches</li>
     * </ul>
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private Fixture happyPathFixture() {
        Fixture fx = new Fixture();
        fx.httpSecurity = mock(HttpSecurity.class);
        fx.context = mock(ApplicationContext.class);
        fx.environment = mock(Environment.class);
        when(fx.httpSecurity.getSharedObject(ApplicationContext.class)).thenReturn(fx.context);
        when(fx.context.getEnvironment()).thenReturn(fx.environment);

        fx.kindeProperties = mock(KindeOAuth2Properties.class);
        when(fx.kindeProperties.getPermissionsClaim()).thenReturn("permissions");
        when(fx.kindeProperties.getProxy()).thenReturn(null);
        seedKindeProperties(fx.context, fx.kindeProperties);

        fx.oauth2ClientProperties = mock(OAuth2ClientProperties.class);
        fx.provider = mock(OAuth2ClientProperties.Provider.class);
        when(fx.provider.getIssuerUri()).thenReturn(VALID_ISSUER);
        Map<String, OAuth2ClientProperties.Provider> providerMap = new HashMap<>();
        providerMap.put("kinde", fx.provider);
        when(fx.oauth2ClientProperties.getProvider()).thenReturn(providerMap);

        fx.registration = mock(OAuth2ClientProperties.Registration.class);
        when(fx.registration.getClientId()).thenReturn(VALID_CLIENT_ID);
        Map<String, OAuth2ClientProperties.Registration> registrationMap = new HashMap<>();
        registrationMap.put("kinde", fx.registration);
        when(fx.oauth2ClientProperties.getRegistration()).thenReturn(registrationMap);

        Map<String, OAuth2ClientProperties> oauth2ClientPropsMap = new HashMap<>();
        oauth2ClientPropsMap.put("oauth2ClientProperties", fx.oauth2ClientProperties);
        when(fx.context.getBeansOfType(OAuth2ClientProperties.class)).thenReturn(oauth2ClientPropsMap);
        when(fx.context.getBean(OAuth2ClientProperties.class)).thenReturn(fx.oauth2ClientProperties);

        // Default: no RP-Initiated logout handler bean configured.
        when(fx.context.getBeansOfType(OidcClientInitiatedLogoutSuccessHandler.class))
                .thenReturn(Collections.emptyMap());

        // Default: no resource-server configurer wired, so the JWT/Opaque branches are skipped.
        when(fx.httpSecurity.getConfigurer(OAuth2ResourceServerConfigurer.class)).thenReturn(null);

        // --- oauth2Login customizer drives tokenEndpoint(...) and (optionally) redirectionEndpoint(...).
        fx.oauth2LoginConfigurer = mock(OAuth2LoginConfigurer.class);
        fx.tokenEndpoint = mock(OAuth2LoginConfigurer.TokenEndpointConfig.class);
        fx.redirectionEndpoint = mock(OAuth2LoginConfigurer.RedirectionEndpointConfig.class);
        when(fx.oauth2LoginConfigurer.tokenEndpoint(any())).thenAnswer(inv -> {
            Customizer<OAuth2LoginConfigurer.TokenEndpointConfig> inner = inv.getArgument(0);
            inner.customize(fx.tokenEndpoint);
            return fx.oauth2LoginConfigurer;
        });
        when(fx.tokenEndpoint.accessTokenResponseClient(any())).thenReturn(fx.tokenEndpoint);
        when(fx.oauth2LoginConfigurer.redirectionEndpoint(any())).thenAnswer(inv -> {
            Customizer<OAuth2LoginConfigurer.RedirectionEndpointConfig> inner = inv.getArgument(0);
            inner.customize(fx.redirectionEndpoint);
            return fx.oauth2LoginConfigurer;
        });
        when(fx.redirectionEndpoint.baseUri(any())).thenReturn(fx.redirectionEndpoint);
        when(fx.httpSecurity.oauth2Login(any())).thenAnswer(inv -> {
            Customizer<OAuth2LoginConfigurer<HttpSecurity>> customizer = inv.getArgument(0);
            customizer.customize(fx.oauth2LoginConfigurer);
            return fx.httpSecurity;
        });

        // --- logout customizer drives logoutSuccessHandler(...)
        fx.logoutConfigurer = mock(LogoutConfigurer.class);
        when(fx.logoutConfigurer.logoutSuccessHandler(any())).thenReturn(fx.logoutConfigurer);
        when(fx.httpSecurity.logout(any())).thenAnswer(inv -> {
            Customizer<LogoutConfigurer<HttpSecurity>> customizer = inv.getArgument(0);
            customizer.customize(fx.logoutConfigurer);
            return fx.httpSecurity;
        });

        // --- oauth2ResourceServer customizer just no-ops; we only need the outer call to be recorded
        // so the JWT-branch verify() succeeds. The nested jwt(...) customizer is not invoked to
        // avoid having to mock OAuth2ResourceServerConfigurer.JwtConfigurer too.
        when(fx.httpSecurity.oauth2ResourceServer(any())).thenReturn(fx.httpSecurity);

        return fx;
    }

    /** Bag of mocks/state used by happy-path-style tests. */
    private static final class Fixture {
        HttpSecurity httpSecurity;
        ApplicationContext context;
        Environment environment;
        KindeOAuth2Properties kindeProperties;
        OAuth2ClientProperties oauth2ClientProperties;
        OAuth2ClientProperties.Provider provider;
        OAuth2ClientProperties.Registration registration;
        OAuth2LoginConfigurer<HttpSecurity> oauth2LoginConfigurer;
        OAuth2LoginConfigurer.TokenEndpointConfig tokenEndpoint;
        OAuth2LoginConfigurer.RedirectionEndpointConfig redirectionEndpoint;
        LogoutConfigurer<HttpSecurity> logoutConfigurer;
    }
}
