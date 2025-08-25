package com.kinde.accounts.manager;

import com.kinde.accounts.dto.OrganizationDto;
import org.openapitools.client.model.CurrentOrganizationResponse;
import org.openapitools.client.model.UserProfile;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Interface for managing user information operations.
 * Provides methods to retrieve user profile and organization data from the Kinde Accounts API.
 */
public interface UserInfoManager {
    
    /**
     * Gets all organizations for the current user.
     * This method automatically handles pagination to retrieve all organizations.
     * 
     * @return List of organization DTOs
     * @throws RuntimeException if the API call fails
     */
    List<OrganizationDto> getUserOrganizations();
    
    /**
     * Gets the current user's profile.
     * 
     * @return The user profile
     * @throws RuntimeException if the API call fails
     */
    UserProfile getUserProfile();
    
    /**
     * Gets the current organization.
     * 
     * @return The current organization response
     * @throws RuntimeException if the API call fails
     */
    CurrentOrganizationResponse getCurrentOrganization();
    
    /**
     * Gets all organizations for the current user asynchronously.
     * This method automatically handles pagination to retrieve all organizations.
     * 
     * @return A CompletableFuture containing the list of organization DTOs
     */
    CompletableFuture<List<OrganizationDto>> getUserOrganizationsAsync();
    
    /**
     * Gets the current user's profile asynchronously.
     * 
     * @return A CompletableFuture containing the user profile
     */
    CompletableFuture<UserProfile> getUserProfileAsync();
    
    /**
     * Gets the current organization asynchronously.
     * 
     * @return A CompletableFuture containing the current organization response
     */
    CompletableFuture<CurrentOrganizationResponse> getCurrentOrganizationAsync();
}
