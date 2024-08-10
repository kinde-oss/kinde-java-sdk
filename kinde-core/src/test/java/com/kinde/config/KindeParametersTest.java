package com.kinde.config;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class KindeParametersTest
        extends TestCase {

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public KindeParametersTest(String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( KindeParametersTest.class );
    }

    /*
    Test all of the below parameters
    ================================
    DOMAIN("DOMAIN"),
    REDIRECT_URI("REDIRECT_URI"),
    LOGOUT_REDIRECT_URI("LOGOUT_REDIRECT_URI"),
    OPENID_ENDPOINT("OPENID_ENDPOINT"),
    AUTHORIZATION_ENDPOINT("AUTHORIZATION_ENDPOINT"),
    TOKEN_ENDPOINT("TOKEN_ENDPOINT"),
    LOGOUT_ENDPOINT("LOGOUT_ENDPOINT"),
    CLIENT_ID("CLIENT_ID"),
    CLIENT_SECRET("CLIENT_SECRET"),
    GRANT_TYPE("GRANT_TYPE"),
    SCOPES("SCOPES"),
    PROTOCOL("PROTOCOL")
     */

    /**
     * Rigourous Test :-)
     */
    public void testKindParametersDomain() {
        String value1 = KindeParameters.DOMAIN.getValue();
        String value2 = KindeParameters.DOMAIN.getValue();
        assertTrue( value1.equals(value2) );
        String value3 = KindeParameters.fromValue("DOMAIN").getValue();
        assertTrue( value1.equals(value3) );
    }

    public void testKindParametersRedirectUri() {
        String value1 = KindeParameters.REDIRECT_URI.getValue();
        String value2 = KindeParameters.REDIRECT_URI.getValue();
        assertTrue( value1.equals(value2) );
        String value3 = KindeParameters.fromValue("REDIRECT_URI").getValue();
        assertTrue( value1.equals(value3) );
    }

    public void testKindParametersLogoutRedirectUri() {
        String value1 = KindeParameters.LOGOUT_REDIRECT_URI.getValue();
        String value2 = KindeParameters.LOGOUT_REDIRECT_URI.getValue();
        assertTrue( value1.equals(value2) );
        String value3 = KindeParameters.fromValue("LOGOUT_REDIRECT_URI").getValue();
        assertTrue( value1.equals(value3) );
    }

    public void testKindParametersOpenidEndpoint() {
        String value1 = KindeParameters.OPENID_ENDPOINT.getValue();
        String value2 = KindeParameters.OPENID_ENDPOINT.getValue();
        assertTrue( value1.equals(value2) );
        String value3 = KindeParameters.fromValue("OPENID_ENDPOINT").getValue();
        assertTrue( value1.equals(value3) );
    }

    public void testKindParametersTokenEndpoint() {
        String value1 = KindeParameters.TOKEN_ENDPOINT.getValue();
        String value2 = KindeParameters.TOKEN_ENDPOINT.getValue();
        assertTrue( value1.equals(value2) );
        String value3 = KindeParameters.fromValue("TOKEN_ENDPOINT").getValue();
        assertTrue( value1.equals(value3) );
    }

    public void testKindParametersLogoutEndpoint() {
        String value1 = KindeParameters.LOGOUT_ENDPOINT.getValue();
        String value2 = KindeParameters.LOGOUT_ENDPOINT.getValue();
        assertTrue( value1.equals(value2) );
        String value3 = KindeParameters.fromValue("LOGOUT_ENDPOINT").getValue();
        assertTrue( value1.equals(value3) );
    }

    public void testKindParametersClientId() {
        String value1 = KindeParameters.CLIENT_ID.getValue();
        String value2 = KindeParameters.CLIENT_ID.getValue();
        assertTrue( value1.equals(value2) );
        String value3 = KindeParameters.fromValue("CLIENT_ID").getValue();
        assertTrue( value1.equals(value3) );
    }

    public void testKindParametersClientSecret() {
        String value1 = KindeParameters.CLIENT_SECRET.getValue();
        String value2 = KindeParameters.CLIENT_SECRET.getValue();
        assertTrue( value1.equals(value2) );
        String value3 = KindeParameters.fromValue("CLIENT_SECRET").getValue();
        assertTrue( value1.equals(value3) );
    }

    public void testKindParametersGrantType() {
        String value1 = KindeParameters.GRANT_TYPE.getValue();
        String value2 = KindeParameters.GRANT_TYPE.getValue();
        assertTrue( value1.equals(value2) );
        String value3 = KindeParameters.fromValue("GRANT_TYPE").getValue();
        assertTrue( value1.equals(value3) );
    }

    public void testKindParametersScopes() {
        String value1 = KindeParameters.SCOPES.getValue();
        String value2 = KindeParameters.SCOPES.getValue();
        assertTrue( value1.equals(value2) );
        String value3 = KindeParameters.fromValue("SCOPES").getValue();
        assertTrue( value1.equals(value3) );
    }

    public void testKindParametersProtocol() {
        String value1 = KindeParameters.PROTOCOL.getValue();
        String value2 = KindeParameters.PROTOCOL.getValue();
        assertTrue( value1.equals(value2) );
        String value3 = KindeParameters.fromValue("PROTOCOL").getValue();
        assertTrue( value1.equals(value3) );
    }

}
