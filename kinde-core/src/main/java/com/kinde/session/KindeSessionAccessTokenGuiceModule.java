package com.kinde.session;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.kinde.KindeClientSession;
import com.kinde.guice.KindeAnnotations;
import com.kinde.token.KindeToken;

public class KindeSessionAccessTokenGuiceModule extends AbstractModule {

    private KindeToken accessToken;

    public KindeSessionAccessTokenGuiceModule(KindeToken accessToken) {
        this.accessToken = accessToken;
    }

    @Provides
    @KindeAnnotations.AccessToken
    public KindeToken provideAccessToken() {
        return accessToken;
    }


    @Override
    protected void configure() {
        bind(KindeClientSession.class).to(KindeClientAccessTokenSessionImpl.class);
    }
}
