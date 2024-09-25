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
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {
        KindeOAuth2PropertiesMappingEnvironmentPostProcessor.KINDE_OAUTH_DOMAIN + "=https://test.kinde.com" ,
        KindeOAuth2PropertiesMappingEnvironmentPostProcessor.KINDE_OAUTH_CLIENT_ID + "=test_client_id" ,
        KindeOAuth2PropertiesMappingEnvironmentPostProcessor.KINDE_OAUTH_CLIENT_SECRET + "=test_client_secret" ,
        KindeOAuth2PropertiesMappingEnvironmentPostProcessor.KINDE_OAUTH_SCOPES + "=profile" ,
        "spring.security.oauth2.client.registration.kinde.client-id=test_client_id"
})
@SpringBootTest(classes = {ReactiveKindeOAuth2ResourceServerHttpServerAutoConfigTest.class})
@Import(ReactiveKindeOAuth2ResourceServerHttpServerAutoConfigTest.MyTestConfig.class)
public class ReactiveKindeOAuth2ResourceServerHttpServerAutoConfigTest {
    @TestConfiguration
    public static class MyTestConfig {
        @Bean
        public ReactiveKindeOAuth2ResourceServerHttpServerAutoConfig reactiveKindeOAuth2ResourceServerHttpServerAutoConfig() {
            System.out.println("Hello 3");
            ReactiveKindeOAuth2ResourceServerHttpServerAutoConfig reactiveKindeOAuth2ResourceServerAutoConfig =
                    new ReactiveKindeOAuth2ResourceServerHttpServerAutoConfig();
            return reactiveKindeOAuth2ResourceServerAutoConfig;
        }
    }

    @Autowired
    private ReactiveKindeOAuth2ResourceServerHttpServerAutoConfig reactiveKindeOAuth2ResourceServerHttpServerAutoConfig;

    @Test
    public void testKindeOAuth2ResourceServerBeanPostProcessor() {
        KindeOAuth2Properties kindeOAuth2Properties = Mockito.mock(KindeOAuth2Properties.class);
        reactiveKindeOAuth2ResourceServerHttpServerAutoConfig.kindeOAuth2ResourceServerBeanPostProcessor(kindeOAuth2Properties);
    }
}
