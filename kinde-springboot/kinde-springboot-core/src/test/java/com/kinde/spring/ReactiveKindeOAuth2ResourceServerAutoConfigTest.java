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
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.mockito.Mockito.when;

@TestPropertySource(properties = {
        KindeOAuth2PropertiesMappingEnvironmentPostProcessor.KINDE_OAUTH_DOMAIN + "=https://test.kinde.com" ,
        KindeOAuth2PropertiesMappingEnvironmentPostProcessor.KINDE_OAUTH_CLIENT_ID + "=test_client_id" ,
        KindeOAuth2PropertiesMappingEnvironmentPostProcessor.KINDE_OAUTH_CLIENT_SECRET + "=test_client_secret" ,
        KindeOAuth2PropertiesMappingEnvironmentPostProcessor.KINDE_OAUTH_SCOPES + "=profile" ,
        "spring.security.oauth2.client.registration.kinde.client-id=test_client_id"
})
@SpringBootTest(classes = {ReactiveKindeOAuth2ResourceServerAutoConfigTest.class})
@Import(ReactiveKindeOAuth2ResourceServerAutoConfigTest.MyTestConfig.class)
public class ReactiveKindeOAuth2ResourceServerAutoConfigTest {
    @TestConfiguration
    public static class MyTestConfig {
        @Bean
        public ReactiveKindeOAuth2ResourceServerAutoConfig reactiveKindeOAuth2AutoConfig() {
            System.out.println("Hello 3");
            ReactiveKindeOAuth2ResourceServerAutoConfig reactiveKindeOAuth2ResourceServerAutoConfig = new ReactiveKindeOAuth2ResourceServerAutoConfig();
            return reactiveKindeOAuth2ResourceServerAutoConfig;
        }

        @Bean
        public KindeSdkClient kindeSdkClient() {
            return Mockito.mock(KindeSdkClient.class);
        }

        @Bean
        public KindeOAuth2Properties kindeOAuth2Properties() {
            return Mockito.mock(KindeOAuth2Properties.class);
        }
    }

    @Autowired
    private ReactiveKindeOAuth2ResourceServerAutoConfig reactiveKindeOAuth2ResourceServerAutoConfig;


    @Test
    public void testOauth2UserService() {
        OAuth2ResourceServerProperties oAuth2ResourceServerProperties = Mockito.mock(OAuth2ResourceServerProperties.class);
        KindeOAuth2Properties kindeOAuth2Properties = Mockito.mock(KindeOAuth2Properties.class);
        OAuth2ResourceServerProperties.Jwt jwt = Mockito.mock(OAuth2ResourceServerProperties.Jwt.class);
        when(oAuth2ResourceServerProperties.getJwt()).thenReturn(jwt);
        when(jwt.getJwkSetUri()).thenReturn("https://test.kinde.com");
        when(jwt.getIssuerUri()).thenReturn("https://test.kinde.com");
        when(kindeOAuth2Properties.getAudience()).thenReturn("https://test.kinde.com");
        reactiveKindeOAuth2ResourceServerAutoConfig.jwtDecoder(oAuth2ResourceServerProperties,kindeOAuth2Properties);
    }



}
