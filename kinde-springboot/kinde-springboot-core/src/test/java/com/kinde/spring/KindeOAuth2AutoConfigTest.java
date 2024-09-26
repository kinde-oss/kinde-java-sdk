package com.kinde.spring;

import com.kinde.KindeClient;
import com.kinde.config.KindeConfig;
import com.kinde.spring.config.KindeOAuth2Properties;
import com.kinde.spring.env.KindeOAuth2PropertiesMappingEnvironmentPostProcessor;
import com.kinde.spring.sdk.KindeSdkClient;
import org.checkerframework.checker.units.qual.K;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@TestPropertySource(properties = {
        KindeOAuth2PropertiesMappingEnvironmentPostProcessor.KINDE_OAUTH_DOMAIN + "=https://test.kinde.com" ,
        KindeOAuth2PropertiesMappingEnvironmentPostProcessor.KINDE_OAUTH_CLIENT_ID + "=test_client_id" ,
        KindeOAuth2PropertiesMappingEnvironmentPostProcessor.KINDE_OAUTH_CLIENT_SECRET + "=test_client_secret" ,
        KindeOAuth2PropertiesMappingEnvironmentPostProcessor.KINDE_OAUTH_SCOPES + "=profile" ,
        "spring.security.oauth2.client.registration.kinde.client-id=test_client_id"
})
@SpringBootTest(classes = {KindeOAuth2AutoConfigTest.class})
@Import(KindeOAuth2AutoConfigTest.MyTestConfig.class)
public class KindeOAuth2AutoConfigTest {

    @TestConfiguration
    public static class MyTestConfig {


        @Bean
        public ClientRegistrationRepository clientRegistrationRepository() {
            System.out.println("Hello 1");
            return Mockito.mock(ClientRegistrationRepository.class);
        }

        @Bean
        public KindeConfig kindeConfig() {
            KindeConfig kindeConfig = Mockito.mock(KindeConfig.class);
            when(kindeConfig.logoutRedirectUri()).thenReturn("http://localhost:8080/");
            return kindeConfig;
        }

        @Bean
        public KindeClient kindeClient(KindeConfig kindeConfig) {
            KindeClient kindeClient = Mockito.mock(KindeClient.class);
            when(kindeClient.kindeConfig()).thenReturn(kindeConfig);
            return kindeClient;
        }

        @Bean
        public KindeSdkClient kindeSdkClient(KindeClient kindeClient) {
            System.out.println("Hello 2");
            KindeSdkClient kindeSdkClient = Mockito.mock(KindeSdkClient.class);
            when(kindeSdkClient.getClient()).thenReturn(kindeClient);
            return kindeSdkClient;
        }

        @Bean
        public KindeOAuth2Properties kindeOAuth2Properties() {
            return Mockito.mock(KindeOAuth2Properties.class);
        }

        @Bean
        public KindeOAuth2AutoConfig kindeOAuth2AutoConfig(KindeSdkClient kindeSdkClient) {
            System.out.println("Hello 3");
            KindeOAuth2AutoConfig kindeOAuth2AutoConfig = new KindeOAuth2AutoConfig();
            kindeOAuth2AutoConfig.setKindeSdkClient(kindeSdkClient);
            return kindeOAuth2AutoConfig;
        }
    }

    @Autowired
    private KindeOAuth2AutoConfig kindeOAuth2AutoConfig;

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    @Autowired
    private ApplicationContext context;

    @BeforeEach
    public void setUp() {
    }

    @Test
    public void testOidcLogoutSuccessHandler() throws Exception {
        OidcClientInitiatedLogoutSuccessHandler oidcClientInitiatedLogoutSuccessHandler = kindeOAuth2AutoConfig.oidcLogoutSuccessHandler(clientRegistrationRepository);
    }

    @Test
    public void testOAuth2UserService() throws Exception {
        OAuth2UserService oAuth2UserService = kindeOAuth2AutoConfig.oAuth2UserService(List.of());
    }

    @Test
    public void testOidcUserService() throws Exception {
        OAuth2UserService oAuth2UserService = kindeOAuth2AutoConfig.oidcUserService(kindeOAuth2AutoConfig.oAuth2UserService(List.of()),List.of());
    }


}
