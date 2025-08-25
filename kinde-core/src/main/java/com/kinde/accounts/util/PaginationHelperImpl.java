package com.kinde.accounts.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Implementation of PaginationHelper that handles pagination logic for API calls.
 */
public class PaginationHelperImpl implements PaginationHelper {
    
    private static final Logger log = LoggerFactory.getLogger(PaginationHelperImpl.class);
    private static final int DEFAULT_PAGE_SIZE = 100;
    
    @Override
    public <T, R, D> CompletableFuture<List<D>> paginateAsync(
            BiFunction<String, Integer, T> apiCall,
            Function<T, List<R>> dataExtractor,
            Function<List<R>, List<D>> converter) {
        
        return paginateAsync(apiCall, dataExtractor, converter, this::extractNextPageToken);
    }
    
    @Override
    public <T, R, D> CompletableFuture<List<D>> paginateAsync(
            BiFunction<String, Integer, T> apiCall,
            Function<T, List<R>> dataExtractor,
            Function<List<R>, List<D>> converter,
            Function<T, String> nextPageExtractor) {
        
        return CompletableFuture.supplyAsync(() -> {
            List<R> allData = new ArrayList<>();
            String startingAfter = null;
            boolean hasMorePages = true;
            
            while (hasMorePages) {
                try {
                    T pageResponse = apiCall.apply(startingAfter, DEFAULT_PAGE_SIZE);
                    
                    // Extract data from this page
                    List<R> pageData = dataExtractor.apply(pageResponse);
                    if (pageData != null) {
                        allData.addAll(pageData);
                    }
                    
                    // Check if there are more pages
                    startingAfter = nextPageExtractor.apply(pageResponse);
                    hasMorePages = startingAfter != null;
                    
                    log.debug("Retrieved page with {} items, hasMorePages: {}", 
                            pageData != null ? pageData.size() : 0, hasMorePages);
                    
                } catch (Exception e) {
                    log.error("Error during pagination", e);
                    throw new RuntimeException("Failed to retrieve paginated data", e);
                }
            }
            
            // Convert all collected data to DTOs
            return converter.apply(allData);
        });
    }
    
    /**
     * Default implementation to extract next page token from response metadata.
     * This is a generic implementation that can be overridden by specific managers.
     * 
     * @param response The API response
     * @return The next page token, or null if no more pages
     */
    private <T> String extractNextPageToken(T response) {
        // This is a generic implementation that assumes the response has a metadata field
        // with a nextPageStartingAfter field. Specific managers can override this.
        try {
            // Use reflection to access metadata.nextPageStartingAfter if it exists
            var metadataField = response.getClass().getMethod("getMetadata");
            var metadata = metadataField.invoke(response);
            if (metadata != null) {
                var nextPageField = metadata.getClass().getMethod("getNextPageStartingAfter");
                return (String) nextPageField.invoke(metadata);
            }
        } catch (Exception e) {
            log.debug("Could not extract next page token using reflection: {}", e.getMessage());
        }
        return null;
    }
}
