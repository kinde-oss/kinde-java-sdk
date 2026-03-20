package com.kinde.token;

import com.kinde.KindeClient;
import com.kinde.KindeClientBuilder;
import com.kinde.KindeClientSession;
import com.kinde.KindeTokenFactory;

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

        System.out.println("Token valid: " + parsedToken.valid());
        System.out.println();

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
        System.out.println("=== Permissions (from token claims) ===");

        List<String> permissions = token.getPermissions();
        if (permissions == null || permissions.isEmpty()) {
            System.out.println("No permissions found in token.");
            System.out.println("Tip: Permissions are included in user tokens when roles are assigned.");
        } else {
            System.out.println("Permissions in token: " + permissions);

            boolean hasRead = token.hasPermission("read:users");
            System.out.println("Has 'read:users': " + hasRead);

            List<String> toCheck = Arrays.asList("read:users", "write:users", "delete:users");
            boolean hasAny = token.hasAnyPermission(toCheck);
            boolean hasAll = token.hasAllPermissions(toCheck);
            System.out.println("Has any of " + toCheck + ": " + hasAny);
            System.out.println("Has all of " + toCheck + ": " + hasAll);
        }
        System.out.println();
    }

    /**
     * Check roles directly from the token claims.
     * Roles are present when users have roles assigned
     * and the token is issued via the authorization code flow (user login).
     */
    private static void checkRolesFromToken(KindeToken token) {
        System.out.println("=== Roles (from token claims) ===");

        List<String> roles = token.getRoles();
        if (roles == null || roles.isEmpty()) {
            System.out.println("No roles found in token.");
            System.out.println("Tip: Roles are included in user tokens when assigned at the organization level.");
        } else {
            System.out.println("Roles in token: " + roles);

            List<String> toCheck = Arrays.asList("admin", "moderator", "user");
            boolean hasAny = token.hasAnyRole(toCheck);
            boolean hasAll = token.hasAllRoles(toCheck);
            System.out.println("Has any of " + toCheck + ": " + hasAny);
            System.out.println("Has all of " + toCheck + ": " + hasAll);
        }
        System.out.println();
    }

    /**
     * Check feature flags from the token claims.
     * Feature flags are included when enabled in the Kinde dashboard.
     */
    private static void checkFeatureFlags(KindeToken token) {
        System.out.println("=== Feature Flags (from token claims) ===");

        try {
            boolean darkMode = token.isFeatureFlagEnabled("dark_mode");
            System.out.println("dark_mode enabled: " + darkMode);
        } catch (Exception e) {
            System.out.println("dark_mode: not found in token");
        }

        try {
            Object betaValue = token.getFeatureFlagValue("beta_features");
            System.out.println("beta_features value: " + betaValue);
        } catch (Exception e) {
            System.out.println("beta_features: not found in token");
        }
        System.out.println();
    }

    /**
     * Dump all feature flags from the token for inspection.
     */
    private static void checkFlagValues(KindeToken token) {
        System.out.println("=== All Feature Flags ===");

        Map<String, Object> flags = token.getFlags();
        if (flags == null || flags.isEmpty()) {
            System.out.println("No feature flags found in token.");
        } else {
            for (Map.Entry<String, Object> entry : flags.entrySet()) {
                System.out.println("  " + entry.getKey() + " = " + entry.getValue());
            }
        }
        System.out.println();
    }
}
