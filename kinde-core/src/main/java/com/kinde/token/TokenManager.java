package com.kinde.token;

import com.kinde.KindeClient;
import com.kinde.KindeClientBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Manages token lifecycle including automatic refresh when tokens are about to expire.
 * Thread-safe implementation for concurrent access.
 */
@Slf4j
public class TokenManager {
    
    private final KindeTokens tokens;
    private final int refreshBufferMinutes;
    private final ReentrantLock refreshLock = new ReentrantLock();
    
    public TokenManager(KindeTokens tokens, int refreshBufferMinutes) {
        this.tokens = tokens;
        this.refreshBufferMinutes = refreshBufferMinutes;
    }
    
    /**
     * Checks if the access token needs to be refreshed.
     * @return true if token needs refresh, false otherwise
     */
    public boolean needsRefresh() {
        if (tokens == null || tokens.getAccessToken() == null) {
            log.debug("No access token available for refresh check");
            return false;
        }
        
        Object expObj = tokens.getAccessToken().getClaim("exp");
        if (expObj == null) {
            log.debug("No expiration claim found in access token");
            return false;
        }
        
        long exp;
        if (expObj instanceof Number) {
            exp = ((Number) expObj).longValue();
        } else if (expObj instanceof java.util.Date) {
            exp = ((java.util.Date) expObj).getTime() / 1000L;
        } else {
            log.debug("Unexpected expiration claim type: {}", expObj.getClass());
            return false;
        }
        
        if (exp <= 0) {
            log.debug("Token already expired");
            return true;
        }
        
        long now = System.currentTimeMillis() / 1000L;
        long bufferSeconds = refreshBufferMinutes * 60L;
        boolean needsRefresh = (exp - now) <= bufferSeconds;
        
        log.debug("Token refresh check - Current time: {}, Expiry: {}, Buffer: {}s, Needs refresh: {}", 
                now, exp, bufferSeconds, needsRefresh);
        
        return needsRefresh;
    }
    
    /**
     * Checks if refresh is possible (refresh token available).
     * @return true if refresh token is available, false otherwise
     */
    public boolean canRefresh() {
        boolean canRefresh = tokens != null && 
                           tokens.getRefreshToken() != null && 
                           tokens.getRefreshToken().token() != null;
        
        log.debug("Can refresh check - Result: {}", canRefresh);
        return canRefresh;
    }
    
    /**
     * Refreshes the tokens using the refresh token.
     * Thread-safe implementation to prevent multiple concurrent refresh attempts.
     * @return new tokens after refresh
     * @throws IllegalStateException if no refresh token is available
     * @throws Exception if refresh fails
     */
    public KindeTokens refresh() throws Exception {
        if (!canRefresh()) {
            throw new IllegalStateException("Cannot refresh: no refresh token available");
        }
        
        // Use lock to prevent multiple concurrent refresh attempts
        if (!refreshLock.tryLock()) {
            log.debug("Refresh already in progress, waiting for completion");
            refreshLock.lock();
            refreshLock.unlock();
            return tokens; // Return current tokens if refresh was already completed
        }
        
        try {
            log.info("Starting token refresh");
            KindeClient kindeClient = KindeClientBuilder.builder().build();
            RefreshToken refreshToken = tokens.getRefreshToken();
            KindeTokens newTokens = kindeClient.initClientSession(refreshToken).retrieveTokens();
            log.info("Token refresh completed successfully");
            return newTokens;
        } finally {
            refreshLock.unlock();
        }
    }
    
    /**
     * Gets the current tokens.
     * @return current tokens
     */
    public KindeTokens getTokens() {
        return tokens;
    }
} 