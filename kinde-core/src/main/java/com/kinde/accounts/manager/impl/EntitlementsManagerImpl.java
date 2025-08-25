package com.kinde.accounts.manager.impl;

import com.google.inject.Inject;
import com.kinde.accounts.dto.EntitlementDto;
import com.kinde.accounts.manager.EntitlementsManager;
import com.kinde.accounts.util.ApiResponseHandler;
import com.kinde.accounts.util.PaginationHelper;
import com.kinde.accounts.dto.DtoConverter;
import com.kinde.KindeClientSession;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.DefaultApi;
import org.openapitools.client.model.Entitlement;
import org.openapitools.client.model.EntitlementResponse;
import org.openapitools.client.model.EntitlementsResponse;
import org.openapitools.client.model.EntitlementsResponseData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Implementation of EntitlementsManager that handles entitlements operations.
 * Uses Guice dependency injection for dependencies.
 */
public class EntitlementsManagerImpl implements EntitlementsManager {
    
    private static final Logger log = LoggerFactory.getLogger(EntitlementsManagerImpl.class);
    
    private final DefaultApi apiClient;
    private final PaginationHelper paginationHelper;
    private final ApiResponseHandler responseHandler;
    private final KindeClientSession session;
    
    @Inject
    public EntitlementsManagerImpl(
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
    public List<EntitlementDto> getAllEntitlements() {
        try {
            return getAllEntitlementsAsync().get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to get entitlements", e);
        }
    }
    
    @Override
    public EntitlementDto getEntitlement(String key) {
        responseHandler.validateKey(key, "entitlement");
        try {
            return getEntitlementAsync(key).get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to get entitlement: " + key, e);
        }
    }
    
    @Override
    public CompletableFuture<List<EntitlementDto>> getAllEntitlementsAsync() {
        log.debug("Getting all entitlements asynchronously");
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                EntitlementsResponse allEntitlements = new EntitlementsResponse();
                EntitlementsResponseData allData = new EntitlementsResponseData();
                allData.setEntitlements(new ArrayList<>());
                allData.setPlans(new ArrayList<>());
                allEntitlements.setData(allData);
                
                String startingAfter = null;
                boolean hasMorePages = true;
                
                while (hasMorePages) {
                    EntitlementsResponse pageResponse = apiClient.getEntitlements(startingAfter, 100);
                    
                    // Merge entitlements from this page
                    if (pageResponse.getData() != null && pageResponse.getData().getEntitlements() != null) {
                        allData.getEntitlements().addAll(pageResponse.getData().getEntitlements());
                    }
                    
                    // Merge plans from this page (take from first page only)
                    if (pageResponse.getData() != null && pageResponse.getData().getPlans() != null && 
                        allData.getPlans().isEmpty()) {
                        allData.setPlans(pageResponse.getData().getPlans());
                    }
                    
                    // Set org code from first page
                    if (pageResponse.getData() != null && pageResponse.getData().getOrgCode() != null && 
                        allData.getOrgCode() == null) {
                        allData.setOrgCode(pageResponse.getData().getOrgCode());
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
                return DtoConverter.toEntitlementDtos(allData.getEntitlements());
                
            } catch (ApiException ae) {
                responseHandler.handleApiException(ae, "get entitlements");
                return new ArrayList<>();
            } catch (Exception e) {
                responseHandler.handleGenericException(e, "get entitlements");
                return new ArrayList<>();
            }
        });
    }
    
    @Override
    public CompletableFuture<EntitlementDto> getEntitlementAsync(String key) {
        responseHandler.validateKey(key, "entitlement");
        log.debug("Getting entitlement with key: {} asynchronously", key);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                EntitlementResponse response = apiClient.getEntitlement(key);
                
                if (response.getData() != null && response.getData().getEntitlement() != null) {
                    return DtoConverter.toEntitlementDto(response.getData().getEntitlement());
                }
                
                return null;
                
            } catch (ApiException ae) {
                responseHandler.handleApiException(ae, "get entitlement: " + key);
                return null;
            } catch (Exception e) {
                responseHandler.handleGenericException(e, "get entitlement: " + key);
                return null;
            }
        });
    }
}
