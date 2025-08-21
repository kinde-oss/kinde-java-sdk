package com.kinde.session;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.kinde.KindeClientSession;
import com.kinde.authorization.AuthorizationUrl;
import com.kinde.entitlements.KindeEntitlementsGuiceModule;
import com.kinde.guice.KindeAnnotations;
import com.kinde.token.KindeToken;
import com.kinde.token.KindeTokenGuiceModule;


/**
 * Guice module for session functionality with authorization code.
 * This module binds the KindeClientSession interface to its code-based implementation
 * and includes the entitlements module for proper dependency injection.
 */
public class KindeSessionCodeGuiceModule extends AbstractModule {

    private String code;
    private AuthorizationUrl authorizationUrl;

    public KindeSessionCodeGuiceModule(String code, AuthorizationUrl authorizationUrl) {
        this.code = code;
        this.authorizationUrl = authorizationUrl;
    }

    @Provides
    @KindeAnnotations.KindeCode
    public String provideKindeCode() {
        return code;
    }

    @Provides
    @KindeAnnotations.AuthorizationUrl
    public AuthorizationUrl provideAuthorizationUrl() {
        return authorizationUrl;
    }

    @Override
    protected void configure() {
        // Install the token module to provide KindeJwkStore and KindeTokenFactory bindings
        install(new KindeTokenGuiceModule());
        
        // Install the entitlements module to ensure proper binding of entitlements components
        install(new KindeEntitlementsGuiceModule());
        
        // Bind the KindeClientSession interface to its code-based implementation
        bind(KindeClientSession.class).to(KindeClientCodeSessionImpl.class);
    }
}
