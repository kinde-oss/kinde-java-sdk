package com.kinde.client;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.kinde.KindeTokenFactory;
import com.kinde.KindeClient;
import com.kinde.KindeClientSession;
import com.kinde.session.KindeSessionAccessTokenGuiceModule;
import com.kinde.session.KindeSessionGuiceModule;
import com.kinde.token.KindeTokenFactoryImpl;
import com.kinde.token.KindeToken;
import com.kinde.token.KindeTokenGuiceModule;

public class KindeClientImpl implements KindeClient {

    private final Injector injector;
    private OidcMetaData oidcMetaData;

    @Inject
    public KindeClientImpl(Injector injector,OidcMetaData oidcMetaData) {
        this.injector = injector;
        this.oidcMetaData = oidcMetaData;
    }

    @Override
    public KindeClientSession initClientSession(KindeToken accessToken) {
        return this.injector.createChildInjector(new KindeSessionAccessTokenGuiceModule(accessToken)).getInstance(KindeClientSession.class);
    }

    @Override
    public KindeClientSession clientSession() {
        return this.injector.createChildInjector(new KindeSessionGuiceModule()).getInstance(KindeClientSession.class);
    }

    @Override
    public KindeTokenFactory tokenFactory() {
        return this.injector.createChildInjector(new KindeTokenGuiceModule()).getInstance(KindeTokenFactory.class);
    }

}
