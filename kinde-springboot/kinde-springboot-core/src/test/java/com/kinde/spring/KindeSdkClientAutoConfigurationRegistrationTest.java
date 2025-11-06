package com.kinde.spring;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

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

    /**
     * Helper method to read the auto-configuration imports file.
     * 
     * @return List of lines from the auto-configuration imports file
     * @throws IOException if the file cannot be read
     */
    private List<String> readAutoConfigurationImports() throws IOException {
        ClassPathResource resource = new ClassPathResource("META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports");
        assertThat(resource.exists()).isTrue();
        
        try (var reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            return reader.lines().collect(Collectors.toList());
        }
    }

    @Test
    public void testKindeSdkClientAutoConfigIsProperlyRegistered() throws IOException {
        // Verifies that KindeSdkClientAutoConfig is registered in auto-configuration imports
        // and available for Spring Boot's auto-configuration system
        
        List<String> lines = readAutoConfigurationImports();
        
        assertThat(lines)
            .isNotEmpty()
            .contains("com.kinde.spring.KindeSdkClientAutoConfig");
    }

    @Test
    public void testKindeSdkClientAutoConfigIsRegisteredBeforeKindeOAuth2AutoConfig() throws IOException {
        // This test verifies that KindeSdkClientAutoConfig is registered before
        // KindeOAuth2AutoConfig, ensuring proper ordering
        
        List<String> lines = readAutoConfigurationImports();
        
        int kindeSdkClientAutoConfigIndex = lines.indexOf("com.kinde.spring.KindeSdkClientAutoConfig");
        int kindeOAuth2AutoConfigIndex = lines.indexOf("com.kinde.spring.KindeOAuth2AutoConfig");
        
        // Verify that KindeSdkClientAutoConfig comes before KindeOAuth2AutoConfig
        assertThat(kindeSdkClientAutoConfigIndex).isGreaterThan(-1);
        assertThat(kindeOAuth2AutoConfigIndex).isGreaterThan(-1);
        assertThat(kindeSdkClientAutoConfigIndex).isLessThan(kindeOAuth2AutoConfigIndex);
    }
}
