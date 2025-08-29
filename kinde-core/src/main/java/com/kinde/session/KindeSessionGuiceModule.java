package com.kinde.session;

import com.google.inject.AbstractModule;
import com.kinde.KindeClientSession;
import com.kinde.entitlements.KindeEntitlementsGuiceModule;
import com.kinde.token.KindeTokenGuiceModule;


/**
 * Guice module for session functionality.
 * This module binds the KindeClientSession interface to its implementation
 * and includes the entitlements module for proper dependency injection.
 */
public class KindeSessionGuiceModule extends AbstractModule {

    @Override
    protected void configure() {
        // Bind the KindeClientSession interface to its implementation
        bind(KindeClientSession.class).to(KindeClientSessionImpl.class);
        
        // Install the token module to provide KindeJwkStore and KindeTokenFactory bindings
        install(new KindeTokenGuiceModule());
        
        // Install the entitlements module to ensure proper binding of entitlements components
        // The session will be injected by Guice when needed
        install(new KindeEntitlementsGuiceModule());
    }
}
