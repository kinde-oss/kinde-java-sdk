package com.kinde.spring;

import com.kinde.spring.config.KindeOAuth2Properties;
import com.kinde.spring.env.KindeOAuth2PropertiesMappingEnvironmentPostProcessor;
import com.kinde.spring.sdk.KindeSdkClient;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

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

}
