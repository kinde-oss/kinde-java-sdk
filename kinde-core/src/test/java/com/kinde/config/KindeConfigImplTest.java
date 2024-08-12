package com.kinde.config;

import com.kinde.authorization.AuthorizationType;
import com.kinde.token.AccessToken;
import com.kinde.token.KindeToken;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KindeConfigImplTest
        extends TestCase {

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public KindeConfigImplTest(String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( KindeConfigImplTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testKindeConfig() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(KindeParameters.DOMAIN.getValue(),"test-domain");
        parameters.put(KindeParameters.REDIRECT_URI.getValue(),"test-redirect-uri");
        parameters.put(KindeParameters.LOGOUT_REDIRECT_URI.getValue(),"test-logout-redirect-uri");
        parameters.put(KindeParameters.OPENID_ENDPOINT.getValue(),"test-openid-endpoint");
        parameters.put(KindeParameters.AUTHORIZATION_ENDPOINT.getValue(),"test-authorization-endpoint");
        parameters.put(KindeParameters.TOKEN_ENDPOINT.getValue(),"test-token-endpoint");
        parameters.put(KindeParameters.LOGOUT_ENDPOINT.getValue(),"test-logout-endpoint");
        parameters.put(KindeParameters.CLIENT_ID.getValue(),"test-client-id");
        parameters.put(KindeParameters.CLIENT_SECRET.getValue(),"test-client-secret");
        parameters.put(KindeParameters.GRANT_TYPE.getValue(), AuthorizationType.CODE);
        parameters.put(KindeParameters.SCOPES.getValue(), Arrays.asList("test-grant-types1","test-grant-types2"));
        parameters.put(KindeParameters.PROTOCOL.getValue(),"test-protocol");
        parameters.put("long value",new Long(10));
        KindeConfigImpl kindeConfig = new KindeConfigImpl(parameters);

        assertTrue( kindeConfig.domain().equals("test-domain") );
        assertTrue( kindeConfig.redirectUri().equals("test-redirect-uri") );
        assertTrue( kindeConfig.logoutRedirectUri().equals("test-logout-redirect-uri") );
        assertTrue( kindeConfig.openidEndpoint().equals("test-openid-endpoint") );
        assertTrue( kindeConfig.authorizationEndpoint().equals("test-authorization-endpoint") );
        assertTrue( kindeConfig.tokenEndpoint().equals("test-token-endpoint") );
        assertTrue( kindeConfig.logoutEndpoint().equals("test-logout-endpoint") );
        assertTrue( kindeConfig.clientId().equals("test-client-id") );
        assertTrue( kindeConfig.clientSecret().equals("test-client-secret") );
        assertTrue( kindeConfig.grantType() == AuthorizationType.CODE );
        assertTrue( kindeConfig.scopes().equals(Arrays.asList("test-grant-types1","test-grant-types2")) );
        assertTrue( kindeConfig.protocol().equals("test-protocol") );

        assertTrue(kindeConfig.getLongValue("long value") == 10);
        assertTrue(kindeConfig.getStringValue(KindeParameters.DOMAIN.getValue()).equals("test-domain"));
        assertTrue(kindeConfig.getValue(KindeParameters.DOMAIN.getValue()).equals("test-domain"));
    }
}
