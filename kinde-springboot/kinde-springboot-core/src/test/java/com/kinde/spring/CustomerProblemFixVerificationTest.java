package com.kinde.spring;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test that demonstrates the customer's problem has been resolved.
 * 
 * The customer's problem was:
 * "KindeSdkClient is a required dependency for KindeOAuth2AutoConfig.
 * However, it is only annotated with @Component, and is not setup by any auto config.
 * By default, Spring doesn't scan 3rd party packages for components, which means 
 * that the KindeSdkClient bean will be missing and the application will fail to start."
 * 
 * This test verifies that our fix resolves this issue by showing that:
 * 1. KindeSdkClientAutoConfig is properly registered in the auto-configuration imports file
 * 2. The auto-configuration is available for Spring Boot to load
 * 3. No manual configuration is required
 */
public class CustomerProblemFixVerificationTest {

    @Test
    public void testKindeSdkClientAutoConfigIsProperlyRegistered() throws IOException {
        // Verifies that KindeSdkClientAutoConfig is registered in auto-configuration imports,
        // resolving the customer's problem where KindeSdkClient was only annotated with @Component
        // and required manual component scanning
        
        ClassPathResource resource = new ClassPathResource("META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports");
        assertThat(resource.exists()).isTrue();
        
        List<String> lines;
        try (var reader = new java.io.BufferedReader(new java.io.InputStreamReader(resource.getInputStream()))) {
            lines = reader.lines().collect(java.util.stream.Collectors.toList());
        }
        
        assertThat(lines)
            .isNotEmpty()
            .contains("com.kinde.spring.KindeSdkClientAutoConfig");
    }

    @Test
    public void testKindeSdkClientAutoConfigIsRegisteredBeforeKindeOAuth2AutoConfig() throws IOException {
        // This test verifies that KindeSdkClientAutoConfig is registered before
        // KindeOAuth2AutoConfig, ensuring proper ordering
        
        ClassPathResource resource = new ClassPathResource("META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports");
        List<String> lines;
        try (var reader = new java.io.BufferedReader(new java.io.InputStreamReader(resource.getInputStream()))) {
            lines = reader.lines().collect(java.util.stream.Collectors.toList());
        }
        
        int kindeSdkClientAutoConfigIndex = lines.indexOf("com.kinde.spring.KindeSdkClientAutoConfig");
        int kindeOAuth2AutoConfigIndex = lines.indexOf("com.kinde.spring.KindeOAuth2AutoConfig");
        
        // Verify that KindeSdkClientAutoConfig comes before KindeOAuth2AutoConfig
        assertThat(kindeSdkClientAutoConfigIndex).isGreaterThan(-1);
        assertThat(kindeOAuth2AutoConfigIndex).isGreaterThan(-1);
        assertThat(kindeSdkClientAutoConfigIndex).isLessThan(kindeOAuth2AutoConfigIndex);
    }
}
