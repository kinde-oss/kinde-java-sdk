package com.kinde.token;

import com.google.inject.Inject;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.SneakyThrows;

import java.util.Date;

public class TestTokenGeneratorImpl implements TestTokenGenerator {

    private TestKeyGenerator testKeyGenerator;

    @Inject
    public TestTokenGeneratorImpl(TestKeyGenerator testKeyGenerator) {
        this.testKeyGenerator = testKeyGenerator;
        System.out.println("The test token generator");
    }

    @Override
    @SneakyThrows
    public String generateAccessToken() {
        RSAKey rsaJWK = (RSAKey)this.testKeyGenerator.getJWK();
        JWSSigner signer = new RSASSASigner(rsaJWK);

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject("access_token")
                .issuer("https://kinde.com")
                .expirationTime(new Date(new Date().getTime() + 60 * 1000))
                .build();

        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(rsaJWK.getKeyID()).build(),
                claimsSet);

        signedJWT.sign(signer);

        return signedJWT.serialize();
    }

    @Override
    @SneakyThrows
    public String generateIdToken() {
        RSAKey rsaJWK = (RSAKey)this.testKeyGenerator.getJWK();
        JWSSigner signer = new RSASSASigner(rsaJWK);

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject("id_token")
                .issuer("https://kinde.com")
                .claim("idp","idp")
                .claim("nonce","nonce")
                .expirationTime(new Date(new Date().getTime() + 60 * 1000))
                .build();

        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(rsaJWK.getKeyID()).build(),
                claimsSet);

        signedJWT.sign(signer);

        return signedJWT.serialize();
    }

    @Override
    @SneakyThrows
    public String generateRefreshToken() {
        RSAKey rsaJWK = (RSAKey)this.testKeyGenerator.getJWK();
        JWSSigner signer = new RSASSASigner(rsaJWK);

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject("refresh_token")
                .issuer("https://kinde.com")
                .claim("refresh_token","refresh_token")
                .expirationTime(new Date(new Date().getTime() + 60 * 1000))
                .build();

        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(rsaJWK.getKeyID()).build(),
                claimsSet);

        signedJWT.sign(signer);

        return signedJWT.serialize();
    }
}
