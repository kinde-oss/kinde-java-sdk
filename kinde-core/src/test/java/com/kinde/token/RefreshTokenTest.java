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

public class RefreshTokenTest
        extends TestCase {

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public RefreshTokenTest(String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( RefreshTokenTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testRefreshToken() throws Exception {
        String token1 = JwtGenerator.refreshToken();
        KindeToken kindeToken1 = RefreshToken.init(token1,false);
        assertTrue( kindeToken1.token().equals(token1) );
        assertTrue( kindeToken1.valid() == false );

        String token2 = JwtGenerator.refreshToken();
        KindeToken kindeToken2 = RefreshToken.init(token2,true);
        assertTrue( kindeToken2.token().equals(token2) );
        assertTrue( kindeToken2.valid() );

        String tokenString = JwtGenerator.refreshToken();

        KindeToken kindeToken4 = RefreshToken.init(tokenString,true);

        assertNotNull(tokenString);
        assertNotNull(kindeToken4.getPermissions());
    }
}
