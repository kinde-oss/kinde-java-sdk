package com.kinde.client.oidc;

import com.kinde.KindeClient;
import com.kinde.KindeClientSession;
import com.kinde.KindeTokenFactory;
import com.kinde.client.KindeClientGuiceModule;
import com.kinde.client.KindeClientGuiceTestModule;
import com.kinde.config.KindeConfigImpl;
import com.kinde.config.KindeParameters;
import com.kinde.guice.KindeEnvironmentSingleton;
import com.kinde.guice.KindeGuiceSingleton;
import com.kinde.token.AccessToken;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.HashMap;
import java.util.Map;

public class OidcMetaDataImplTest
        extends TestCase {

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public OidcMetaDataImplTest(String testName )
    {
        super( testName );
        KindeGuiceSingleton.getInstance(new KindeClientGuiceTestModule());
        KindeEnvironmentSingleton.init(KindeEnvironmentSingleton.State.TEST);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( OidcMetaDataImplTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testOidcMetaDataImplTest() throws Exception {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(KindeParameters.DOMAIN.getValue(),"https://burntjam.kinde.com");
        KindeConfigImpl kindeConfig = new KindeConfigImpl(parameters);

        OidcMetaDataImpl oidcMetaData = new OidcMetaDataImpl(kindeConfig);
        assertTrue(oidcMetaData.getOpMetadata()!= null);
        assertTrue( oidcMetaData.getJwkUrl() != null );
    }
}
