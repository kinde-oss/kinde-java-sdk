package com.kinde.accounts.manager.impl;

import com.google.inject.Inject;
import com.kinde.accounts.dto.FeatureFlagDto;
import com.kinde.accounts.manager.FeatureFlagsManager;
import com.kinde.accounts.util.ApiResponseHandler;
import com.kinde.accounts.util.PaginationHelper;
import com.kinde.accounts.dto.DtoConverter;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.DefaultApi;
import org.openapitools.client.model.FeatureFlag;
import org.openapitools.client.model.FeatureFlagResponse;
import org.openapitools.client.model.FeatureFlagsResponse;
import org.openapitools.client.model.FeatureFlagsResponseData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Implementation of FeatureFlagsManager that handles feature flags operations.
 * Uses Guice dependency injection for dependencies.
 */
public class FeatureFlagsManagerImpl implements FeatureFlagsManager {
    
    private static final Logger log = LoggerFactory.getLogger(FeatureFlagsManagerImpl.class);
    
    private final DefaultApi apiClient;
    private final PaginationHelper paginationHelper;
    private final ApiResponseHandler responseHandler;
    
    @Inject
    public FeatureFlagsManagerImpl(
            DefaultApi apiClient,
            PaginationHelper paginationHelper,
            ApiResponseHandler responseHandler) {
        this.apiClient = apiClient;
        this.paginationHelper = paginationHelper;
        this.responseHandler = responseHandler;
    }
    
    @Override
    public List<FeatureFlagDto> getAllFeatureFlags() {
        try {
            return getAllFeatureFlagsAsync().get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to get feature flags", e);
        }
    }
    
    @Override
    public FeatureFlagDto getFeatureFlag(String key) {
        responseHandler.validateKey(key, "feature flag");
        try {
            return getFeatureFlagAsync(key).get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to get feature flag: " + key, e);
        }
    }
    
    @Override
    public CompletableFuture<List<FeatureFlagDto>> getAllFeatureFlagsAsync() {
        log.debug("Getting all feature flags asynchronously");
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                FeatureFlagsResponse allFeatureFlags = new FeatureFlagsResponse();
                FeatureFlagsResponseData allData = new FeatureFlagsResponseData();
                allData.setFeatureFlags(new ArrayList<>());
                allFeatureFlags.setData(allData);
                
                String startingAfter = null;
                boolean hasMorePages = true;
                
                while (hasMorePages) {
                    FeatureFlagsResponse pageResponse = apiClient.getFeatureFlags(startingAfter, 100);
                    
                    // Merge feature flags from this page
                    if (pageResponse.getData() != null && pageResponse.getData().getFeatureFlags() != null) {
                        allData.getFeatureFlags().addAll(pageResponse.getData().getFeatureFlags());
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
                return DtoConverter.toFeatureFlagDtos(allData.getFeatureFlags());
                
            } catch (ApiException ae) {
                responseHandler.handleApiException(ae, "get feature flags");
                return new ArrayList<>();
            } catch (Exception e) {
                responseHandler.handleGenericException(e, "get feature flags");
                return new ArrayList<>();
            }
        });
    }
    
    @Override
    public CompletableFuture<FeatureFlagDto> getFeatureFlagAsync(String key) {
        responseHandler.validateKey(key, "feature flag");
        log.debug("Getting feature flag with key: {} asynchronously", key);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                FeatureFlagResponse response = apiClient.getFeatureFlag(key);
                
                if (response.getData() != null) {
                    return DtoConverter.toFeatureFlagDto(response.getData());
                }
                
                return null;
                
            } catch (ApiException ae) {
                responseHandler.handleApiException(ae, "get feature flag: " + key);
                return null;
            } catch (Exception e) {
                responseHandler.handleGenericException(e, "get feature flag: " + key);
                return null;
            }
        });
    }
}
