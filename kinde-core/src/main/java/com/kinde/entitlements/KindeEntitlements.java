package com.kinde.entitlements;

import com.kinde.accounts.dto.EntitlementDto;

import java.util.List;

/**
 * Interface for accessing Kinde entitlements functionality.
 * This interface provides methods to query entitlements from the Kinde Accounts API
 * using clean DTOs without exposing OpenAPI internals.
 */
public interface KindeEntitlements {

    /**
     * Gets all entitlements for the current user's organization.
     * This method automatically handles pagination to retrieve all entitlements.
     * 
     * @return List of entitlement DTOs
     * @throws RuntimeException if the API call fails
     */
    List<EntitlementDto> getEntitlements();

    /**
     * Gets a specific entitlement by key.
     * 
     * @param key The entitlement key
     * @return The entitlement DTO, or null if not found
     * @throws RuntimeException if the API call fails
     */
    EntitlementDto getEntitlement(String key);
} 