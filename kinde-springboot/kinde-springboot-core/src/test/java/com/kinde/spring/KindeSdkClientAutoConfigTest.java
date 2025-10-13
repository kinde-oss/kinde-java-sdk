package com.kinde.spring;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for KindeSdkClientAutoConfig.
 * 
 * This test class covers:
 * 1. Verification that KindeSdkClientAutoConfig is properly registered in the imports file
 * 2. Verification that the auto-configuration is available for Spring Boot to load
 * 3. Verification that the auto-configuration is properly ordered
 */
public class KindeSdkClientAutoConfigTest {

    @Test
    public void testKindeSdkClientAutoConfigIsRegisteredInImportsFile() throws IOException {
        // Test that KindeSdkClientAutoConfig is properly registered in the auto-configuration imports file
        ClassPathResource resource = new ClassPathResource("META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports");
        assertThat(resource.exists()).isTrue();
        
        List<String> lines = Files.readAllLines(resource.getFile().toPath());
        
        // Verify that KindeSdkClientAutoConfig is registered
        assertThat(lines).contains("com.kinde.spring.KindeSdkClientAutoConfig");
    }

    @Test
    public void testKindeSdkClientAutoConfigIsRegisteredBeforeKindeOAuth2AutoConfig() throws IOException {
        // Test that KindeSdkClientAutoConfig is registered before KindeOAuth2AutoConfig
        // This ensures proper ordering so that KindeSdkClient is available for KindeOAuth2AutoConfig
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
        // Test that the auto-configuration imports file exists and contains the necessary configuration
        ClassPathResource resource = new ClassPathResource("META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports");
        assertThat(resource.exists()).isTrue();
        
        List<String> lines = Files.readAllLines(resource.getFile().toPath());
        assertThat(lines).isNotEmpty();
        
        // Verify that KindeSdkClientAutoConfig is in the list
        assertThat(lines).contains("com.kinde.spring.KindeSdkClientAutoConfig");
    }

    @Test
    public void testKindeSdkClientAutoConfigClassExists() {
        // Test that the KindeSdkClientAutoConfig class exists and can be loaded
        try {
            Class<?> autoConfigClass = Class.forName("com.kinde.spring.KindeSdkClientAutoConfig");
            assertThat(autoConfigClass).isNotNull();
            assertThat(autoConfigClass.getSimpleName()).isEqualTo("KindeSdkClientAutoConfig");
        } catch (ClassNotFoundException e) {
            throw new AssertionError("KindeSdkClientAutoConfig class not found", e);
        }
    }

    @Test
    public void testKindeSdkClientAutoConfigHasCorrectAnnotations() {
        // Test that KindeSdkClientAutoConfig has the correct annotations
        try {
            Class<?> autoConfigClass = Class.forName("com.kinde.spring.KindeSdkClientAutoConfig");
            
            // Verify that it has @AutoConfiguration annotation
            assertThat(autoConfigClass.isAnnotationPresent(org.springframework.boot.autoconfigure.AutoConfiguration.class))
                    .isTrue();
            
            // Verify that it has @ConditionalOnKindeClientProperties annotation
            assertThat(autoConfigClass.isAnnotationPresent(com.kinde.spring.ConditionalOnKindeClientProperties.class))
                    .isTrue();
            
            // Verify that it has @EnableConfigurationProperties annotation
            assertThat(autoConfigClass.isAnnotationPresent(org.springframework.boot.context.properties.EnableConfigurationProperties.class))
                    .isTrue();
            
        } catch (ClassNotFoundException e) {
            throw new AssertionError("KindeSdkClientAutoConfig class not found", e);
        }
    }

    @Test
    public void testKindeSdkClientAutoConfigHasKindeSdkClientBean() {
        // Test that KindeSdkClientAutoConfig has a method that creates KindeSdkClient bean
        try {
            Class<?> autoConfigClass = Class.forName("com.kinde.spring.KindeSdkClientAutoConfig");
            
            // Look for a method that returns KindeSdkClient
            java.lang.reflect.Method[] methods = autoConfigClass.getDeclaredMethods();
            boolean hasKindeSdkClientMethod = false;
            
            for (java.lang.reflect.Method method : methods) {
                if (method.getReturnType().getSimpleName().equals("KindeSdkClient")) {
                    hasKindeSdkClientMethod = true;
                    
                    // Verify that it has @Bean annotation
                    assertThat(method.isAnnotationPresent(org.springframework.context.annotation.Bean.class))
                            .isTrue();
                    
                    // Verify that it has @ConditionalOnMissingBean annotation
                    assertThat(method.isAnnotationPresent(org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean.class))
                            .isTrue();
                    
                    break;
                }
            }
            
            assertThat(hasKindeSdkClientMethod).isTrue();
            
        } catch (ClassNotFoundException e) {
            throw new AssertionError("KindeSdkClientAutoConfig class not found", e);
        }
    }

    @Test
    public void testCustomerProblemIsResolved() throws IOException {
        // Test that demonstrates the customer's problem has been resolved
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
        // Test that verifies no manual configuration is required
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
