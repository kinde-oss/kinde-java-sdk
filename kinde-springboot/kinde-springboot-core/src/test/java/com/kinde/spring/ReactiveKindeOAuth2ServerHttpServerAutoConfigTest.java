package com.kinde.spring;

import com.kinde.spring.config.KindeOAuth2Properties;
import com.kinde.spring.env.KindeOAuth2PropertiesMappingEnvironmentPostProcessor;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcReactiveOAuth2UserService;
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService;
import org.springframework.test.context.TestPropertySource;

import static org.mockito.Mockito.when;

@TestPropertySource(properties = {
        KindeOAuth2PropertiesMappingEnvironmentPostProcessor.KINDE_OAUTH_DOMAIN + "=https://test.kinde.com" ,
        KindeOAuth2PropertiesMappingEnvironmentPostProcessor.KINDE_OAUTH_CLIENT_ID + "=test_client_id" ,
        KindeOAuth2PropertiesMappingEnvironmentPostProcessor.KINDE_OAUTH_CLIENT_SECRET + "=test_client_secret" ,
        KindeOAuth2PropertiesMappingEnvironmentPostProcessor.KINDE_OAUTH_SCOPES + "=profile" ,
        "spring.security.oauth2.client.registration.kinde.client-id=test_client_id"
})
@SpringBootTest(classes = {ReactiveKindeOAuth2ServerHttpServerAutoConfigTest.class})
@Import(ReactiveKindeOAuth2ServerHttpServerAutoConfigTest.MyTestConfig.class)
public class ReactiveKindeOAuth2ServerHttpServerAutoConfigTest {

    @TestConfiguration
    public static class MyTestConfig {
        @Bean
        public ReactiveKindeOAuth2ServerHttpServerAutoConfig reactiveKindeOAuth2ServerHttpServerAutoConfig() {
            System.out.println("Hello 3");
            ReactiveKindeOAuth2ServerHttpServerAutoConfig reactiveKindeOAuth2ResourceServerHttpServerAutoConfig =
                    new ReactiveKindeOAuth2ServerHttpServerAutoConfig();
            return reactiveKindeOAuth2ResourceServerHttpServerAutoConfig;
        }
    }

    @Autowired
    private ReactiveKindeOAuth2ServerHttpServerAutoConfig reactiveKindeOAuth2ResourceServerHttpServerAutoConfig;

    @Test
    public void testAuthManagerServerHttpSecurityBeanPostProcessor() {
        ReactiveOAuth2UserService reactiveOAuth2UserService = Mockito.mock(ReactiveOAuth2UserService.class);
        OidcReactiveOAuth2UserService oidcReactiveOAuth2UserService = Mockito.mock(OidcReactiveOAuth2UserService.class);
        OidcClientInitiatedServerLogoutSuccessHandler oidcClientInitiatedServerLogoutSuccessHandler =
                Mockito.mock(OidcClientInitiatedServerLogoutSuccessHandler.class);
        reactiveKindeOAuth2ResourceServerHttpServerAutoConfig.authManagerServerHttpSecurityBeanPostProcessor(
                reactiveOAuth2UserService,oidcReactiveOAuth2UserService,oidcClientInitiatedServerLogoutSuccessHandler);
    }

    @Test
    public void testOidcClientInitiatedServerLogoutSuccessHandler() throws Exception {
        KindeOAuth2Properties kindeOAuth2Properties = Mockito.mock(KindeOAuth2Properties.class);
        ReactiveClientRegistrationRepository repository = Mockito.mock(ReactiveClientRegistrationRepository.class);
        when(kindeOAuth2Properties.getPostLogoutRedirectUri()).thenReturn("https://kinde.com");
        reactiveKindeOAuth2ResourceServerHttpServerAutoConfig.oidcClientInitiatedServerLogoutSuccessHandler(
                kindeOAuth2Properties,repository);
    }


}
