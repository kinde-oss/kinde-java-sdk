package com.kinde.session;

import com.google.inject.AbstractModule;
import com.kinde.KindeClientSession;

public class KindeSessionGuiceModule  extends AbstractModule {

    @Override
    protected void configure() {
        bind(KindeClientSession.class).to(KindeClientSessionImpl.class);
    }
}
