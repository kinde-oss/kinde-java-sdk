package com.kinde.entitlements;

import com.google.inject.AbstractModule;
import com.kinde.accounts.KindeAccountsClient;
import com.kinde.accounts.KindeAccountsModule;
import com.kinde.KindeClientSession;

/**
 * Guice module for entitlements and accounts functionality.
 * This module binds the entitlements interface to its implementation
 * and configures the accounts client for dependency injection.
 */
public class KindeEntitlementsGuiceModule extends AbstractModule {

    @Override
    protected void configure() {
        // Bind the KindeEntitlements interface to its implementation
        bind(KindeEntitlements.class).to(KindeEntitlementsImpl.class);
        
        // Install the KindeAccountsModule to provide all the manager bindings
        // The session will be injected by Guice when the module is created
        install(new KindeAccountsModule());
        
        // The KindeAccountsClient is already configured with @Inject annotation
        // so it will be automatically bound by Guice
    }
}
