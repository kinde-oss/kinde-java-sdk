package com.kinde.client;

import com.google.inject.AbstractModule;
import com.kinde.client.oidc.OidcMetaDataImpl;
import com.kinde.client.oidc.OidcMetaDataTestImpl;

public class KindeClientGuiceTestModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(OidcMetaData.class).to(OidcMetaDataTestImpl.class);
    }
}
