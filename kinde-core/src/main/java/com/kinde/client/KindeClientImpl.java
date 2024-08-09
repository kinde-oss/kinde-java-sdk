package com.kinde.client;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.kinde.KindTokenFactory;
import com.kinde.KindeClient;
import com.kinde.KindeClientSession;
import com.kinde.session.KindeClientSessionImpl;
import com.kinde.token.KindTokenFactoryImpl;
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
        return this.injector.getInstance(KindeClientSession.class);
    }

    @Override
    public KindeClientSession clientSession() {
        return new KindeClientSessionImpl(this);
    }

    @Override
    public KindTokenFactory tokenFactory() {
        return new KindTokenFactoryImpl(this);
    }

}
