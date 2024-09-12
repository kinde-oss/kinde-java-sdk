package com.kinde.session;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.kinde.KindeClientSession;
import com.kinde.guice.KindeAnnotations;
import com.kinde.token.KindeToken;

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
        bind(KindeClientSession.class).to(KindeClientKindeTokenSessionImpl.class);
    }
}
