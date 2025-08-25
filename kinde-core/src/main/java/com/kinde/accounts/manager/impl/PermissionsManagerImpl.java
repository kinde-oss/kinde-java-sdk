package com.kinde.accounts.manager.impl;

import com.google.inject.Inject;
import com.kinde.accounts.dto.PermissionDto;
import com.kinde.accounts.manager.PermissionsManager;
import com.kinde.accounts.util.ApiResponseHandler;
import com.kinde.accounts.util.PaginationHelper;
import com.kinde.accounts.dto.DtoConverter;
import com.kinde.KindeClientSession;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.DefaultApi;
import org.openapitools.client.model.Permission;
import org.openapitools.client.model.PermissionResponse;
import org.openapitools.client.model.PermissionsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Implementation of PermissionsManager that handles permissions operations.
 * Uses Guice dependency injection for dependencies.
 */
public class PermissionsManagerImpl implements PermissionsManager {
    
    private static final Logger log = LoggerFactory.getLogger(PermissionsManagerImpl.class);
    
    private final DefaultApi apiClient;
    private final PaginationHelper paginationHelper;
    private final ApiResponseHandler responseHandler;
    private final KindeClientSession session;
    
    @Inject
    public PermissionsManagerImpl(
            DefaultApi apiClient,
            PaginationHelper paginationHelper,
            ApiResponseHandler responseHandler,
            KindeClientSession session) {
        this.apiClient = apiClient;
        this.paginationHelper = paginationHelper;
        this.responseHandler = responseHandler;
        this.session = session;
        configureApiClient();
    }
    
    /**
     * Configures the API client with authentication headers from the session.
     */
    private void configureApiClient() {
        String accessToken = session.getAccessToken();
        if (accessToken != null && !accessToken.isEmpty()) {
            apiClient.getApiClient().setBearerToken(accessToken);
        }
    }
    
    @Override
    public List<PermissionDto> getAllPermissions() {
        try {
            return getAllPermissionsAsync().get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to get permissions", e);
        }
    }
    
    @Override
    public PermissionDto getPermission(String key) {
        responseHandler.validateKey(key, "permission");
        try {
            return getPermissionAsync(key).get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to get permission: " + key, e);
        }
    }
    
    @Override
    public CompletableFuture<List<PermissionDto>> getAllPermissionsAsync() {
        log.debug("Getting all permissions asynchronously");
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                PermissionsResponse allPermissions = new PermissionsResponse();
                allPermissions.setData(new ArrayList<>());
                
                String startingAfter = null;
                boolean hasMorePages = true;
                
                while (hasMorePages) {
                    PermissionsResponse pageResponse = apiClient.getPermissions(startingAfter, 100);
                    
                    // Merge permissions from this page
                    if (pageResponse.getData() != null) {
                        allPermissions.getData().addAll(pageResponse.getData());
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
                return DtoConverter.toPermissionDtos(allPermissions.getData());
                
            } catch (ApiException ae) {
                responseHandler.handleApiException(ae, "get permissions");
                return new ArrayList<>();
            } catch (Exception e) {
                responseHandler.handleGenericException(e, "get permissions");
                return new ArrayList<>();
            }
        });
    }
    
    @Override
    public CompletableFuture<PermissionDto> getPermissionAsync(String key) {
        responseHandler.validateKey(key, "permission");
        log.debug("Getting permission with key: {} asynchronously", key);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                PermissionResponse response = apiClient.getPermission(key);
                
                if (response.getData() != null) {
                    return DtoConverter.toPermissionDto(response.getData());
                }
                
                return null;
                
            } catch (ApiException ae) {
                responseHandler.handleApiException(ae, "get permission: " + key);
                return null;
            } catch (Exception e) {
                responseHandler.handleGenericException(e, "get permission: " + key);
                return null;
            }
        });
    }
}
