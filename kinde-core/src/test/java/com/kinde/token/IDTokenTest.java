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

public class IDTokenTest
        extends TestCase {

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public IDTokenTest(String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( IDTokenTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testIDToken() throws Exception {
        String token1 = JwtGenerator.generateIDToken();
        KindeToken kindeToken1 = IDToken.init(token1,false);
        assertTrue( kindeToken1.token().equals(token1) );
        assertTrue( kindeToken1.valid() == false );

        String token2 = JwtGenerator.generateIDToken();
        KindeToken kindeToken2 = IDToken.init(token2,true);
        assertTrue( kindeToken2.token().equals(token2) );
        assertTrue( kindeToken2.valid() );


        String tokenString = JwtGenerator.generateIDToken();
        System.out.println(tokenString);

        KindeToken kindeToken4 = IDToken.init(tokenString,true);

        System.out.println(kindeToken4.getPermissions());
    }
}
