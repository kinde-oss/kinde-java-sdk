package com.kinde.spring.config;

import com.kinde.spring.KindeAuthorizationRequestFailureHandler;
import com.kinde.spring.KindeStepUp;
import com.kinde.spring.KindeStepUpAccessDeniedHandler;
import com.kinde.stepup.AcrValue;
import org.springframework.security.config.ObjectPostProcessor;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import com.kinde.spring.KindeOAuth2AuthorizationRequestResolver;
import com.kinde.spring.resolver.CustomAuthorizationRequestResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * JWT-based resource server filter chain for /api/** endpoints.
     *
     * <p>Ordered before the browser-OAuth2-login chain so that requests to /api/** are handled by
     * Bearer token validation instead of being redirected to the Kinde login page. The
     * {@link org.springframework.security.oauth2.jwt.JwtDecoder} and
     * {@link org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter}
     * are auto-configured by kinde-springboot-core ({@code KindeOAuth2ResourceServerAutoConfig}).
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**")
                .authorizeHttpRequests(auth -> auth
                        // Step-up authentication: needs MFA from the last 5 minutes. A token
                        // without it is answered with the RFC 9470 challenge (401
                        // insufficient_user_authentication).
                        .requestMatchers("/api/sensitive").access(KindeStepUp.requires(AcrValue.MFA, 300))
                        .anyRequest().authenticated())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(
                        org.springframework.security.config.http.SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex.authenticationEntryPoint(
                        new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .oauth2ResourceServer(oauth2 -> oauth2
                        .accessDeniedHandler(new KindeStepUpAccessDeniedHandler())
                        .jwt(org.springframework.security.config.Customizer.withDefaults()));

        return http.build();
    }

    @Bean(name = "applicationSecurityFilterChain")
    SecurityFilterChain securityFilterChain(HttpSecurity http, OAuth2AuthorizationRequestResolver customAuthorizationRequestResolver) throws Exception {
        http
                .securityMatcher("/**") // Explicitly match all requests to avoid conflict
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/","/index.css", "/registration", "/oauth2/authorization/**", "/error").permitAll()
                        .anyRequest().authenticated())
                .oauth2Login(oauth2Login -> oauth2Login
                        .defaultSuccessUrl("/home", true)
                        .authorizationEndpoint(authorizationEndpoint -> authorizationEndpoint
                                .authorizationRequestResolver(customAuthorizationRequestResolver))
                        // Answer an invalid acr_values / max_age with HTTP 400 instead of 500
                        .withObjectPostProcessor(new ObjectPostProcessor<OAuth2AuthorizationRequestRedirectFilter>() {
                            @Override
                            public <O extends OAuth2AuthorizationRequestRedirectFilter> O postProcess(O filter) {
                                filter.setAuthenticationFailureHandler(new KindeAuthorizationRequestFailureHandler());
                                return filter;
                            }
                        })
                );

        return http.build();
    }

    // Redirect for /registration to trigger the registration flow
    @Bean
    public OAuth2AuthorizationRequestResolver customAuthorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository) {
        KindeOAuth2AuthorizationRequestResolver kindeResolver =
                new KindeOAuth2AuthorizationRequestResolver(clientRegistrationRepository, "/oauth2/authorization");

        return new CustomAuthorizationRequestResolver(kindeResolver);
    }
}