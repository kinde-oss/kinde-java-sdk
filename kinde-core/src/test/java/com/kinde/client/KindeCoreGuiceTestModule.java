package com.kinde.client;

import com.kinde.client.oidc.OidcMetaDataTestImpl;
import com.kinde.token.BaseKindeClientGuiceTestModule;

/**
 * Kinde Core specific Guice test module.
 * Extends the base module and adds core-specific test bindings.
 */
public class KindeCoreGuiceTestModule extends BaseKindeClientGuiceTestModule {

    @Override
    protected void configureModuleSpecificBindings() {
        // Bind test implementation for OIDC metadata
        bind(OidcMetaData.class).to(OidcMetaDataTestImpl.class);
    }
}
