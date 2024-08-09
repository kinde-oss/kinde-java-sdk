package com.kinde.client;

import com.google.inject.AbstractModule;
import com.kinde.client.oidc.OidcMetaDataImpl;
import com.kinde.client.oidc.OidcMetaDataTestImpl;
import com.kinde.token.TestKeyGenerator;
import com.kinde.token.TestKeyGeneratorImpl;

public class KindeClientGuiceTestModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(TestKeyGenerator.class).to(TestKeyGeneratorImpl.class);
        bind(OidcMetaData.class).to(OidcMetaDataTestImpl.class);
    }
}
