package com.kinde.client;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.kinde.KindeTokenFactory;
import com.kinde.KindeClient;
import com.kinde.KindeClientSession;
import com.kinde.config.KindeConfig;
import com.kinde.session.KindeSessionAccessTokenGuiceModule;
import com.kinde.session.KindeSessionGuiceModule;
import com.kinde.token.AccessToken;
import com.kinde.token.KindeTokenFactoryImpl;
import com.kinde.token.KindeToken;
import com.kinde.token.KindeTokenGuiceModule;

import java.security.InvalidParameterException;

public class KindeClientImpl implements KindeClient {

    private final Injector injector;
    private OidcMetaData oidcMetaData;
    private KindeConfig kindeConfig;

    @Inject
    public KindeClientImpl(
            Injector injector,
            OidcMetaData oidcMetaData,
            KindeConfig kindeConfig) {
        this.injector = injector;
        this.oidcMetaData = oidcMetaData;
        this.kindeConfig = kindeConfig;
    }

    @Override
    public KindeClientSession initClientSession(KindeToken accessToken) {
        if (!(accessToken instanceof AccessToken)) {
            throw new InvalidParameterException("Must pass in an AccessToken this is a : " + accessToken.getClass().getName());
        } else if (!accessToken.valid()) {
            throw new InvalidParameterException("The token is not valid");
        }
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
