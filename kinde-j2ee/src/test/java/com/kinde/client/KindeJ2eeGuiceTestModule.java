package com.kinde.client;

import com.kinde.client.oidc.OidcMetaDataTestImpl;
import com.kinde.token.BaseKindeClientGuiceTestModule;

/**
 * Kinde J2EE specific Guice test module.
 * Extends the base module and adds J2EE-specific test bindings.
 */
public class KindeJ2eeGuiceTestModule extends BaseKindeClientGuiceTestModule {

    @Override
    protected void configureModuleSpecificBindings() {
        // Bind test implementation for OIDC metadata
        bind(OidcMetaData.class).to(OidcMetaDataTestImpl.class);
    }
}
