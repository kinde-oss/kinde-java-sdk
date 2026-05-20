package com.kinde.spring;

import com.kinde.spring.config.KindeOAuth2Properties;
import com.kinde.spring.env.KindeOAuth2PropertiesMappingEnvironmentPostProcessor;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.OAuth2ResourceServerProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.mock.http.client.MockClientHttpRequest;
import org.springframework.mock.http.client.MockClientHttpResponse;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@TestPropertySource(properties = {
        KindeOAuth2PropertiesMappingEnvironmentPostProcessor.KINDE_OAUTH_DOMAIN + "=https://test.kinde.com" ,
        KindeOAuth2PropertiesMappingEnvironmentPostProcessor.KINDE_OAUTH_CLIENT_ID + "=test_client_id" ,
        KindeOAuth2PropertiesMappingEnvironmentPostProcessor.KINDE_OAUTH_CLIENT_SECRET + "=test_client_secret" ,
        KindeOAuth2PropertiesMappingEnvironmentPostProcessor.KINDE_OAUTH_SCOPES + "=profile" ,
        "spring.security.oauth2.client.registration.kinde.client-id=test_client_id"
})
@SpringBootTest(classes = {KindeOAuth2ResourceServerAutoConfigTest.class})
@Import(KindeOAuth2ResourceServerAutoConfigTest.MyTestConfig.class)
public class KindeOAuth2ResourceServerAutoConfigTest {

    @TestConfiguration
    public static class MyTestConfig {
        @Bean
        public KindeOAuth2ResourceServerAutoConfig kindeOAuth2ResourceServerAutoConfig() {
            return new KindeOAuth2ResourceServerAutoConfig();
        }
    }


    @Autowired
    private KindeOAuth2ResourceServerAutoConfig kindeOAuth2ResourceServerAutoConfig;


    @Test
    public void testJwtAuthenticationConverter() {
        KindeOAuth2Properties kindeOAuth2Properties = Mockito.mock(KindeOAuth2Properties.class);
        kindeOAuth2ResourceServerAutoConfig.jwtAuthenticationConverter(kindeOAuth2Properties);
    }

    @Test
    public void jwtDecoder() {
        OAuth2ResourceServerProperties oAuth2ResourceServerProperties = Mockito.mock(OAuth2ResourceServerProperties.class);
        KindeOAuth2Properties kindeOAuth2Properties = Mockito.mock(KindeOAuth2Properties.class);
        OAuth2ResourceServerProperties.Jwt jwt = Mockito.mock(OAuth2ResourceServerProperties.Jwt.class);
        when(oAuth2ResourceServerProperties.getJwt()).thenReturn(jwt);
        when(jwt.getJwkSetUri()).thenReturn("http://test.kinde.com");
        when(jwt.getIssuerUri()).thenReturn("http://test.kinde.com");
        when(kindeOAuth2Properties.getAudience()).thenReturn("http://test.kinde.com/test");
        kindeOAuth2ResourceServerAutoConfig.jwtDecoder(oAuth2ResourceServerProperties,kindeOAuth2Properties);
    }

    // --- restClient() / restTemplate() proxy-branch coverage ----------------------------------
    //
    // These tests assert that the proxy host/port and (when present) the basic-auth credentials
    // are actually wired into the produced RestClient / RestTemplate. Asserting non-null on the
    // factory result alone would only confirm "no exception during construction" and would let a
    // future regression that silently drops proxy.setProxy(...) or the auth interceptor go
    // unnoticed.
    //
    // RestClient has no public accessor for its request factory or interceptors, so we walk the
    // DefaultRestClient fields by type rather than by name (resilient to Spring-internal renames).
    // For the basic-auth interceptor we exercise it against a MockClientHttpRequest and assert on
    // the resulting Authorization header instead of reflecting into the interceptor's internals.

    private static final String EXPECTED_PROXY_HOST = "proxy.example.com";
    private static final int EXPECTED_PROXY_PORT = 8080;
    private static final String PROXY_USER = "proxy-user";
    private static final String PROXY_PASS = "proxy-pass";

