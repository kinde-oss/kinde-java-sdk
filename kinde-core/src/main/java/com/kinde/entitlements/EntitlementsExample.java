package com.kinde.entitlements;

import com.kinde.KindeClient;
import com.kinde.KindeClientSession;
import com.kinde.KindeClientBuilder;
import com.kinde.config.KindeConfig;

import org.openapitools.client.model.EntitlementResponse;
import org.openapitools.client.model.EntitlementsResponse;

import java.util.concurrent.CompletableFuture;

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
        CompletableFuture<EntitlementsResponse> allEntitlements = entitlements.getEntitlements();
        allEntitlements.thenAccept(result -> {
            System.out.println("All entitlements: " + result);
        });

        // Get a specific entitlement
        CompletableFuture<EntitlementResponse> specificEntitlement = entitlements.getEntitlement("premium-feature");
        specificEntitlement.thenAccept(result -> {
            System.out.println("Premium feature entitlement: " + result);
        });
    }
} 