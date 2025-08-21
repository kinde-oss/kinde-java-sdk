package com.kinde.accounts.manager;

import com.kinde.accounts.dto.RoleDto;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Interface for managing roles operations.
 * Provides methods to retrieve roles from the Kinde Accounts API.
 */
public interface RolesManager {
    
    /**
     * Gets all roles for the current user.
     * This method automatically handles pagination to retrieve all roles.
     * 
     * @return List of role DTOs
     * @throws RuntimeException if the API call fails
     */
    List<RoleDto> getAllRoles();
    
    /**
     * Gets all roles for the current user asynchronously.
     * This method automatically handles pagination to retrieve all roles.
     * 
     * @return A CompletableFuture containing the list of role DTOs
     */
    CompletableFuture<List<RoleDto>> getAllRolesAsync();
}
