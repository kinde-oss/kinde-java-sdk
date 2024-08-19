package com.kinde.token;

import com.kinde.token.jwt.JwtGenerator;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

public class AccessTokenTest
        extends TestCase {

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AccessTokenTest(String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AccessTokenTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testAccessToken() throws Exception {
        String tokenStr = JwtGenerator.generateAccessToken();
        KindeToken kindeToken1 = AccessToken.init(tokenStr,false);
        assertTrue( kindeToken1.token().equals(tokenStr) );
        assertTrue( kindeToken1.valid() == false );

        String tokenStr2 = JwtGenerator.generateAccessToken();
        KindeToken kindeToken2 = AccessToken.init(tokenStr2,true);
        assertTrue( kindeToken2.token().equals(tokenStr2) );
        assertTrue( kindeToken2.valid() );

        String tokenString = JwtGenerator.generateAccessToken();

        KindeToken kindeToken4 = AccessToken.init(tokenString,true);

        assertNotNull(tokenString);
        assertNotNull(kindeToken4.getPermissions());
    }
}
