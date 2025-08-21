package com.kinde.accounts.util;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Interface for handling pagination logic across different API endpoints.
 * Provides a generic way to handle paginated API responses.
 */
public interface PaginationHelper {
    
    /**
     * Handles pagination for API calls that return paginated responses.
     * 
     * @param apiCall Function that makes the API call with pagination parameters
     * @param dataExtractor Function that extracts the data list from the response
     * @param converter Function that converts the raw data to DTOs
     * @param <T> The API response type
     * @param <R> The raw data type
     * @param <D> The DTO type
     * @return A CompletableFuture containing the complete list of DTOs
     */
    <T, R, D> CompletableFuture<List<D>> paginateAsync(
        BiFunction<String, Integer, T> apiCall,
        Function<T, List<R>> dataExtractor,
        Function<List<R>, List<D>> converter
    );
    
    /**
     * Handles pagination for API calls that return paginated responses with metadata.
     * 
     * @param apiCall Function that makes the API call with pagination parameters
     * @param dataExtractor Function that extracts the data list from the response
     * @param converter Function that converts the raw data to DTOs
     * @param nextPageExtractor Function that extracts the next page token from response metadata
     * @param <T> The API response type
     * @param <R> The raw data type
     * @param <D> The DTO type
     * @return A CompletableFuture containing the complete list of DTOs
     */
    <T, R, D> CompletableFuture<List<D>> paginateAsync(
        BiFunction<String, Integer, T> apiCall,
        Function<T, List<R>> dataExtractor,
        Function<List<R>, List<D>> converter,
        Function<T, String> nextPageExtractor
    );
}
