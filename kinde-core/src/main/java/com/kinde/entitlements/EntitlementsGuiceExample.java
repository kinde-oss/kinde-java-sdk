package com.kinde.entitlements;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.kinde.KindeClient;
import com.kinde.KindeClientSession;
import com.kinde.token.KindeToken;
import com.kinde.accounts.dto.EntitlementDto;
import com.kinde.client.KindeClientGuiceModule;
import com.kinde.entitlements.KindeEntitlementsGuiceModule;
import com.kinde.session.KindeSessionGuiceModule;
import com.kinde.session.KindeSessionKindeTokenGuiceModule;
import com.kinde.token.AccessToken;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Example demonstrating how to use Kinde Entitlements with Guice dependency injection.
 * This example shows both M2M (Machine-to-Machine) and token-based session approaches.
 */
public class EntitlementsGuiceExample {

    public static void main(String[] args) {
        demonstrateM2MEntitlements();
        demonstrateTokenBasedEntitlements();
    }

    /**
     * Demonstrates using entitlements with M2M (Machine-to-Machine) session.
     * This shows how to configure Guice modules for M2M session functionality.
     */
    private static void demonstrateM2MEntitlements() {
        System.out.println("=== M2M Entitlements Example ===");
        
        // Create configuration parameters
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("domain", "https://your-domain.kinde.com");
        parameters.put("clientId", "your-client-id");
        parameters.put("clientSecret", "your-client-secret");
        parameters.put("redirectUri", "http://localhost:8080/callback");
        
        // Create Guice injector with all necessary modules
        Injector injector = Guice.createInjector(
            new KindeClientGuiceModule(parameters),
            new KindeSessionGuiceModule(),
            new KindeEntitlementsGuiceModule()
        );
        
        // Get the KindeClient from Guice
        KindeClient client = injector.getInstance(KindeClient.class);
        
        // Get a client session (M2M)
        KindeClientSession session = client.clientSession();
        
        // Access entitlements functionality through the session
        KindeEntitlements entitlements = session.entitlements();
        
        // Get all entitlements
        List<EntitlementDto> allEntitlements = entitlements.getEntitlements();
        System.out.println("M2M - Found " + allEntitlements.size() + " entitlements:");
        for (EntitlementDto entitlement : allEntitlements) {
            System.out.println("  - " + entitlement.getKey() + ": " + entitlement.getName());
        }
    }
    
    /**
     * Demonstrates using entitlements with a token-based session.
     * This shows how to configure Guice modules for token-based session functionality.
     */
    private static void demonstrateTokenBasedEntitlements() {
        System.out.println("\n=== Token-Based Entitlements Example ===");
        
        // Create configuration parameters
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("domain", "https://your-domain.kinde.com");
        parameters.put("clientId", "your-client-id");
        parameters.put("clientSecret", "your-client-secret");
        parameters.put("redirectUri", "http://localhost:8080/callback");
        
        // Create a sample access token (in real usage, this would come from OAuth flow)
        KindeToken accessToken = AccessToken.init("your-access-token", true);
        
        // Create Guice injector with token-based session module
        Injector injector = Guice.createInjector(
            new KindeClientGuiceModule(parameters),
            new KindeSessionKindeTokenGuiceModule(accessToken),
            new KindeEntitlementsGuiceModule()
        );
        
        // Get the KindeClientSession directly from Guice
        KindeClientSession session = injector.getInstance(KindeClientSession.class);
        
        // Access entitlements functionality through the session
        KindeEntitlements entitlements = session.entitlements();
        
        // Get all entitlements
        List<EntitlementDto> allEntitlements = entitlements.getEntitlements();
        System.out.println("Token-Based - Found " + allEntitlements.size() + " entitlements:");
        for (EntitlementDto entitlement : allEntitlements) {
            System.out.println("  - " + entitlement.getKey() + ": " + entitlement.getName());
        }
    }
}
