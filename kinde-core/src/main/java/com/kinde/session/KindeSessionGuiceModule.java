package com.kinde.session;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.kinde.KindeClientSession;
import com.kinde.KindeParameters;
import com.kinde.guice.KindeAnnotations;
import com.kinde.token.KindeToken;

public class KindeSessionGuiceModule  extends AbstractModule {

    @Override
    protected void configure() {
        bind(KindeClientSession.class).to(KindeClientSessionImpl.class);
    }
}
