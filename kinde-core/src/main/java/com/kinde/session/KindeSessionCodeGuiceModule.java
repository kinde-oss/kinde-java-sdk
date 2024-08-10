package com.kinde.session;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.kinde.KindeClientSession;
import com.kinde.guice.KindeAnnotations;
import com.kinde.token.KindeToken;

public class KindeSessionCodeGuiceModule extends AbstractModule {

    private String code;

    public KindeSessionCodeGuiceModule(String code) {
        this.code = code;
    }

    @Provides
    @KindeAnnotations.KindeCode
    public String provideKindeCode() {
        return code;
    }


    @Override
    protected void configure() {
        bind(KindeClientSession.class).to(KindeClientCodeSessionImpl.class);
    }
}
