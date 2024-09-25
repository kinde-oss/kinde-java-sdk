package com.kinde.spring;


import com.kinde.spring.config.KindeOAuth2Properties;
import com.kinde.spring.env.KindeOAuth2PropertiesMappingEnvironmentPostProcessor;
import com.kinde.spring.sdk.KindeSdkClient;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

@TestPropertySource(properties = {
        KindeOAuth2PropertiesMappingEnvironmentPostProcessor.KINDE_OAUTH_DOMAIN + "=https://test.kinde.com" ,
        KindeOAuth2PropertiesMappingEnvironmentPostProcessor.KINDE_OAUTH_CLIENT_ID + "=test_client_id" ,
        KindeOAuth2PropertiesMappingEnvironmentPostProcessor.KINDE_OAUTH_CLIENT_SECRET + "=test_client_secret" ,
        KindeOAuth2PropertiesMappingEnvironmentPostProcessor.KINDE_OAUTH_SCOPES + "=profile" ,
        "spring.security.oauth2.client.registration.kinde.client-id=test_client_id"
})
@SpringBootTest(classes = {ReactiveKindeOAuth2AutoConfigTest.class})
@Import(ReactiveKindeOAuth2AutoConfigTest.MyTestConfig.class)
public class ReactiveKindeOAuth2AutoConfigTest {
    @TestConfiguration
    public static class MyTestConfig {
        @Bean
        public ReactiveKindeOAuth2AutoConfig reactiveKindeOAuth2AutoConfig() {
            System.out.println("Hello 3");
            ReactiveKindeOAuth2AutoConfig reactiveKindeOAuth2AutoConfig = new ReactiveKindeOAuth2AutoConfig();
            return reactiveKindeOAuth2AutoConfig;
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
    private ReactiveKindeOAuth2AutoConfig reactiveKindeOAuth2AutoConfig;


    @Test
    public void testOauth2UserService() {
        reactiveKindeOAuth2AutoConfig.oauth2UserService(List.of());
    }

    @Test
    public void testOidcUserService() {
        ReactiveOAuth2UserService reactiveOAuth2UserService = Mockito.mock(ReactiveOAuth2UserService.class);
        reactiveKindeOAuth2AutoConfig.oidcUserService(List.of(),reactiveOAuth2UserService);
    }

}
