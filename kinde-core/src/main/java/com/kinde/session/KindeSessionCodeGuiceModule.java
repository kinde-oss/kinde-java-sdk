package com.kinde.session;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.kinde.KindeClientSession;
import com.kinde.authorization.AuthorizationUrl;
import com.kinde.guice.KindeAnnotations;
import com.kinde.token.KindeToken;

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
        bind(KindeClientSession.class).to(KindeClientCodeSessionImpl.class);
    }
}
