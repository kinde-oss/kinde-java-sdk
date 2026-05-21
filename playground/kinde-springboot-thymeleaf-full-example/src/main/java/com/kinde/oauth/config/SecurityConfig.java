package com.kinde.oauth.config;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

/**
 * Security configuration class that sets up the security filters and OAuth2 login
 * configurations for the application. This class enables method-level security
 * and configures the security filter chain with necessary handlers.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${jwk-set-uri}")
    private String issuerUri;

    @Value("${KINDE_LOGOUT_REDIRECT_URI:${app.base.url}}")
    private String postLogoutRedirectUri;

    /**
     * Configures the security filter chain, setting up CORS, authorization rules,
     * OAuth2 resource server, and OAuth2 login with OIDC user service.
     * <p>
     * Note: If you wish to use configuration-based security instead of method-based security,
     * you can uncomment the relevant lines and specify roles or permissions directly here.
     * </p>
     *
     * @param http the HttpSecurity to modify.
     * @return the configured SecurityFilterChain.
     * @throws Exception if an error occurs during configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   LogoutSuccessHandler oidcLogoutSuccessHandler) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**").permitAll()
                        .requestMatchers("/", "/home").permitAll()
                        /* This project is currently configured at the RestController method level for `read` & `admin`.
                         * `write` is configured below at the configuration level.
                         */
                        // .requestMatchers("/admin").hasRole("admins")
                        // .requestMatchers("/read").hasRole("read")
                         .requestMatchers("/write").hasRole("write")
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exceptions ->
                        exceptions
                                .accessDeniedHandler(accessDeniedHandler())
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .oidcUserService(new CustomOidcUserService(issuerUri))
                        )
                )
                // RP-initiated logout: clearing Spring's session is not enough — without this
                // handler the browser keeps Kinde's SSO cookie, so the next sign-in skips the
                // password prompt. The handler redirects to {issuer}/logout with id_token_hint
                // + post_logout_redirect_uri, terminating the upstream session as well.
                .logout(logout -> logout
                        .logoutSuccessHandler(oidcLogoutSuccessHandler)
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                );

        return http.build();
    }

    /**
     * RP-initiated logout success handler. Modeled on the same wiring that the Kinde Spring Boot
     * starter performs in {@code KindeOAuth2AutoConfig} when {@code kinde.oauth2.post-logout-redirect-uri}
     * is configured; reproduced here because this example uses raw {@code spring.security.oauth2.*}
     * properties and its own {@code SecurityFilterChain}, which bypasses the starter's auto-config.
     */
    @Bean
    public LogoutSuccessHandler oidcLogoutSuccessHandler(ClientRegistrationRepository clientRegistrationRepository) {
        OidcClientInitiatedLogoutSuccessHandler handler =
                new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);
        handler.setPostLogoutRedirectUri(postLogoutRedirectUri);
        return handler;
    }

    /**
     * Defines a bean for handling Access Denied (403 Forbidden) errors.
     * When an authenticated user tries to access a resource they do not have permission for,
     * they are redirected to the "/403" error page.
     *
     * @return an {@link AccessDeniedHandler} that forwards the request to the "/403" page.
     */
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) -> {
            RequestDispatcher requestDispatcher = request.getRequestDispatcher("/403");
            requestDispatcher.forward(request, response);
        };
    }
}
