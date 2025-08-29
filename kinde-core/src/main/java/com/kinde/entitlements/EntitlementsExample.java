package com.kinde.entitlements;

import com.kinde.KindeClient;
import com.kinde.KindeClientSession;
import com.kinde.KindeClientBuilder;
import com.kinde.accounts.dto.EntitlementDto;

import java.util.List;

/**
 * Example demonstrating how to use the entitlements functionality in the core library.
 */
public class EntitlementsExample {

    public static void main(String[] args) {
        // Create a Kinde client
        KindeClient client = KindeClientBuilder.builder()
                .domain("https://your-domain.kinde.com")
                .clientId("your-client-id")
                .clientSecret("your-client-secret")
                .redirectUri("http://localhost:8080/callback")
                .build();

        // Get a client session (M2M or user session)
        KindeClientSession session = client.clientSession();

        // Access entitlements functionality
        KindeEntitlements entitlements = session.entitlements();

        // Get all entitlements
        List<EntitlementDto> allEntitlements = entitlements.getEntitlements();
        System.out.println("Found " + allEntitlements.size() + " entitlements:");
        for (EntitlementDto entitlement : allEntitlements) {
            System.out.println("  - " + entitlement.getKey() + ": " + entitlement.getName());
        }
    }
} 