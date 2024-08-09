package com.kinde.token;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.kinde.KindeClientSession;
import com.kinde.KindeTokenFactory;
import com.kinde.guice.KindeAnnotations;
import com.kinde.session.KindeClientSessionImpl;

public class KindeTokenGuiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(KindeTokenFactory.class).to(KindeTokenFactoryImpl.class);
    }
}
