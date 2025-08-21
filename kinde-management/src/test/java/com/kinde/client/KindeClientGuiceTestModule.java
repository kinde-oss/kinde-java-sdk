package test.java.com.kinde.client;

import com.google.inject.AbstractModule;
import com.kinde.client.OidcMetaData;
import test.java.com.kinde.client.oidc.OidcMetaDataTestImpl;
import test.java.com.kinde.token.TestKeyGenerator;
import test.java.com.kinde.token.TestKeyGeneratorImpl;

public class KindeClientGuiceTestModule extends AbstractModule {
    @Override
    protected void configure() {
        // Bind test implementations for testing
        bind(OidcMetaData.class).to(OidcMetaDataTestImpl.class);
        bind(TestKeyGenerator.class).to(TestKeyGeneratorImpl.class);
    }
}