    @Test
    public void restClientWithNoProxyConfigured() {
        KindeOAuth2Properties props = Mockito.mock(KindeOAuth2Properties.class);
        when(props.getProxy()).thenReturn(null);

        RestClient client = KindeOAuth2ResourceServerAutoConfig.restClient(props);
        assertNotNull(client);

        assertNull(proxyOf(requestFactoryOf(client)),
                "Expected no proxy on the request factory when properties#getProxy() is null");
        assertFalse(hasBasicAuthInterceptor(interceptorsOf(client)),
                "Expected no BasicAuthenticationInterceptor when no proxy is configured");
    }

    @Test
    public void restClientWithProxyHostAndPort() {
        KindeOAuth2Properties props = propertiesWithProxy(EXPECTED_PROXY_HOST, EXPECTED_PROXY_PORT, "", "");

        RestClient client = KindeOAuth2ResourceServerAutoConfig.restClient(props);
        assertProxyAddress(proxyOf(requestFactoryOf(client)));
        assertFalse(hasBasicAuthInterceptor(interceptorsOf(client)),
                "Expected no BasicAuthenticationInterceptor when username/password are blank");
    }

    @Test
    public void restClientWithAuthenticatedProxy() throws Exception {
        KindeOAuth2Properties props = propertiesWithProxy(EXPECTED_PROXY_HOST, EXPECTED_PROXY_PORT, PROXY_USER, PROXY_PASS);

        RestClient client = KindeOAuth2ResourceServerAutoConfig.restClient(props);
        assertProxyAddress(proxyOf(requestFactoryOf(client)));
        assertBasicAuthInterceptorEmits(interceptorsOf(client), PROXY_USER, PROXY_PASS);
    }

    @Test
    public void restTemplateWithNoProxyConfigured() {
        KindeOAuth2Properties props = Mockito.mock(KindeOAuth2Properties.class);
        when(props.getProxy()).thenReturn(null);

        RestTemplate template = KindeOAuth2ResourceServerAutoConfig.restTemplate(props);
        assertNull(proxyOf(requestFactoryOf(template)),
                "Expected no proxy on the request factory when properties#getProxy() is null");
        assertFalse(hasBasicAuthInterceptor(template.getInterceptors()),
                "Expected no BasicAuthenticationInterceptor when no proxy is configured");
    }

    @Test
    public void restTemplateWithProxyHostAndPort() {
        KindeOAuth2Properties props = propertiesWithProxy(EXPECTED_PROXY_HOST, EXPECTED_PROXY_PORT, "", "");

        RestTemplate template = KindeOAuth2ResourceServerAutoConfig.restTemplate(props);
        assertProxyAddress(proxyOf(requestFactoryOf(template)));
        assertFalse(hasBasicAuthInterceptor(template.getInterceptors()),
                "Expected no BasicAuthenticationInterceptor when username/password are blank");
    }

    @Test
    public void restTemplateWithAuthenticatedProxy() throws Exception {
        KindeOAuth2Properties props = propertiesWithProxy(EXPECTED_PROXY_HOST, EXPECTED_PROXY_PORT, PROXY_USER, PROXY_PASS);

        RestTemplate template = KindeOAuth2ResourceServerAutoConfig.restTemplate(props);
        assertProxyAddress(proxyOf(requestFactoryOf(template)));
        assertBasicAuthInterceptorEmits(template.getInterceptors(), PROXY_USER, PROXY_PASS);
    }

    // --- helpers ---------------------------------------------------------------------------------

    private static KindeOAuth2Properties propertiesWithProxy(String host, int port, String user, String pass) {
        KindeOAuth2Properties.Proxy proxyProps = new KindeOAuth2Properties.Proxy();
        proxyProps.setHost(host);
        proxyProps.setPort(port);
        proxyProps.setUsername(user);
        proxyProps.setPassword(pass);

        KindeOAuth2Properties props = Mockito.mock(KindeOAuth2Properties.class);
        when(props.getProxy()).thenReturn(proxyProps);
        return props;
    }

