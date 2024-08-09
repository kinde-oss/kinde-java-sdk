package com.kinde.client;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.kinde.KindeTokenFactory;
import com.kinde.KindeClient;
import com.kinde.KindeClientSession;
import com.kinde.session.KindeSessionGuiceModule;
import com.kinde.token.KindeTokenFactoryImpl;
import com.kinde.token.KindeToken;

public class KindeClientImpl implements KindeClient {

    private final Injector injector;

    @Inject
    public KindeClientImpl(Injector injector) {
        this.injector = injector;
        OidcMetaData oidcMetaData = this.injector.getInstance(OidcMetaData.class);
    }

    @Override
    public KindeClientSession initClientSession(KindeToken accessToken) {
        return this.injector.createChildInjector(new KindeSessionGuiceModule(accessToken)).getInstance(KindeClientSession.class);
    }

    @Override
    public KindeClientSession clientSession() {
        return this.injector.getInstance(KindeClientSession.class);
    }

    @Override
    public KindeTokenFactory tokenFactory() {
        return this.injector.getInstance(KindeTokenFactory.class);
    }

}
