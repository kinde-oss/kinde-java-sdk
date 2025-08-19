package com.kinde.entitlements;

import org.openapitools.client.model.EntitlementResponse;
import org.openapitools.client.model.EntitlementsResponse;

import java.util.concurrent.CompletableFuture;

/**
 * Interface for accessing entitlements functionality.
 * This provides methods to query the current user's entitlements from the Kinde Accounts API.
 */
public interface KindeEntitlements {

        /**
     * Gets all entitlements for the current user's organization.
     *
     * @return A CompletableFuture containing the entitlements response
     */
    CompletableFuture<EntitlementsResponse> getEntitlements();

    /**
     * Gets a specific entitlement by key.
     *
     * @param key The entitlement key to retrieve
     * @return A CompletableFuture containing the entitlement response
     */
    CompletableFuture<EntitlementResponse> getEntitlement(String key);
} 