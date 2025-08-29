package com.kinde.accounts.util;

import org.openapitools.client.ApiException;

/**
 * Interface for handling API response errors and exceptions.
 * Provides a consistent way to handle API errors across different managers.
 */
public interface ApiResponseHandler {
    
    /**
     * Handles API exceptions with proper error context.
     * 
     * @param ae The API exception
     * @param operation Description of the operation that failed
     * @throws RuntimeException with a user-friendly error message
     */
    void handleApiException(ApiException ae, String operation);
    
    /**
     * Handles generic exceptions with proper error context.
     * 
     * @param e The generic exception
     * @param operation Description of the operation that failed
     * @throws RuntimeException with a user-friendly error message
     */
    void handleGenericException(Exception e, String operation);
    
    /**
     * Validates that a key parameter is not null or empty.
     * 
     * @param key The key to validate
     * @param keyType The type of key (e.g., "entitlement", "permission")
     * @throws IllegalArgumentException if the key is null or empty
     */
    void validateKey(String key, String keyType);
}
