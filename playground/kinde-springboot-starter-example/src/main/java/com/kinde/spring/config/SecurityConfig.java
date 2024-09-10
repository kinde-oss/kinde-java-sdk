package com.kinde.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/home/**").authenticated()
                        .requestMatchers("/**").permitAll()
                )
                .oauth2Client()
                .and()
                .oauth2Login()
                .and()
                .oauth2ResourceServer().jwt();
        return http.build();
    }
}