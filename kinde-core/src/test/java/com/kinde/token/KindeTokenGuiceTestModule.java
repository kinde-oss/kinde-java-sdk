package com.kinde.token;

import com.google.inject.AbstractModule;
import com.kinde.client.OidcMetaData;
import com.kinde.client.oidc.OidcMetaDataTestImpl;

public class KindeTokenGuiceTestModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(TestTokenGenerator.class).to(TestTokenGeneratorImpl.class);
    }
}
