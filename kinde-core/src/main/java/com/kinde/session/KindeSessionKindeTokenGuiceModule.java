package com.kinde.session;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.kinde.KindeClientSession;
import com.kinde.entitlements.KindeEntitlementsGuiceModule;
import com.kinde.guice.KindeAnnotations;
import com.kinde.token.KindeToken;
import com.kinde.token.KindeTokenGuiceModule;


/**
 * Guice module for session functionality with KindeToken.
 * This module binds the KindeClientSession interface to its token-based implementation
 * and includes the entitlements module for proper dependency injection.
 */
public class KindeSessionKindeTokenGuiceModule extends AbstractModule {

    private KindeToken kindeToken;

    public KindeSessionKindeTokenGuiceModule(KindeToken kindeToken) {
        if (kindeToken == null) {
            throw new IllegalArgumentException("kindeToken cannot be null");
        }
        this.kindeToken = kindeToken;
    }

    @Provides
    @KindeAnnotations.KindeToken
    public KindeToken provideKindeToken() {
        return kindeToken;
    }

    @Override
    protected void configure() {
        // Install the token module to provide KindeJwkStore and KindeTokenFactory bindings
        install(new KindeTokenGuiceModule());
        
        // Install the entitlements module to ensure proper binding of entitlements components
        install(new KindeEntitlementsGuiceModule());
        
        // Bind the KindeClientSession interface to its token-based implementation
        bind(KindeClientSession.class).to(KindeClientKindeTokenSessionImpl.class);
    }
}
