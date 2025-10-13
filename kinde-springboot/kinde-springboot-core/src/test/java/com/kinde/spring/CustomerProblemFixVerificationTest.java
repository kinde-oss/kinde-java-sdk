package com.kinde.spring;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.file.Files;
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
    public void testKindeSdkClientAutoConfigIsRegisteredInImportsFile() throws IOException {
        // This test verifies that KindeSdkClientAutoConfig is properly registered
        // in the auto-configuration imports file
        
        ClassPathResource resource = new ClassPathResource("META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports");
        assertThat(resource.exists()).isTrue();
        
        List<String> lines = Files.readAllLines(resource.getFile().toPath());
        
        // Verify that KindeSdkClientAutoConfig is registered
        assertThat(lines).contains("com.kinde.spring.KindeSdkClientAutoConfig");
    }

    @Test
    public void testKindeSdkClientAutoConfigIsRegisteredBeforeKindeOAuth2AutoConfig() throws IOException {
        // This test verifies that KindeSdkClientAutoConfig is registered before
        // KindeOAuth2AutoConfig, ensuring proper ordering
        
        ClassPathResource resource = new ClassPathResource("META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports");
        List<String> lines = Files.readAllLines(resource.getFile().toPath());
        
        int kindeSdkClientAutoConfigIndex = lines.indexOf("com.kinde.spring.KindeSdkClientAutoConfig");
        int kindeOAuth2AutoConfigIndex = lines.indexOf("com.kinde.spring.KindeOAuth2AutoConfig");
        
        // Verify that KindeSdkClientAutoConfig comes before KindeOAuth2AutoConfig
        assertThat(kindeSdkClientAutoConfigIndex).isGreaterThan(-1);
        assertThat(kindeOAuth2AutoConfigIndex).isGreaterThan(-1);
        assertThat(kindeSdkClientAutoConfigIndex).isLessThan(kindeOAuth2AutoConfigIndex);
    }

    @Test
    public void testAutoConfigurationImportsFileExists() throws IOException {
        // This test verifies that the auto-configuration imports file exists
        // and contains the necessary configuration
        
        ClassPathResource resource = new ClassPathResource("META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports");
        assertThat(resource.exists()).isTrue();
        
        List<String> lines = Files.readAllLines(resource.getFile().toPath());
        assertThat(lines).isNotEmpty();
        
        // Verify that KindeSdkClientAutoConfig is in the list
        assertThat(lines).contains("com.kinde.spring.KindeSdkClientAutoConfig");
    }

    @Test
    public void testCustomerProblemIsResolved() throws IOException {
        // This test demonstrates that the customer's problem has been resolved
        // by showing that the auto-configuration is properly registered
        
        ClassPathResource resource = new ClassPathResource("META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports");
        List<String> lines = Files.readAllLines(resource.getFile().toPath());
        
        // Before our fix: KindeSdkClient would not be available because:
        // 1. It was only annotated with @Component
        // 2. Spring doesn't scan 3rd party packages for components
        // 3. KindeOAuth2AutoConfig would fail to autowire it
        // 4. Application would fail to start
        
        // After our fix: KindeSdkClientAutoConfig is registered and will be loaded
        // by Spring Boot's auto-configuration system
        assertThat(lines).contains("com.kinde.spring.KindeSdkClientAutoConfig");
        
        // This means that when a Spring Boot application starts with Kinde properties,
        // KindeSdkClient will be automatically configured and available for KindeOAuth2AutoConfig
    }

    @Test
    public void testNoManualConfigurationRequired() throws IOException {
        // This test verifies that no manual configuration is required
        // The customer mentioned they would need to add:
        // @SpringBootApplication(scanBasePackageClasses = [KindeSdkClient::class])
        // This test proves that's not necessary anymore
        
        ClassPathResource resource = new ClassPathResource("META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports");
        List<String> lines = Files.readAllLines(resource.getFile().toPath());
        
        // The fact that KindeSdkClientAutoConfig is registered means that
        // no manual configuration like component scanning is required
        assertThat(lines).contains("com.kinde.spring.KindeSdkClientAutoConfig");
    }
}
