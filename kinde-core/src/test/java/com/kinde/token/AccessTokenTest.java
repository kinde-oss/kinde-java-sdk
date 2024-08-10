package com.kinde.token;

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
        KindeToken kindeToken1 = AccessToken.init("test",false);
        assertTrue( kindeToken1.token().equals("test") );
        assertTrue( kindeToken1.valid() == false );

        KindeToken kindeToken2 = AccessToken.init("test2",true);
        assertTrue( kindeToken2.token().equals("test2") );
        assertTrue( kindeToken2.valid() );

        // generate a new signed token
        RSAKey rsaJWK = new RSAKeyGenerator(2048)
                .keyID("123")
                .generate();
        RSAKey rsaPublicJWK = rsaJWK.toPublicJWK();

        JWSSigner signer = new RSASSASigner(rsaJWK);

        Date now = new Date();

        JWTClaimsSet jwtClaims = new JWTClaimsSet.Builder()
                .issuer("https://openid.net")
                .subject("alice")
                .audience(Arrays.asList("https://kinde.com"))
                .expirationTime(new Date(now.getTime() + 1000*60*10)) // expires in 10 minutes
                .notBeforeTime(now)
                .issueTime(now)
                .claim("permissions",Arrays.asList("test1","test1"))
                .jwtID(UUID.randomUUID().toString())
                .build();

        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(rsaJWK.getKeyID()).build(),
                jwtClaims);

        signedJWT.sign(signer);
        String tokenString = signedJWT.serialize();

        System.out.println(tokenString);

        KindeToken kindeToken4 = AccessToken.init(tokenString,true);

        System.out.println(kindeToken4.getPermissions());
    }
}
