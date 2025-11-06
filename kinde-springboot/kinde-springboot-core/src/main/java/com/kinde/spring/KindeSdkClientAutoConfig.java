package com.kinde.spring;

import com.kinde.spring.config.KindeOAuth2Properties;
import com.kinde.spring.sdk.KindeSdkClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Auto-configuration for KindeSdkClient.
 * This ensures that KindeSdkClient is properly configured as a Spring bean
 * when Kinde OAuth2 properties are available.
 */
@AutoConfiguration
@ConditionalOnKindeClientProperties
@EnableConfigurationProperties(KindeOAuth2Properties.class)
public class KindeSdkClientAutoConfig {

    @Bean
    @ConditionalOnMissingBean
    public KindeSdkClient kindeSdkClient() {
        return new KindeSdkClient();
    }
}
