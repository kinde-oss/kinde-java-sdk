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
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
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
            System.out.println("Hello 3");
            KindeOAuth2ResourceServerAutoConfig kindeOAuth2ResourceServerAutoConfig = new KindeOAuth2ResourceServerAutoConfig();
            return kindeOAuth2ResourceServerAutoConfig;
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

    @Test
    public void restClientWithNoProxyConfigured() {
        KindeOAuth2Properties props = Mockito.mock(KindeOAuth2Properties.class);
        when(props.getProxy()).thenReturn(null);

        RestClient client = KindeOAuth2ResourceServerAutoConfig.restClient(props);
        assertNotNull(client, "restClient() should build a non-null RestClient when no proxy is configured");
    }

    @Test
    public void restClientWithProxyHostAndPort() {
        KindeOAuth2Properties.Proxy proxy = new KindeOAuth2Properties.Proxy();
        proxy.setHost("proxy.example.com");
        proxy.setPort(8080);
        proxy.setUsername("");
        proxy.setPassword("");

        KindeOAuth2Properties props = Mockito.mock(KindeOAuth2Properties.class);
        when(props.getProxy()).thenReturn(proxy);

        RestClient client = KindeOAuth2ResourceServerAutoConfig.restClient(props);
        assertNotNull(client, "restClient() should build a non-null RestClient when proxy host/port are configured");
    }

    @Test
    public void restClientWithAuthenticatedProxy() {
        KindeOAuth2Properties.Proxy proxy = new KindeOAuth2Properties.Proxy();
        proxy.setHost("proxy.example.com");
        proxy.setPort(8080);
        proxy.setUsername("proxy-user");
        proxy.setPassword("proxy-pass");

        KindeOAuth2Properties props = Mockito.mock(KindeOAuth2Properties.class);
        when(props.getProxy()).thenReturn(proxy);

        RestClient client = KindeOAuth2ResourceServerAutoConfig.restClient(props);
        assertNotNull(client, "restClient() should build a non-null RestClient when proxy credentials are configured");
    }

    @Test
    public void restTemplateWithProxyHostAndPort() {
        KindeOAuth2Properties.Proxy proxy = new KindeOAuth2Properties.Proxy();
        proxy.setHost("proxy.example.com");
        proxy.setPort(8080);
        proxy.setUsername("");
        proxy.setPassword("");

        KindeOAuth2Properties props = Mockito.mock(KindeOAuth2Properties.class);
        when(props.getProxy()).thenReturn(proxy);

        RestTemplate template = KindeOAuth2ResourceServerAutoConfig.restTemplate(props);
        assertNotNull(template, "restTemplate() should build a non-null RestTemplate when proxy is configured");
    }

    @Test
    public void restTemplateWithAuthenticatedProxy() {
        KindeOAuth2Properties.Proxy proxy = new KindeOAuth2Properties.Proxy();
        proxy.setHost("proxy.example.com");
        proxy.setPort(8080);
        proxy.setUsername("proxy-user");
        proxy.setPassword("proxy-pass");

        KindeOAuth2Properties props = Mockito.mock(KindeOAuth2Properties.class);
        when(props.getProxy()).thenReturn(proxy);

        RestTemplate template = KindeOAuth2ResourceServerAutoConfig.restTemplate(props);
        assertNotNull(template, "restTemplate() should build a non-null RestTemplate when proxy credentials are configured");
    }

}
