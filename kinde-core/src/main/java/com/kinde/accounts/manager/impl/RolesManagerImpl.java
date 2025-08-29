package com.kinde.accounts.manager.impl;

import com.google.inject.Inject;
import com.kinde.accounts.dto.RoleDto;
import com.kinde.accounts.manager.RolesManager;
import com.kinde.accounts.util.ApiResponseHandler;
import com.kinde.accounts.util.PaginationHelper;
import com.kinde.accounts.dto.DtoConverter;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.DefaultApi;
import org.openapitools.client.model.Role;
import org.openapitools.client.model.RolesResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Implementation of RolesManager that handles roles operations.
 * Uses Guice dependency injection for dependencies.
 */
public class RolesManagerImpl implements RolesManager {
    
    private static final Logger log = LoggerFactory.getLogger(RolesManagerImpl.class);
    
    private final DefaultApi apiClient;
    private final PaginationHelper paginationHelper;
    private final ApiResponseHandler responseHandler;
    
    @Inject
    public RolesManagerImpl(
            DefaultApi apiClient,
            PaginationHelper paginationHelper,
            ApiResponseHandler responseHandler) {
        this.apiClient = apiClient;
        this.paginationHelper = paginationHelper;
        this.responseHandler = responseHandler;
    }
    
    @Override
    public List<RoleDto> getAllRoles() {
        try {
            return getAllRolesAsync().get();
        } catch (InterruptedException | ExecutionException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new RuntimeException("Failed to get roles", e);
        }
    }
    
    @Override
    public CompletableFuture<List<RoleDto>> getAllRolesAsync() {
        log.debug("Getting all roles asynchronously");
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                RolesResponse allRoles = new RolesResponse();
                allRoles.setData(new ArrayList<>());
                
                String startingAfter = null;
                boolean hasMorePages = true;
                
                while (hasMorePages) {
                    RolesResponse pageResponse = apiClient.getRoles(startingAfter, 100);
                    
                    // Merge roles from this page
                    if (pageResponse.getData() != null) {
                        allRoles.getData().addAll(pageResponse.getData());
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
                return DtoConverter.toRoleDtos(allRoles.getData());
                
            } catch (ApiException ae) {
                responseHandler.handleApiException(ae, "get roles");
                return new ArrayList<>();
            } catch (Exception e) {
                responseHandler.handleGenericException(e, "get roles");
                return new ArrayList<>();
            }
        });
    }
}
