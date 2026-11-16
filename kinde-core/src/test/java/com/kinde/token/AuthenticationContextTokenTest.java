package com.kinde.token;

import com.kinde.stepup.AcrValue;
import com.kinde.stepup.AuthenticationContext;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticationContextTokenTest {

    private static RSAKey key;

    @BeforeAll
    static void generateKey() throws Exception {
        key = new RSAKeyGenerator(2048).keyID("step-up").generate();
    }

    private static String jwt(String subject, String acr, List<String> amr, Long authTime) throws Exception {
        JWTClaimsSet.Builder claims = new JWTClaimsSet.Builder()
                .issuer("https://example.kinde.com")
                .subject(subject)
                .expirationTime(new Date(System.currentTimeMillis() + 600_000));
        if (acr != null) {
            claims.claim("acr", acr);
        }
        if (amr != null) {
            claims.claim("amr", amr);
        }
        if (authTime != null) {
            claims.claim("auth_time", authTime);
        }
        SignedJWT signed = new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(key.getKeyID()).build(), claims.build());
        signed.sign(new RSASSASigner(key));
        return signed.serialize();
    }

    @Test
    @DisplayName("tokens expose acr, amr and auth_time")
    void tokenAccessors() throws Exception {
        IDToken token = IDToken.init(jwt("kp_1", "mfa", List.of("pwd", "totp"), 1_716_800_000L), true);
        assertEquals("mfa", token.getAcr());
        assertEquals(List.of("pwd", "totp"), token.getAmr());
        assertEquals(1_716_800_000L, token.getAuthTime());
    }

    @Test
    @DisplayName("a single-factor token has no acr, an empty amr and may have no auth_time")
    void singleFactorToken() throws Exception {
        AccessToken token = AccessToken.init(jwt("kp_1", null, null, null), true);
        assertNull(token.getAcr());
        assertTrue(token.getAmr().isEmpty());
        assertNull(token.getAuthTime());
    }

    @Test
    @DisplayName("KindeTokens builds the context, preferring the ID token's auth_time")
    void kindeTokensContext() throws Exception {
        long recent = System.currentTimeMillis() / 1000 - 30;
        KindeTokens tokens = new KindeTokens(
                List.of("openid"),
                IDToken.init(jwt("kp_1", "phr", List.of("passkey"), recent), true),
                AccessToken.init(jwt("kp_1", null, null, 5L), true),
                null);

        AuthenticationContext context = tokens.getAuthenticationContext();
        assertEquals(Optional.of(AcrValue.PHR), context.getAcr());
        assertEquals(Optional.of(recent), context.getAuthTime());
        assertTrue(context.satisfies(AcrValue.MFA, 300));
        assertFalse(tokens.getAuthenticationContext(false).satisfies(AcrValue.MFA, 300));
    }

    @Test
    @DisplayName("KindeTokens without an ID token falls back to the access token")
    void accessTokenOnly() throws Exception {
        KindeTokens tokens = new KindeTokens(null, null,
                AccessToken.init(jwt("kp_1", "mfa", null, null), true), null);
        assertTrue(tokens.getAuthenticationContext().satisfies(AcrValue.MFA, null));
        assertFalse(tokens.getAuthenticationContext().satisfies(AcrValue.PHR, null));
        assertFalse(new KindeTokens(null, null, null, null).getAuthenticationContext().satisfies(AcrValue.MFA, null));
    }
}
