package org.openapitools.sdk;

import org.openapitools.sdk.storage.Storage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public Storage storage() {
        return new Storage();
    }

    @Bean
    public String emptyString() {
        return ""; // Return an empty string
    }
}