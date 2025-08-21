package test.java.com.kinde.token;

import com.google.inject.Inject;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;


import java.util.Arrays;
import java.util.Date;

public class TestTokenGeneratorImpl implements TestTokenGenerator {

    private final TestKeyGenerator testKeyGenerator;

    @Inject
    public TestTokenGeneratorImpl(TestKeyGenerator testKeyGenerator) {
        this.testKeyGenerator = testKeyGenerator;
    }

    @Override
    public String generateAccessToken() {
        try {
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject("test-user")
                    .issuer("http://localhost:8089")
                    .audience("test-audience")
                    .issueTime(new Date())
                    .expirationTime(new Date(System.currentTimeMillis() + 3600000)) // 1 hour
                    .claim("permissions", Arrays.asList("read:users", "write:users"))
                    .claim("roles", Arrays.asList("admin", "user"))
                    .build();

            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader.Builder(JWSAlgorithm.RS256)
                            .keyID(testKeyGenerator.getJWK().getKeyID())
                            .build(),
                    claimsSet
            );

            signedJWT.sign(new RSASSASigner(testKeyGenerator.getJWK().toPrivateKey()));
            System.out.println("The test token generator has been started");
            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException("Failed to generate access token", e);
        }
    }

    @Override
    public String generateIDToken() {
        try {
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject("test-user")
                    .issuer("http://localhost:8089")
                    .audience("test-client")
                    .issueTime(new Date())
                    .expirationTime(new Date(System.currentTimeMillis() + 3600000)) // 1 hour
                    .claim("email", "test@example.com")
                    .claim("name", "Test User")
                    .build();

            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader.Builder(JWSAlgorithm.RS256)
                            .keyID(testKeyGenerator.getJWK().getKeyID())
                            .build(),
                    claimsSet
            );

            signedJWT.sign(new RSASSASigner(testKeyGenerator.getJWK().toPrivateKey()));
            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException("Failed to generate ID token", e);
        }
    }

    @Override
    public String refreshToken() {
        try {
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject("test-user")
                    .issuer("http://localhost:8089")
                    .audience("test-client")
                    .issueTime(new Date())
                    .expirationTime(new Date(System.currentTimeMillis() + 86400000)) // 24 hours
                    .build();

            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader.Builder(JWSAlgorithm.RS256)
                            .keyID(testKeyGenerator.getJWK().getKeyID())
                            .build(),
                    claimsSet
            );

            signedJWT.sign(new RSASSASigner(testKeyGenerator.getJWK().toPrivateKey()));
            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException("Failed to generate refresh token", e);
        }
    }
}
