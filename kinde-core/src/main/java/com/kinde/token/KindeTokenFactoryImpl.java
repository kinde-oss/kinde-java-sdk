package com.kinde.token;

import com.google.inject.Inject;
import com.kinde.KindeTokenFactory;
import com.kinde.client.OidcMetaData;
import com.kinde.token.jwk.KindeJwkStore;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKMatcher;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class KindeTokenFactoryImpl implements KindeTokenFactory {

    private OidcMetaData oidcMetaData;
    private KindeJwkStore kindeJwkStore;

    @Inject
    public KindeTokenFactoryImpl(OidcMetaData oidcMetaData,KindeJwkStore kindeJwkStore) {
        this.oidcMetaData = oidcMetaData;
        this.kindeJwkStore = kindeJwkStore;
    }


    @SneakyThrows
    public KindeToken parse(String token) {
        SignedJWT signedJWT = SignedJWT.parse(token);

        JWKSelector jwkSelector = new JWKSelector(new JWKMatcher.Builder().keyID(signedJWT.getHeader().getKeyID()).build());
        JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(this.kindeJwkStore.publicKeys());
        List<JWK> jwks = jwkSource.get(jwkSelector, null);
        if (jwks.isEmpty()) {
            throw new IllegalStateException("No matching JWK found");
        }
        JWK jwk = jwks.get(0);
        boolean valid = false;
        if (jwk instanceof RSAKey) {
            RSAKey rsaKey = (RSAKey) jwk;
            RSASSAVerifier verifier = new RSASSAVerifier(rsaKey);
            valid = signedJWT.verify(verifier);
        }

        JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();

        if (claimsSet.getClaim("idp") != null || claimsSet.getStringClaim("nonce") != null) {
            return IDToken.init(token,valid);
        } else if (claimsSet.getStringClaim("refresh_token") != null) {
            return RefreshToken.init(token,valid);
        } else {
            return AccessToken.init(token,valid);
        }
    }

}
