package com.kinde.accounts.manager;

import com.kinde.accounts.dto.FeatureFlagDto;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Interface for managing feature flags operations.
 * Provides methods to retrieve feature flags from the Kinde Accounts API.
 */
public interface FeatureFlagsManager {
    
    /**
     * Gets all feature flags for the current user.
     * This method automatically handles pagination to retrieve all feature flags.
     * 
     * @return List of feature flag DTOs
     * @throws RuntimeException if the API call fails
     */
    List<FeatureFlagDto> getAllFeatureFlags();
    
    /**
     * Gets a specific feature flag by key.
     * 
     * @param key The feature flag key
     * @return The feature flag DTO, or null if not found
     * @throws RuntimeException if the API call fails
     */
    FeatureFlagDto getFeatureFlag(String key);
    
    /**
     * Gets all feature flags for the current user asynchronously.
     * This method automatically handles pagination to retrieve all feature flags.
     * 
     * @return A CompletableFuture containing the list of feature flag DTOs
     */
    CompletableFuture<List<FeatureFlagDto>> getAllFeatureFlagsAsync();
    
    /**
     * Gets a specific feature flag by key asynchronously.
     * 
     * @param key The feature flag key
     * @return A CompletableFuture containing the feature flag DTO, or null if not found
     */
    CompletableFuture<FeatureFlagDto> getFeatureFlagAsync(String key);
}
