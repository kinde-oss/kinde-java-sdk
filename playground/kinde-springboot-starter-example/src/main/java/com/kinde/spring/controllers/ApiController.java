package com.kinde.spring.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * REST API endpoints protected by Bearer-token JWT validation. Used to exercise the
 * resource-server path wired up via {@code KindeOAuth2ResourceServerAutoConfig}
 * (custom {@code NimbusJwtDecoder} + {@code KindeJwtAuthenticationConverter}).
 *
 * <p>Hit these with: {@code curl -H "Authorization: Bearer <access_token>" http://localhost:8080/api/me}.
 */
@RestController
@RequestMapping("/api")
public class ApiController {

    @GetMapping("/me")
    public Map<String, Object> me(JwtAuthenticationToken authentication) {
        Jwt jwt = authentication.getToken();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("name", authentication.getName());
        result.put("authorities", authentication.getAuthorities());
        result.put("claims", jwt.getClaims());
        result.put("headers", jwt.getHeaders());
        return result;
    }

    @GetMapping("/permissions")
    public Object permissions(Authentication authentication) {
        return authentication.getAuthorities();
    }
}
