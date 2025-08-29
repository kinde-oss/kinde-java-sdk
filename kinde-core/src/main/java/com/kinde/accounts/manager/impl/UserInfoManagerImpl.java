package com.kinde.accounts.manager.impl;

import com.google.inject.Inject;
import com.kinde.accounts.dto.OrganizationDto;
import com.kinde.accounts.manager.UserInfoManager;
import com.kinde.accounts.util.ApiResponseHandler;
import com.kinde.accounts.util.PaginationHelper;
import com.kinde.accounts.dto.DtoConverter;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.DefaultApi;
import org.openapitools.client.model.CurrentOrganizationResponse;
import org.openapitools.client.model.Organization;
import org.openapitools.client.model.UserOrganizationsResponse;
import org.openapitools.client.model.UserProfile;
import org.openapitools.client.model.UserProfileResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Implementation of UserInfoManager that handles user information operations.
 * Uses Guice dependency injection for dependencies.
 */
public class UserInfoManagerImpl implements UserInfoManager {
    
    private static final Logger log = LoggerFactory.getLogger(UserInfoManagerImpl.class);
    
    private final DefaultApi apiClient;
    private final PaginationHelper paginationHelper;
    private final ApiResponseHandler responseHandler;
    
    @Inject
    public UserInfoManagerImpl(
            DefaultApi apiClient,
            PaginationHelper paginationHelper,
            ApiResponseHandler responseHandler) {
        this.apiClient = apiClient;
        this.paginationHelper = paginationHelper;
        this.responseHandler = responseHandler;
    }
    
    @Override
    public List<OrganizationDto> getUserOrganizations() {
        try {
            return getUserOrganizationsAsync().get();
        } catch (InterruptedException | ExecutionException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new RuntimeException("Failed to get user organizations", e);
        }
    }
    
    @Override
    public UserProfile getUserProfile() {
        try {
            return getUserProfileAsync().get();
        } catch (InterruptedException | ExecutionException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new RuntimeException("Failed to get user profile", e);
        }
    }
    
    @Override
    public CurrentOrganizationResponse getCurrentOrganization() {
        try {
            return getCurrentOrganizationAsync().get();
        } catch (InterruptedException | ExecutionException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new RuntimeException("Failed to get current organization", e);
        }
    }
    
    @Override
    public CompletableFuture<List<OrganizationDto>> getUserOrganizationsAsync() {
        log.debug("Getting user organizations asynchronously");
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                UserOrganizationsResponse allOrganizations = new UserOrganizationsResponse();
                allOrganizations.setData(new ArrayList<>());
                
                String startingAfter = null;
                boolean hasMorePages = true;
                
                while (hasMorePages) {
                    UserOrganizationsResponse pageResponse = apiClient.getUserOrganizations(startingAfter, 100);
                    
                    // Merge organizations from this page
                    if (pageResponse.getData() != null) {
                        allOrganizations.getData().addAll(pageResponse.getData());
                    }
                    
                    // Check if there are more pages
                    if (pageResponse.getMetadata() != null && 
                        pageResponse.getMetadata().getNextPageStartingAfter() != null) {
                        startingAfter = pageResponse.getMetadata().getNextPageStartingAfter();
                        hasMorePages = true;
                    } else {
                        hasMorePages = false;
                    }
                }
                
                // Convert to DTOs
                return DtoConverter.toOrganizationDtos(allOrganizations.getData());
                
            } catch (ApiException ae) {
                responseHandler.handleApiException(ae, "get user organizations");
                return new ArrayList<>();
            } catch (Exception e) {
                responseHandler.handleGenericException(e, "get user organizations");
                return new ArrayList<>();
            }
        });
    }
    
    @Override
    public CompletableFuture<UserProfile> getUserProfileAsync() {
        log.debug("Getting user profile asynchronously");
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                UserProfileResponse response = apiClient.getUserProfile();
                return response.getData();
            } catch (ApiException ae) {
                responseHandler.handleApiException(ae, "get user profile");
                return null;
            } catch (Exception e) {
                responseHandler.handleGenericException(e, "get user profile");
                return null;
            }
        });
    }
    
    @Override
    public CompletableFuture<CurrentOrganizationResponse> getCurrentOrganizationAsync() {
        log.debug("Getting current organization asynchronously");
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                return apiClient.getCurrentOrganization();
            } catch (ApiException ae) {
                responseHandler.handleApiException(ae, "get current organization");
                return null;
            } catch (Exception e) {
                responseHandler.handleGenericException(e, "get current organization");
                return null;
            }
        });
    }
}
