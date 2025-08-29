package com.kinde.token.jwt;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.SneakyThrows;

import java.util.*;

public class JwtGenerator {
    @SneakyThrows
    public static String generateAccessToken() {

        // generate a new signed token
        RSAKey rsaJWK = new RSAKeyGenerator(2048)
                .keyID("123")
                .generate();
        RSAKey rsaPublicJWK = rsaJWK.toPublicJWK();

        JWSSigner signer = new RSASSASigner(rsaJWK);

        Date now = new Date();

        JWTClaimsSet jwtClaims = new JWTClaimsSet.Builder()
                .issuer("https://openid.net")
                .subject("test")
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
        return signedJWT.serialize();
    }

    @SneakyThrows
    public static String generateAccessTokenWithPermissions(List<String> permissions) {
        RSAKey rsaJWK = new RSAKeyGenerator(2048)
                .keyID("123")
                .generate();

        JWSSigner signer = new RSASSASigner(rsaJWK);
        Date now = new Date();

        JWTClaimsSet jwtClaims = new JWTClaimsSet.Builder()
                .issuer("https://openid.net")
                .subject("test")
                .audience(Arrays.asList("https://kinde.com"))
                .expirationTime(new Date(now.getTime() + 1000*60*10))
                .notBeforeTime(now)
                .issueTime(now)
                .claim("permissions", permissions)
                .jwtID(UUID.randomUUID().toString())
                .build();

        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(rsaJWK.getKeyID()).build(),
                jwtClaims);

        signedJWT.sign(signer);
        return signedJWT.serialize();
    }

    @SneakyThrows
    public static String generateAccessTokenWithRoles(List<String> roles) {
        RSAKey rsaJWK = new RSAKeyGenerator(2048)
                .keyID("123")
                .generate();

        JWSSigner signer = new RSASSASigner(rsaJWK);
        Date now = new Date();

        JWTClaimsSet jwtClaims = new JWTClaimsSet.Builder()
                .issuer("https://openid.net")
                .subject("test")
                .audience(Arrays.asList("https://kinde.com"))
                .expirationTime(new Date(now.getTime() + 1000*60*10))
                .notBeforeTime(now)
                .issueTime(now)
                .claim("roles", roles)
                .jwtID(UUID.randomUUID().toString())
                .build();

        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(rsaJWK.getKeyID()).build(),
                jwtClaims);

        signedJWT.sign(signer);
        return signedJWT.serialize();
    }

    @SneakyThrows
    public static String generateAccessTokenWithFeatureFlags(String flagKey, Object flagValue) {
        RSAKey rsaJWK = new RSAKeyGenerator(2048)
                .keyID("123")
                .generate();

        JWSSigner signer = new RSASSASigner(rsaJWK);
        Date now = new Date();

        Map<String, Object> featureFlags = new HashMap<>();
        featureFlags.put(flagKey, flagValue);

        JWTClaimsSet jwtClaims = new JWTClaimsSet.Builder()
                .issuer("https://openid.net")
                .subject("test")
                .audience(Arrays.asList("https://kinde.com"))
                .expirationTime(new Date(now.getTime() + 1000*60*10))
                .notBeforeTime(now)
                .issueTime(now)
                .claim("feature_flags", featureFlags)
                .jwtID(UUID.randomUUID().toString())
                .build();

        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(rsaJWK.getKeyID()).build(),
                jwtClaims);

        signedJWT.sign(signer);
        return signedJWT.serialize();
    }

    @SneakyThrows
    public static String generateAccessTokenWithPermissionsAndRoles(List<String> permissions, List<String> roles) {
        RSAKey rsaJWK = new RSAKeyGenerator(2048)
                .keyID("123")
                .generate();

        JWSSigner signer = new RSASSASigner(rsaJWK);
        Date now = new Date();

        JWTClaimsSet jwtClaims = new JWTClaimsSet.Builder()
                .issuer("https://openid.net")
                .subject("test")
                .audience(Arrays.asList("https://kinde.com"))
                .expirationTime(new Date(now.getTime() + 1000*60*10))
                .notBeforeTime(now)
                .issueTime(now)
                .claim("permissions", permissions)
                .claim("roles", roles)
                .jwtID(UUID.randomUUID().toString())
                .build();

        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(rsaJWK.getKeyID()).build(),
                jwtClaims);

        signedJWT.sign(signer);
        return signedJWT.serialize();
    }

    @SneakyThrows
    public static String generateIDToken() {
        // generate a new signed token
        RSAKey rsaJWK = new RSAKeyGenerator(2048)
                .keyID("123")
                .generate();
        RSAKey rsaPublicJWK = rsaJWK.toPublicJWK();

        JWSSigner signer = new RSASSASigner(rsaJWK);

        Date now = new Date();

        Map<String,Object> featureFlags = new HashMap<>();

        featureFlags.put("test_str","test_str");
        featureFlags.put("test_integer",Integer.valueOf(1));
        featureFlags.put("test_boolean",Boolean.valueOf(false));

        JWTClaimsSet jwtClaims = new JWTClaimsSet.Builder()
                .issuer("https://openid.net")
                .subject("test")
                .audience(Arrays.asList("https://kinde.com"))
                .expirationTime(new Date(now.getTime() + 1000*60*10)) // expires in 10 minutes
                .notBeforeTime(now)
                .issueTime(now)
                .claim("permissions",Arrays.asList("test1","test1"))
                .claim("org_codes",Arrays.asList("test1","test1"))
                .claim("feature_flags",featureFlags)
                .jwtID(UUID.randomUUID().toString())
                .build();

        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(rsaJWK.getKeyID()).build(),
                jwtClaims);

        signedJWT.sign(signer);
        return signedJWT.serialize();
    }

    @SneakyThrows
    public static String refreshToken() {
        // generate a new signed token
        RSAKey rsaJWK = new RSAKeyGenerator(2048)
                .keyID("123")
                .generate();
        RSAKey rsaPublicJWK = rsaJWK.toPublicJWK();

        JWSSigner signer = new RSASSASigner(rsaJWK);

        Date now = new Date();

        JWTClaimsSet jwtClaims = new JWTClaimsSet.Builder()
                .issuer("https://openid.net")
                .subject("test")
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
        return signedJWT.serialize();
    }
}
