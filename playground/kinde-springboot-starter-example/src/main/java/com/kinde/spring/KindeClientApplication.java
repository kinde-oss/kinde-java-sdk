package com.kinde.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@SpringBootApplication
@EnableMethodSecurity(securedEnabled = true)
public class KindeClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(KindeClientApplication.class, args);
	}

	@Configuration
    static class SecurityConfig {
        @Bean
        SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http.authorizeHttpRequests()
                .anyRequest().authenticated()
                .and().oauth2Client()
                .and().oauth2Login()
                .and().oauth2ResourceServer().jwt();
            return http.build();
        }
    }

}
