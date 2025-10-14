package com.kinde.spring;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for KindeSdkClientAutoConfig registration and configuration.
 * 
 * This test class verifies that:
 * 1. KindeSdkClientAutoConfig is properly registered in the auto-configuration imports file
 * 2. The auto-configuration is available for Spring Boot to load
 * 3. Proper ordering is maintained for dependency resolution
 * 4. No manual configuration is required
 */
public class KindeSdkClientAutoConfigurationRegistrationTest {

    @Test
    public void testKindeSdkClientAutoConfigIsProperlyRegistered() throws IOException {
        // Verifies that KindeSdkClientAutoConfig is registered in auto-configuration imports
        // and available for Spring Boot's auto-configuration system
        
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
