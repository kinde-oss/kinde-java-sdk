package com.kinde.auth;

/**
 * Main authentication client that provides access to all authentication-related functionality.
 * This follows the same pattern as the Python SDK's main Auth class.
 */
public class Auth extends BaseAuth {
    
    private final Claims claims;
    private final Permissions permissions;
    private final FeatureFlags featureFlags;
    private final Roles roles;
    private final Entitlements entitlements;
    
    public Auth() {
        this.claims = new Claims();
        this.permissions = new Permissions();
        this.featureFlags = new FeatureFlags();
        this.roles = new Roles();
        this.entitlements = new Entitlements();
    }
    
    /**
     * Get access to claims functionality.
     * 
     * @return Claims instance for accessing token claims
     */
    public Claims claims() {
        return claims;
    }
    
    /**
     * Get access to permissions functionality.
     * 
     * @return Permissions instance for checking user permissions
     */
    public Permissions permissions() {
        return permissions;
    }
    
    /**
     * Get access to feature flags functionality.
     * 
     * @return FeatureFlags instance for accessing feature flags
     */
    public FeatureFlags featureFlags() {
        return featureFlags;
    }
    
    /**
     * Get access to roles functionality.
     * 
     * @return Roles instance for checking user roles
     */
    public Roles roles() {
        return roles;
    }
    
    /**
     * Get access to entitlements functionality.
     * 
     * @return Entitlements instance for accessing user entitlements
     */
    public Entitlements entitlements() {
        return entitlements;
    }
}
