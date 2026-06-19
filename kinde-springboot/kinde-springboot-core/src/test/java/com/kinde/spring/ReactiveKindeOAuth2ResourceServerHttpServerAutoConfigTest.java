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
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.test.context.TestPropertySource;

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
@SpringBootTest(classes = {ReactiveKindeOAuth2ResourceServerHttpServerAutoConfigTest.class})
@Import(ReactiveKindeOAuth2ResourceServerHttpServerAutoConfigTest.MyTestConfig.class)
public class ReactiveKindeOAuth2ResourceServerHttpServerAutoConfigTest {

    @TestConfiguration
    public static class MyTestConfig {
        @Bean
        public ReactiveKindeOAuth2ResourceServerHttpServerAutoConfig reactiveAutoConfig() {
            return new ReactiveKindeOAuth2ResourceServerHttpServerAutoConfig();
        }
    }

    @Autowired
    private ReactiveKindeOAuth2ResourceServerHttpServerAutoConfig autoConfig;

    @Test
    public void kindeOAuth2ResourceServerBeanPostProcessorReturnsNonNullProcessor() {
        KindeOAuth2Properties props = mock(KindeOAuth2Properties.class);

        BeanPostProcessor processor = autoConfig.kindeOAuth2ResourceServerBeanPostProcessor(props);

        assertNotNull(processor);
    }

    /**
     * The post-processor wires JWT validation onto every {@link ServerHttpSecurity} bean Spring
     * surfaces. We invoke {@code postProcessAfterInitialization} directly on a mocked
     * {@link ServerHttpSecurity}, driving the {@code oauth2ResourceServer} and nested {@code jwt}
     * customizer lambdas through Mockito {@code Answer}s so the {@code jwtAuthenticationConverter}
     * call inside the lambda body actually executes under JaCoCo.
     */
    @Test
    public void postProcessAfterInitializationWiresJwtAuthenticationConverter() {
        KindeOAuth2Properties props = mock(KindeOAuth2Properties.class);
        when(props.getPermissionsClaim()).thenReturn("permissions");

        ReactiveKindeOAuth2ResourceServerHttpServerAutoConfig.KindeOAuth2ResourceServerBeanPostProcessor processor =
                new ReactiveKindeOAuth2ResourceServerHttpServerAutoConfig.KindeOAuth2ResourceServerBeanPostProcessor(props);

        ServerHttpSecurity http = mock(ServerHttpSecurity.class);
        ServerHttpSecurity.OAuth2ResourceServerSpec resourceServerSpec =
                mock(ServerHttpSecurity.OAuth2ResourceServerSpec.class);
        ServerHttpSecurity.OAuth2ResourceServerSpec.JwtSpec jwtSpec =
                mock(ServerHttpSecurity.OAuth2ResourceServerSpec.JwtSpec.class);
        when(jwtSpec.jwtAuthenticationConverter(any())).thenReturn(jwtSpec);
        when(resourceServerSpec.jwt(any())).thenAnswer(invocation -> {
            Customizer<ServerHttpSecurity.OAuth2ResourceServerSpec.JwtSpec> inner = invocation.getArgument(0);
            inner.customize(jwtSpec);
            return resourceServerSpec;
        });
        when(http.oauth2ResourceServer(any())).thenAnswer(invocation -> {
            Customizer<ServerHttpSecurity.OAuth2ResourceServerSpec> outer = invocation.getArgument(0);
            outer.customize(resourceServerSpec);
            return http;
        });

        Object result = processor.postProcessAfterInitialization(http, "anyBeanName");

        assertSame(http, result, "postProcessAfterInitialization should return the input bean");
        verify(http).oauth2ResourceServer(any());
        verify(resourceServerSpec).jwt(any());
        verify(jwtSpec).jwtAuthenticationConverter(any());
        verify(props).getPermissionsClaim();
    }

    @Test
    public void postProcessAfterInitializationLeavesNonHttpBeansUntouched() {
        KindeOAuth2Properties props = mock(KindeOAuth2Properties.class);

        ReactiveKindeOAuth2ResourceServerHttpServerAutoConfig.KindeOAuth2ResourceServerBeanPostProcessor processor =
                new ReactiveKindeOAuth2ResourceServerHttpServerAutoConfig.KindeOAuth2ResourceServerBeanPostProcessor(props);

        Object input = "not-a-server-http-security";

        Object result = processor.postProcessAfterInitialization(input, "someBean");

        assertSame(input, result);
        verify(props, never()).getPermissionsClaim();
    }
}
