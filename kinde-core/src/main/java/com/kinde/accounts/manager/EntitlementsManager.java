package com.kinde.accounts.manager;

import com.kinde.accounts.dto.EntitlementDto;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Interface for managing entitlements operations.
 * Provides methods to retrieve entitlements from the Kinde Accounts API.
 */
public interface EntitlementsManager {
    
    /**
     * Gets all entitlements for the current user's organization.
     * This method automatically handles pagination to retrieve all entitlements.
     * 
     * @return List of entitlement DTOs
     * @throws RuntimeException if the API call fails
     */
    List<EntitlementDto> getAllEntitlements();
    
    /**
     * Gets a specific entitlement by key.
     * 
     * @param key The entitlement key
     * @return The entitlement DTO, or null if not found
     * @throws RuntimeException if the API call fails
     */
    EntitlementDto getEntitlement(String key);
    
    /**
     * Gets all entitlements for the current user's organization asynchronously.
     * This method automatically handles pagination to retrieve all entitlements.
     * 
     * @return A CompletableFuture containing the list of entitlement DTOs
     */
    CompletableFuture<List<EntitlementDto>> getAllEntitlementsAsync();
    
    /**
     * Gets a specific entitlement by key asynchronously.
     * 
     * @param key The entitlement key
     * @return A CompletableFuture containing the entitlement DTO, or null if not found
     */
    CompletableFuture<EntitlementDto> getEntitlementAsync(String key);
}
