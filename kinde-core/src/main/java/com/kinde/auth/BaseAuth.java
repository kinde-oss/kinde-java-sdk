package com.kinde.auth;

import com.kinde.KindeClientSession;
import com.kinde.guice.KindeGuiceSingleton;
import com.kinde.guice.KindeEnvironmentSingleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Base class for authentication-related functionality that provides
 * shared methods for accessing the session and token manager.
 * This follows the same pattern as the Python SDK's BaseAuth class.
 */
public abstract class BaseAuth {
    
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    
    /**
     * Gets the KindeClientSession from the Guice singleton.
     * 
     * @return Optional containing the session if available
     */
    protected Optional<KindeClientSession> getSession() {
        try {
            // Get the Guice injector from the singleton
            KindeGuiceSingleton guiceSingleton = KindeGuiceSingleton.getInstance();
            if (guiceSingleton == null) {
                logger.debug("KindeGuiceSingleton not initialized");
                return Optional.empty();
            }
            
            // Try to get the session from the injector
            KindeClientSession session = guiceSingleton.getInjector().getInstance(KindeClientSession.class);
            if (session != null) {
                logger.debug("Successfully retrieved session from Guice container");
                return Optional.of(session);
            } else {
                logger.debug("No session found in Guice container");
                return Optional.empty();
            }
        } catch (Exception e) {
            logger.debug("Could not get session from Guice container: {}", e.getMessage());
            return Optional.empty();
        }
    }
    
    /**
     * Gets the token manager for the current user.
     * 
     * @return Optional containing the token if available
     */
    protected Optional<com.kinde.token.KindeToken> getToken() {
        return getSession().map(session -> {
            try {
                return session.retrieveTokens().getAccessToken();
            } catch (Exception e) {
                logger.debug("Could not get token from session: {}", e.getMessage());
                return null;
            }
        });
    }
}
