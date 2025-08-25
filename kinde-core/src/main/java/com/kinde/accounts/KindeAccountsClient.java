package com.kinde.accounts;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Inject;
import com.kinde.KindeClientSession;
import com.kinde.accounts.dto.*;
import com.kinde.accounts.manager.EntitlementsManager;
import com.kinde.accounts.manager.FeatureFlagsManager;
import com.kinde.accounts.manager.PermissionsManager;
import com.kinde.accounts.manager.RolesManager;
import com.kinde.accounts.manager.UserInfoManager;
import org.openapitools.client.model.CurrentOrganizationResponse;
import org.openapitools.client.model.UserProfile;

import java.util.List;

/**
 * Client for accessing Kinde Accounts API functionality.
 * This client provides methods to query the current user's permissions, roles, entitlements, and feature flags.
 * This implementation uses composition with specialized managers and Guice dependency injection.
 */
public class KindeAccountsClient {

    private final EntitlementsManager entitlementsManager;
    private final PermissionsManager permissionsManager;
    private final RolesManager rolesManager;
    private final FeatureFlagsManager featureFlagsManager;
    private final UserInfoManager userInfoManager;

    /**
     * Primary constructor for Guice dependency injection.
     * 
     * @param entitlementsManager The entitlements manager
     * @param permissionsManager The permissions manager
     * @param rolesManager The roles manager
     * @param featureFlagsManager The feature flags manager
     * @param userInfoManager The user info manager
     */
    @Inject
    public KindeAccountsClient(
            EntitlementsManager entitlementsManager,
            PermissionsManager permissionsManager,
            RolesManager rolesManager,
            FeatureFlagsManager featureFlagsManager,
            UserInfoManager userInfoManager) {
        this.entitlementsManager = entitlementsManager;
        this.permissionsManager = permissionsManager;
        this.rolesManager = rolesManager;
        this.featureFlagsManager = featureFlagsManager;
        this.userInfoManager = userInfoManager;
    }

    /**
     * Backward compatibility constructor for testing and manual instantiation.
     * Creates a Guice injector with the KindeAccountsModule and injects dependencies.
     * 
     * @param session The KindeClientSession to use for authentication
     * @param useDirectInstantiation Flag to indicate direct instantiation (for backward compatibility)
     */
    public KindeAccountsClient(KindeClientSession session, boolean useDirectInstantiation) {
        if (session == null) {
            throw new IllegalArgumentException("session cannot be null");
        }
        
        // Create a child injector with both the module and session binding
        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                // Install the KindeAccountsModule to provide all manager bindings
                install(new KindeAccountsModule());
                // Bind the session instance
                bind(KindeClientSession.class).toInstance(session);
            }
        });
        
        // Inject dependencies
        this.entitlementsManager = injector.getInstance(EntitlementsManager.class);
        this.permissionsManager = injector.getInstance(PermissionsManager.class);
        this.rolesManager = injector.getInstance(RolesManager.class);
        this.featureFlagsManager = injector.getInstance(FeatureFlagsManager.class);
        this.userInfoManager = injector.getInstance(UserInfoManager.class);
    }

    /**
     * Gets all entitlements for the current user's organization.
     * This method automatically handles pagination to retrieve all entitlements.
     * 
     * @return List of entitlement DTOs
     * @throws RuntimeException if the API call fails
     */
    public List<EntitlementDto> getEntitlements() {
        return entitlementsManager.getAllEntitlements();
    }

    /**
     * Gets all permissions for the current user.
     * This method automatically handles pagination to retrieve all permissions.
     * 
     * @return List of permission DTOs
     * @throws RuntimeException if the API call fails
     */
    public List<PermissionDto> getPermissions() {
        return permissionsManager.getAllPermissions();
    }

    /**
     * Gets all roles for the current user.
     * This method automatically handles pagination to retrieve all roles.
     * 
     * @return List of role DTOs
     * @throws RuntimeException if the API call fails
     */
    public List<RoleDto> getRoles() {
        return rolesManager.getAllRoles();
    }

    /**
     * Gets all feature flags for the current user.
     * This method automatically handles pagination to retrieve all feature flags.
     * 
     * @return List of feature flag DTOs
     * @throws RuntimeException if the API call fails
     */
    public List<FeatureFlagDto> getFeatureFlags() {
        return featureFlagsManager.getAllFeatureFlags();
    }

    /**
     * Gets a specific feature flag by key.
     * 
     * @param key The feature flag key
     * @return The feature flag DTO, or null if not found
     * @throws RuntimeException if the API call fails
     */
    public FeatureFlagDto getFeatureFlag(String key) {
        return featureFlagsManager.getFeatureFlag(key);
    }

    /**
     * Gets a specific entitlement by key.
     * 
     * @param key The entitlement key
     * @return The entitlement DTO, or null if not found
     * @throws RuntimeException if the API call fails
     */
    public EntitlementDto getEntitlement(String key) {
        return entitlementsManager.getEntitlement(key);
    }

    /**
     * Gets a specific permission by key.
     * 
     * @param key The permission key
     * @return The permission DTO, or null if not found
     * @throws RuntimeException if the API call fails
     */
    public PermissionDto getPermission(String key) {
        return permissionsManager.getPermission(key);
    }



    /**
     * Gets all organizations for the current user.
     * This method automatically handles pagination to retrieve all organizations.
     * 
     * @return List of organization DTOs
     * @throws RuntimeException if the API call fails
     */
    public List<OrganizationDto> getUserOrganizations() {
        return userInfoManager.getUserOrganizations();
    }

    /**
     * Gets the current user's profile.
     * 
     * @return The user profile DTO
     * @throws RuntimeException if the API call fails
     */
    public UserProfile getUserProfile() {
        return userInfoManager.getUserProfile();
    }

    /**
     * Gets the current organization.
     * 
     * @return The current organization DTO
     * @throws RuntimeException if the API call fails
     */
    public CurrentOrganizationResponse getCurrentOrganization() {
        return userInfoManager.getCurrentOrganization();
    }


} 