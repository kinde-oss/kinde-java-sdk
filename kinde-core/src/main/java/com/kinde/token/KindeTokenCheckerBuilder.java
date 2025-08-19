package com.kinde.token;

import com.kinde.KindeClientSession;

/**
 * Builder class for creating KindeTokenChecker instances with a fluent API.
 */
public class KindeTokenCheckerBuilder {
    
    private KindeToken token;
    private KindeClientSession session;
    
    /**
     * Sets the token to check.
     * 
     * @param token The KindeToken to check
     * @return This builder instance
     */
    public KindeTokenCheckerBuilder token(KindeToken token) {
        this.token = token;
        return this;
    }
    
    /**
     * Sets the session for API fallback.
     * 
     * @param session The KindeClientSession for API fallback
     * @return This builder instance
     */
    public KindeTokenCheckerBuilder session(KindeClientSession session) {
        this.session = session;
        return this;
    }
    
    /**
     * Builds a new KindeTokenChecker instance.
     * 
     * @return A new KindeTokenChecker instance
     * @throws IllegalArgumentException if token or session is null
     */
    public KindeTokenChecker build() {
        if (token == null) {
            throw new IllegalArgumentException("Token cannot be null");
        }
        if (session == null) {
            throw new IllegalArgumentException("Session cannot be null");
        }
        return new KindeTokenChecker(token, session);
    }
    
    /**
     * Creates a new builder instance.
     * 
     * @return A new KindeTokenCheckerBuilder instance
     */
    public static KindeTokenCheckerBuilder builder() {
        return new KindeTokenCheckerBuilder();
    }
}
