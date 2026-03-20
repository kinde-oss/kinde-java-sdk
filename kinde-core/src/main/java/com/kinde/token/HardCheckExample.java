package com.kinde.token;

import com.kinde.KindeClient;
import com.kinde.KindeClientBuilder;
import com.kinde.KindeClientSession;
import com.kinde.KindeTokenFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Example demonstrating the "hard check" functionality for permissions, roles, and feature flags.
 *
 * Token-based checks (permissions, roles, feature flags) read directly from the JWT claims
 * and work with any token type, including M2M (client credentials) tokens.
 *
 * API-fallback checks (via KindeAccountsClient) require a user-context token obtained through
 * the authorization code flow (e.g., in a Spring Boot or J2EE app with a logged-in user).
 * They will not work with M2M tokens because the Account API needs org_code and sub claims.
 *
 * This standalone example demonstrates token-based checks only.
 * For API-fallback examples, see the Spring Boot playground application.
 */
public class HardCheckExample {

    private static final Logger log = LoggerFactory.getLogger(HardCheckExample.class);

    public static void main(String[] args) {
        KindeClient client = KindeClientBuilder.builder()
                .domain("https://your-domain.kinde.com")
                .clientId("your-client-id")
                .clientSecret("your-client-secret")
                .redirectUri("http://localhost:8080/callback")
                .addAudience("https://your-domain.kinde.com/api")
                .build();

        KindeClientSession session = client.clientSession();
        KindeToken token = session.retrieveTokens().getAccessToken();

        KindeTokenFactory tokenFactory = client.tokenFactory();
        KindeToken parsedToken = tokenFactory.parse(token.token());

        log.info("Token valid: {}", parsedToken.valid());

        checkPermissionsFromToken(parsedToken);
        checkRolesFromToken(parsedToken);
        checkFeatureFlags(parsedToken);
        checkFlagValues(parsedToken);
    }

    /**
     * Check permissions directly from the token claims.
     * Permissions are present when users have roles/permissions assigned
     * and the token is issued via the authorization code flow (user login).
     */
    private static void checkPermissionsFromToken(KindeToken token) {
        log.info("=== Permissions (from token claims) ===");

        List<String> permissions = token.getPermissions();
        if (permissions == null || permissions.isEmpty()) {
            log.info("No permissions found in token.");
            log.info("Tip: Permissions are included in user tokens when roles are assigned.");
        } else {
            log.info("Permissions in token: {}", permissions);

            boolean hasRead = token.hasPermission("read:users");
            log.info("Has 'read:users': {}", hasRead);

            List<String> toCheck = Arrays.asList("read:users", "write:users", "delete:users");
            boolean hasAny = token.hasAnyPermission(toCheck);
            boolean hasAll = token.hasAllPermissions(toCheck);
            log.info("Has any of {}: {}", toCheck, hasAny);
            log.info("Has all of {}: {}", toCheck, hasAll);
        }
    }

    /**
     * Check roles directly from the token claims.
     * Roles are present when users have roles assigned
     * and the token is issued via the authorization code flow (user login).
     */
    private static void checkRolesFromToken(KindeToken token) {
        log.info("=== Roles (from token claims) ===");

        List<String> roles = token.getRoles();
        if (roles == null || roles.isEmpty()) {
            log.info("No roles found in token.");
            log.info("Tip: Roles are included in user tokens when assigned at the organization level.");
        } else {
            log.info("Roles in token: {}", roles);

            List<String> toCheck = Arrays.asList("admin", "moderator", "user");
            boolean hasAny = token.hasAnyRole(toCheck);
            boolean hasAll = token.hasAllRoles(toCheck);
            log.info("Has any of {}: {}", toCheck, hasAny);
            log.info("Has all of {}: {}", toCheck, hasAll);
        }
    }

    /**
     * Check feature flags from the token claims.
     * Feature flags are included when enabled in the Kinde dashboard.
     */
    private static void checkFeatureFlags(KindeToken token) {
        log.info("=== Feature Flags (from token claims) ===");

        try {
            boolean darkMode = token.isFeatureFlagEnabled("dark_mode");
            log.info("dark_mode enabled: {}", darkMode);
        } catch (Exception e) {
            log.info("dark_mode: not found in token");
        }

        try {
            Object betaValue = token.getFeatureFlagValue("beta_features");
            log.info("beta_features value: {}", betaValue);
        } catch (Exception e) {
            log.info("beta_features: not found in token");
        }
    }

    /**
     * Dump all feature flags from the token for inspection.
     */
    private static void checkFlagValues(KindeToken token) {
        log.info("=== All Feature Flags ===");

        Map<String, Object> flags = token.getFlags();
        if (flags == null || flags.isEmpty()) {
            log.info("No feature flags found in token.");
        } else {
            for (Map.Entry<String, Object> entry : flags.entrySet()) {
                log.info("  {} = {}", entry.getKey(), entry.getValue());
            }
        }
    }
}
