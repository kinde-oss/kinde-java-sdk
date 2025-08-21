package com.kinde.accounts;

import com.google.inject.AbstractModule;
import com.kinde.KindeClientSession;
import com.kinde.accounts.manager.EntitlementsManager;
import com.kinde.accounts.manager.FeatureFlagsManager;
import com.kinde.accounts.manager.PermissionsManager;
import com.kinde.accounts.manager.RolesManager;
import com.kinde.accounts.manager.UserInfoManager;
import com.kinde.accounts.manager.impl.EntitlementsManagerImpl;
import com.kinde.accounts.manager.impl.FeatureFlagsManagerImpl;
import com.kinde.accounts.manager.impl.PermissionsManagerImpl;
import com.kinde.accounts.manager.impl.RolesManagerImpl;
import com.kinde.accounts.manager.impl.UserInfoManagerImpl;
import com.kinde.accounts.util.ApiResponseHandler;
import com.kinde.accounts.util.PaginationHelper;
import com.kinde.accounts.util.ApiResponseHandlerImpl;
import com.kinde.accounts.util.PaginationHelperImpl;
import org.openapitools.client.api.DefaultApi;

/**
 * Guice module for configuring dependency injection for the Kinde Accounts client.
 * This module binds all interfaces to their implementations and provides the necessary
 * dependencies for the refactored KindeAccountsClient.
 */
public class KindeAccountsModule extends AbstractModule {
    
    @Override
    protected void configure() {
        // Bind managers to their implementations
        bind(EntitlementsManager.class).to(EntitlementsManagerImpl.class);
        bind(PermissionsManager.class).to(PermissionsManagerImpl.class);
        bind(RolesManager.class).to(RolesManagerImpl.class);
        bind(FeatureFlagsManager.class).to(FeatureFlagsManagerImpl.class);
        bind(UserInfoManager.class).to(UserInfoManagerImpl.class);
        
        // Bind shared utilities
        bind(PaginationHelper.class).to(PaginationHelperImpl.class);
        bind(ApiResponseHandler.class).to(ApiResponseHandlerImpl.class);
    }
    
    /**
     * Provides a DefaultApi instance.
     * The API client will be configured with authentication when used.
     * 
     * @return A DefaultApi instance
     */
    @com.google.inject.Provides
    public DefaultApi provideDefaultApi() {
        return new DefaultApi();
    }
}
