package com.kinde.accounts;

import com.kinde.KindeClientSession;

/**
 * Simple interface for session information needed by the accounts client.
 * This is just an alias for KindeClientSession since we're in the same module.
 */
public interface KindeSession extends KindeClientSession {
    // This interface extends KindeClientSession to provide the required methods
    // getDomain() and getAccessToken() are already defined in KindeClientSession
} 