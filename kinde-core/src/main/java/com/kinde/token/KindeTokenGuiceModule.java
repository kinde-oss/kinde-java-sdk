package com.kinde.token;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.kinde.KindeClientSession;
import com.kinde.KindeTokenFactory;
import com.kinde.token.jwk.KindeJwkStore;
import com.kinde.token.jwk.KindeJwkStoreImpl;
import com.kinde.KindeTokenFactory;
import com.kinde.token.KindeTokenFactoryImpl;

public class KindeTokenGuiceModule extends AbstractModule {

    /**
     * Creates a new KindeTokenGuiceModule.
     * The session will be injected by Guice when needed.
     */
    public KindeTokenGuiceModule() {
        // No parameters needed - session will be injected by Guice
    }

    @Override
    protected void configure() {
        // Bind token-related classes
        bind(KindeJwkStore.class).to(KindeJwkStoreImpl.class);
        bind(KindeTokenFactory.class).to(KindeTokenFactoryImpl.class);
    }


}
