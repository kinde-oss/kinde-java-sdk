package com.kinde.accounts.util;

import org.openapitools.client.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of ApiResponseHandler that provides consistent error handling for API calls.
 */
public class ApiResponseHandlerImpl implements ApiResponseHandler {
    
    private static final Logger log = LoggerFactory.getLogger(ApiResponseHandlerImpl.class);
    
    @Override
    public void handleApiException(ApiException ae, String operation) {
        log.error("API exception during {}: status={}, message={}", 
                operation, ae.getCode(), ae.getMessage());
        throw new RuntimeException(
                String.format("Failed to %s (status %d): %s", 
                        operation, ae.getCode(), ae.getMessage()), ae);
    }
    
    @Override
    public void handleGenericException(Exception e, String operation) {
        log.error("Generic exception during {}: {}", operation, e.getMessage(), e);
        throw new RuntimeException(String.format("Failed to %s: %s", operation, e.getMessage()), e);
    }
    
    @Override
    public void validateKey(String key, String keyType) {
        if (key == null || key.trim().isEmpty()) {
            // Capitalize the first letter of the key type for consistent error messages
            String capitalizedKeyType = keyType.substring(0, 1).toUpperCase() + keyType.substring(1);
            throw new IllegalArgumentException(String.format("%s key cannot be null or empty", capitalizedKeyType));
        }
    }
}
