package com.kinde.accounts.example;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.kinde.KindeClientSession;
import com.kinde.accounts.KindeAccountsClient;
import com.kinde.accounts.dto.*;
import com.kinde.entitlements.KindeEntitlements;
import com.kinde.client.KindeClientGuiceModule;
import com.kinde.session.KindeSessionGuiceModule;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Example demonstrating how to use the KindeAccountsClient with Guice dependency injection.
 * This example shows how to retrieve user permissions, roles, entitlements, and feature flags
 * using clean DTOs without exposing OpenAPI internals or CompletableFuture complexity.
 */
public class AccountsExample {

    public static void main(String[] args) {
        // Create a Guice injector with the necessary modules
        Map<String, Object> parameters = new HashMap<>();
        // Add any required parameters here if needed
        
        Injector injector = Guice.createInjector(
            new KindeClientGuiceModule(parameters),
            new KindeSessionGuiceModule()
        );

        try {
            // Get the KindeClientSession from the injector
            KindeClientSession session = injector.getInstance(KindeClientSession.class);
            
            // Get the KindeAccountsClient from the injector
            KindeAccountsClient accountsClient = injector.getInstance(KindeAccountsClient.class);
            
            // Get the KindeEntitlements from the injector
            KindeEntitlements entitlements = injector.getInstance(KindeEntitlements.class);

            System.out.println("=== Kinde Accounts API Example ===");
            System.out.println("Using domain: " + session.getDomain());
            System.out.println();

            // Example 1: Get all entitlements
            System.out.println("1. Getting all entitlements...");
            List<EntitlementDto> entitlementsList = entitlements.getEntitlements();
            System.out.println("Found " + entitlementsList.size() + " entitlements:");
            for (EntitlementDto entitlement : entitlementsList) {
                System.out.println("  - " + entitlement.getKey() + ": " + entitlement.getName());
            }
            System.out.println();

            // Example 2: Get all permissions
            System.out.println("2. Getting all permissions...");
            List<PermissionDto> permissionsList = accountsClient.getPermissions();
            System.out.println("Found " + permissionsList.size() + " permissions:");
            for (PermissionDto permission : permissionsList) {
                System.out.println("  - " + permission.getKey() + ": " + permission.getName());
            }
            System.out.println();

            // Example 3: Get all roles
            System.out.println("3. Getting all roles...");
            List<RoleDto> rolesList = accountsClient.getRoles();
            System.out.println("Found " + rolesList.size() + " roles:");
            for (RoleDto role : rolesList) {
                System.out.println("  - " + role.getKey() + ": " + role.getName());
            }
            System.out.println();

            // Example 4: Get all feature flags
            System.out.println("4. Getting all feature flags...");
            List<FeatureFlagDto> featureFlagsList = accountsClient.getFeatureFlags();
            System.out.println("Found " + featureFlagsList.size() + " feature flags:");
            for (FeatureFlagDto featureFlag : featureFlagsList) {
                System.out.println("  - " + featureFlag.getKey() + ": " + featureFlag.getName() + " (enabled: " + featureFlag.isEnabled() + ")");
            }
            System.out.println();

            // Example 5: Get user organizations
            System.out.println("5. Getting user organizations...");
            List<OrganizationDto> organizationsList = accountsClient.getUserOrganizations();
            System.out.println("Found " + organizationsList.size() + " organizations:");
            for (OrganizationDto organization : organizationsList) {
                System.out.println("  - " + organization.getName() + " (" + organization.getCode() + ") - Default: " + organization.isDefault());
            }
            System.out.println();

            System.out.println("=== Example completed successfully ===");

        } catch (Exception e) {
            System.err.println("Error running example: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 