    private static Proxy proxyOf(SimpleClientHttpRequestFactory factory) {
        return (Proxy) ReflectionTestUtils.getField(factory, "proxy");
    }

    private static void assertProxyAddress(Proxy proxy) {
        assertNotNull(proxy, "Expected proxy to be set on the request factory");
        assertEquals(Proxy.Type.HTTP, proxy.type(), "Expected an HTTP-typed proxy");
        assertEquals(new InetSocketAddress(EXPECTED_PROXY_HOST, EXPECTED_PROXY_PORT), proxy.address(),
                "Expected proxy address to match the configured host/port");
    }

    private static boolean hasBasicAuthInterceptor(List<ClientHttpRequestInterceptor> interceptors) {
        return interceptors.stream().anyMatch(BasicAuthenticationInterceptor.class::isInstance);
    }

    private static void assertBasicAuthInterceptorEmits(
            List<ClientHttpRequestInterceptor> interceptors, String user, String pass) throws Exception {
        ClientHttpRequestInterceptor auth = interceptors.stream()
                .filter(BasicAuthenticationInterceptor.class::isInstance)
                .findFirst()
                .orElseThrow(() -> new AssertionError(
                        "Expected a BasicAuthenticationInterceptor when proxy credentials are configured"));

        MockClientHttpRequest outgoing = new MockClientHttpRequest(HttpMethod.GET, URI.create("https://example.test"));
        auth.intercept(outgoing, new byte[0],
                (req, body) -> new MockClientHttpResponse(new byte[0], HttpStatus.OK));

        String expected = "Basic " + Base64.getEncoder()
                .encodeToString((user + ":" + pass).getBytes(StandardCharsets.UTF_8));
        assertEquals(expected, outgoing.getHeaders().getFirst(HttpHeaders.AUTHORIZATION),
                "BasicAuthenticationInterceptor must emit Authorization header for the configured credentials");
    }

    /**
     * Returns the {@link SimpleClientHttpRequestFactory} that was passed to
     * {@code RestTemplate#setRequestFactory}, bypassing the {@code InterceptingClientHttpRequestFactory}
     * that {@link RestTemplate#getRequestFactory()} auto-wraps around it whenever interceptors are
     * registered. The {@code requestFactory} field lives on {@code HttpAccessor} and has been stable
     * across Spring Framework's public API for many releases.
     */
    private static SimpleClientHttpRequestFactory requestFactoryOf(RestTemplate template) {
        return (SimpleClientHttpRequestFactory) ReflectionTestUtils.getField(template, "requestFactory");
    }

    /**
     * Walks {@code DefaultRestClient}'s declared fields and returns the first
     * {@link SimpleClientHttpRequestFactory} found. RestClient has no public accessor for its
     * request factory; matching by type keeps this resilient to Spring-internal field renames.
     */
    private static SimpleClientHttpRequestFactory requestFactoryOf(RestClient client) {
        for (Field field : client.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(client);
                if (value instanceof SimpleClientHttpRequestFactory factory) {
                    return factory;
                }
            } catch (IllegalAccessException ignored) {
            }
        }
        throw new AssertionError(
                "No SimpleClientHttpRequestFactory field found on " + client.getClass().getName());
    }

    /**
     * Walks {@code DefaultRestClient}'s declared fields and returns the first non-empty
     * {@code List<ClientHttpRequestInterceptor>}. Same rationale as {@link #requestFactoryOf}.
     */
    @SuppressWarnings("unchecked")
    private static List<ClientHttpRequestInterceptor> interceptorsOf(RestClient client) {
        for (Field field : client.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(client);
                if (value instanceof List<?> list
                        && !list.isEmpty()
                        && list.get(0) instanceof ClientHttpRequestInterceptor) {
                    return (List<ClientHttpRequestInterceptor>) list;
                }
            } catch (IllegalAccessException ignored) {
            }
        }
        return List.of();
    }

}
