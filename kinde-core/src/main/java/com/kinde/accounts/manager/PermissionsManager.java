package com.kinde.accounts.manager;

import com.kinde.accounts.dto.PermissionDto;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Interface for managing permissions operations.
 * Provides methods to retrieve permissions from the Kinde Accounts API.
 */
public interface PermissionsManager {
    
    /**
     * Gets all permissions for the current user.
     * This method automatically handles pagination to retrieve all permissions.
     * 
     * @return List of permission DTOs
     * @throws RuntimeException if the API call fails
     */
    List<PermissionDto> getAllPermissions();
    
    /**
     * Gets a specific permission by key.
     * 
     * @param key The permission key
     * @return The permission DTO, or null if not found
     * @throws RuntimeException if the API call fails
     */
    PermissionDto getPermission(String key);
    
    /**
     * Gets all permissions for the current user asynchronously.
     * This method automatically handles pagination to retrieve all permissions.
     * 
     * @return A CompletableFuture containing the list of permission DTOs
     */
    CompletableFuture<List<PermissionDto>> getAllPermissionsAsync();
    
    /**
     * Gets a specific permission by key asynchronously.
     * 
     * @param key The permission key
     * @return A CompletableFuture containing the permission DTO, or null if not found
     */
    CompletableFuture<PermissionDto> getPermissionAsync(String key);
}
