package com.kinde.client;

import com.kinde.client.oidc.OidcMetaDataTestImpl;
import com.kinde.token.BaseKindeClientGuiceTestModule;

/**
 * Kinde Management specific Guice test module.
 * Extends the base module and adds management-specific test bindings.
 */
public class KindeManagementGuiceTestModule extends BaseKindeClientGuiceTestModule {

    @Override
    protected void configureModuleSpecificBindings() {
        // Bind test implementation for OIDC metadata
        bind(OidcMetaData.class).to(OidcMetaDataTestImpl.class);
    }
}
