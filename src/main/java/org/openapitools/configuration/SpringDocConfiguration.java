package org.openapitools.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SpringDocConfiguration {

    @Bean(name = "org.openapitools.configuration.SpringDocConfiguration.apiInfo")
    OpenAPI apiInfo() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("Kinde Management API")
                                .description("Provides endpoints to manage your Kinde Businesses")
                                .termsOfService("https://kinde.com/docs/important-information/terms-of-service/")
                                .contact(
                                        new Contact()
                                                .name("Kinde Support Team")
                                                .url("https://kinde.com/docs/")
                                                .email("support@kinde.com")
                                )
                                .version("1")
                )
                .components(
                        new Components()
                                .addSecuritySchemes("kindeBearerAuth", new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                )
                )
        ;
    }
}