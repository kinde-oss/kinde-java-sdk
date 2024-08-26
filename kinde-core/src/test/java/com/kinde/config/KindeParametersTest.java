package com.kinde.config;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class KindeParametersTest{

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
    @Test
    public void testKindParametersDomainTest() {
        String value1 = KindeParameters.DOMAIN.getValue();
        String value2 = KindeParameters.DOMAIN.getValue();
        assertTrue( value1.equals(value2) );
        String value3 = KindeParameters.fromValue("KINDE_DOMAIN").getValue();
        assertTrue( value1.equals(value3) );
    }

    @Test
    public void testKindParametersRedirectUriTest() {
        String value1 = KindeParameters.REDIRECT_URI.getValue();
        String value2 = KindeParameters.REDIRECT_URI.getValue();
        assertTrue( value1.equals(value2) );
        String value3 = KindeParameters.fromValue("KINDE_REDIRECT_URI").getValue();
        assertTrue( value1.equals(value3) );
    }

    @Test
    public void testKindParametersLogoutRedirectUriTest() {
        String value1 = KindeParameters.LOGOUT_REDIRECT_URI.getValue();
        String value2 = KindeParameters.LOGOUT_REDIRECT_URI.getValue();
        assertTrue( value1.equals(value2) );
        String value3 = KindeParameters.fromValue("KINDE_LOGOUT_REDIRECT_URI").getValue();
        assertTrue( value1.equals(value3) );
    }

    @Test
    public void testKindParametersOpenidEndpointTest() {
        String value1 = KindeParameters.OPENID_ENDPOINT.getValue();
        String value2 = KindeParameters.OPENID_ENDPOINT.getValue();
        assertTrue( value1.equals(value2) );
        String value3 = KindeParameters.fromValue("KINDE_OPENID_ENDPOINT").getValue();
        assertTrue( value1.equals(value3) );
    }

    @Test
    public void testKindParametersTokenEndpointTest() {
        String value1 = KindeParameters.TOKEN_ENDPOINT.getValue();
        String value2 = KindeParameters.TOKEN_ENDPOINT.getValue();
        assertTrue( value1.equals(value2) );
        String value3 = KindeParameters.fromValue("KINDE_TOKEN_ENDPOINT").getValue();
        assertTrue( value1.equals(value3) );
    }

    @Test
    public void testKindParametersLogoutEndpointTest() {
        String value1 = KindeParameters.LOGOUT_ENDPOINT.getValue();
        String value2 = KindeParameters.LOGOUT_ENDPOINT.getValue();
        assertTrue( value1.equals(value2) );
        String value3 = KindeParameters.fromValue("KINDE_LOGOUT_ENDPOINT").getValue();
        assertTrue( value1.equals(value3) );
    }

    @Test
    public void testKindParametersClientIdTest() {
        String value1 = KindeParameters.CLIENT_ID.getValue();
        String value2 = KindeParameters.CLIENT_ID.getValue();
        assertTrue( value1.equals(value2) );
        String value3 = KindeParameters.fromValue("KINDE_CLIENT_ID").getValue();
        assertTrue( value1.equals(value3) );
    }

    @Test
    public void testKindParametersClientSecretTest() {
        String value1 = KindeParameters.CLIENT_SECRET.getValue();
        String value2 = KindeParameters.CLIENT_SECRET.getValue();
        assertTrue( value1.equals(value2) );
        String value3 = KindeParameters.fromValue("KINDE_CLIENT_SECRET").getValue();
        assertTrue( value1.equals(value3) );
    }

    @Test
    public void testKindParametersGrantTypeTest() {
        String value1 = KindeParameters.GRANT_TYPE.getValue();
        String value2 = KindeParameters.GRANT_TYPE.getValue();
        assertTrue( value1.equals(value2) );
        String value3 = KindeParameters.fromValue("KINDE_GRANT_TYPE").getValue();
        assertTrue( value1.equals(value3) );
    }

    @Test
    public void testKindParametersScopesTest() {
        String value1 = KindeParameters.SCOPES.getValue();
        String value2 = KindeParameters.SCOPES.getValue();
        assertTrue( value1.equals(value2) );
        String value3 = KindeParameters.fromValue("KINDE_SCOPES").getValue();
        assertTrue( value1.equals(value3) );
    }

    @Test
    public void testKindParametersProtocolTest() {
        String value1 = KindeParameters.PROTOCOL.getValue();
        String value2 = KindeParameters.PROTOCOL.getValue();
        assertTrue( value1.equals(value2) );
        String value3 = KindeParameters.fromValue("KINDE_PROTOCOL").getValue();
        assertTrue( value1.equals(value3) );
    }

    @Test
    public void testKindParametersInvalidTypeTest() {
        try {
            KindeParameters.fromValue("INVALID_VALUE").getValue();
            fail("Expected an exception");
        } catch (IllegalArgumentException ex) {
            Assert.assertNotNull(ex);
        }
    }

}
