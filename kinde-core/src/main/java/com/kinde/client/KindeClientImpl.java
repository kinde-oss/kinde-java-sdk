package com.kinde.client;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.kinde.KindeTokenFactory;
import com.kinde.KindeClient;
import com.kinde.KindeClientSession;
import com.kinde.authorization.AuthorizationUrl;
import com.kinde.config.KindeConfig;
import com.kinde.session.KindeSessionCodeGuiceModule;
import com.kinde.session.KindeSessionKindeTokenGuiceModule;
import com.kinde.session.KindeSessionGuiceModule;
import com.kinde.token.*;

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
    public KindeClientSession initClientSession(String code, AuthorizationUrl authorizationUrl) {
        if (code == null || code.isEmpty()) {
            throw new InvalidParameterException("The code must be provided and cannot be left empty.");
        }
        return this.injector.createChildInjector(new KindeSessionCodeGuiceModule(code,authorizationUrl)).getInstance(KindeClientSession.class);
    }

    @Override
    public KindeClientSession initClientSession(KindeToken kindeToken) {
        validateToken(kindeToken);
        return this.injector.createChildInjector(new KindeSessionKindeTokenGuiceModule(kindeToken)).getInstance(KindeClientSession.class);
    }

    private void validateToken(KindeToken kindeToken) {
        if (!(kindeToken instanceof AccessToken) && !(kindeToken instanceof RefreshToken)) {
            throw new InvalidParameterException("Must pass in either an AccessToken or a RefreshToken this is a : " + kindeToken.getClass().getName());
        }
        if (!kindeToken.valid()) {
            throw new InvalidParameterException("The token is not valid");
        }
    }

    @Override
    public KindeClientSession clientSession() {
        return this.injector.createChildInjector(new KindeSessionGuiceModule()).getInstance(KindeClientSession.class);
    }

    @Override
    public KindeTokenFactory tokenFactory() {
        return this.injector.createChildInjector(new KindeTokenGuiceModule()).getInstance(KindeTokenFactory.class);
    }

    @Override
    public KindeConfig kindeConfig() {
        return this.kindeConfig;
    }

}
