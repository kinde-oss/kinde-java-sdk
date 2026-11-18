package com.kinde.spring;

import com.kinde.spring.config.KindeOAuth2Properties;
import com.kinde.stepup.AcrValue;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Runs requests through a real Spring Security filter chain for an API that is only a JWT
 * resource server: Kinde properties, no OAuth2 client registration. The Kinde configurer is
 * applied the way applications get it, through spring.factories.
 */
class KindeResourceServerStepUpChainTest {

    private static final String CHALLENGE = "Bearer error=\"insufficient_user_authentication\", "
            + "error_description=\"A different authentication level is required\", acr_values=\"mfa\"";

    /** Bearer "weak" is a single-factor token, "mfa" a token with acr=mfa. */
    static JwtDecoder jwtDecoder() {
        return token -> {
            Jwt.Builder jwt = Jwt.withTokenValue(token)
                    .header("alg", "RS256")
                    .subject("kp_1")
                    .issuedAt(Instant.now())
                    .expiresAt(Instant.now().plusSeconds(600));
            if ("mfa".equals(token)) {
                jwt.claim("acr", "mfa");
            }
            return jwt.build();
        };
    }

    @Configuration
    @EnableWebSecurity
    static class ResourceServerOnly {
        @Bean
        KindeOAuth2Properties kindeOAuth2Properties() {
            return new KindeOAuth2Properties(null);
        }

        @Bean
        JwtDecoder jwtDecoder() {
            return KindeResourceServerStepUpChainTest.jwtDecoder();
        }

        @Bean
        SecurityFilterChain api(HttpSecurity http) throws Exception {
            http.authorizeHttpRequests(requests -> requests
                            .requestMatchers("/sensitive").access(KindeStepUp.requires(AcrValue.MFA, null, true))
                            .requestMatchers("/admin").hasAuthority("admin")
                            .anyRequest().authenticated())
                    .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
            return http.build();
        }
    }

    @Configuration
    @EnableWebSecurity
    static class ResourceServerOnlyWithOwnHandler extends ResourceServerOnly {
        @Bean
        @Override
        SecurityFilterChain api(HttpSecurity http) throws Exception {
            http.authorizeHttpRequests(requests -> requests
                            .requestMatchers("/sensitive").access(KindeStepUp.requires(AcrValue.MFA, null, true))
                            .requestMatchers("/admin").hasAuthority("admin")
                            .anyRequest().authenticated())
                    .oauth2ResourceServer(oauth2 -> oauth2
                            .accessDeniedHandler((request, response, denied) -> {
                                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                                response.setHeader("X-App-Denied", "true");
                            })
                            .jwt(Customizer.withDefaults()));
            return http.build();
        }
    }

    private static MockHttpServletResponse call(Class<?> configuration, String path, String token) throws Exception {
        try (AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext()) {
            context.setServletContext(new MockServletContext());
            context.register(configuration);
            context.refresh();
            FilterChainProxy chain = context.getBean(FilterChainProxy.class);

            MockHttpServletRequest request = new MockHttpServletRequest("GET", path);
            request.setServletPath(path);
            request.addHeader("Authorization", "Bearer " + token);
            MockHttpServletResponse response = new MockHttpServletResponse();
            chain.doFilter(request, response, new MockFilterChain());
            return response;
        }
    }

    @Test
    @DisplayName("a failed step-up check gets the 401 challenge without a client registration")
    void stepUpChallengeWithoutLogin() throws Exception {
        MockHttpServletResponse response = call(ResourceServerOnly.class, "/sensitive", "weak");

        assertEquals(401, response.getStatus());
        assertEquals(CHALLENGE, response.getHeader("WWW-Authenticate"));
    }

    @Test
    @DisplayName("a token that meets the requirement passes")
    void stepUpPasses() throws Exception {
        MockHttpServletResponse response = call(ResourceServerOnly.class, "/sensitive", "mfa");

        assertEquals(200, response.getStatus());
    }

    @Test
    @DisplayName("an ordinary denial keeps Spring's default 403 response")
    void ordinaryDenialKeepsDefault() throws Exception {
        MockHttpServletResponse response = call(ResourceServerOnly.class, "/admin", "mfa");

        assertEquals(403, response.getStatus());
        assertTrue(String.valueOf(response.getHeader("WWW-Authenticate")).contains("insufficient_scope"),
                "Spring's bearer token handler should still answer ordinary denials");
    }

    @Test
    @DisplayName("an application's own access denied handler still answers ordinary denials")
    void applicationHandlerIsKept() throws Exception {
        MockHttpServletResponse ordinary = call(ResourceServerOnlyWithOwnHandler.class, "/admin", "mfa");
        assertEquals(403, ordinary.getStatus());
        assertEquals("true", ordinary.getHeader("X-App-Denied"));

        MockHttpServletResponse stepUp = call(ResourceServerOnlyWithOwnHandler.class, "/sensitive", "weak");
        assertEquals(401, stepUp.getStatus());
        assertEquals(CHALLENGE, stepUp.getHeader("WWW-Authenticate"));
        assertNull(stepUp.getHeader("X-App-Denied"));
    }
}
