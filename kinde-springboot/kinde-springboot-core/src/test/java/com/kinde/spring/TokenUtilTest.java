package com.kinde.spring;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class TokenUtilTest {

    private static final String ISSUER = "https://example.kinde.com";

    @Test
    public void testTokenClaimsToAuthorities() throws Exception {
        TokenUtil.tokenClaimsToAuthorities(Map.of(),"String");
    }

    @Test
    public void testScopesToAuthorities() throws Exception {
        TokenUtil.tokenScopesToAuthorities(null);


        OAuth2AccessToken oAuth2AccessToken = Mockito.mock(OAuth2AccessToken.class);
        when(oAuth2AccessToken.getScopes())
                .thenReturn(Set.of("profile","openid"));

        TokenUtil.tokenScopesToAuthorities(oAuth2AccessToken);
    }

    @Test
    public void testJwtValidator() throws Exception {
        TokenUtil.jwtValidator("Kinde","test.com");
    }

    /**
     * When no audience is configured (null), the validator should NOT enforce an audience claim.
     * This matches the Kinde default: tokens issued for clients without a configured API resource
     * carry an empty {@code aud} array. Regression coverage for the prior {@code "api://default"}
     * default that rejected every real Kinde token.
     */
    @Test
    public void testJwtValidatorWithNullAudienceSkipsAudienceCheck() {
        OAuth2TokenValidator<Jwt> validator = TokenUtil.jwtValidator(ISSUER, null);

        Jwt tokenWithEmptyAud = jwt(ISSUER, List.of());
        Jwt tokenWithAud = jwt(ISSUER, List.of("https://api.example.com"));

        assertTrue(validator.validate(tokenWithEmptyAud).getErrors().isEmpty(),
                "Empty aud should be accepted when no audience configured");
        assertTrue(validator.validate(tokenWithAud).getErrors().isEmpty(),
                "Any aud should be accepted when no audience configured");
    }

    @Test
    public void testJwtValidatorWithBlankAudienceSkipsAudienceCheck() {
        OAuth2TokenValidator<Jwt> validator = TokenUtil.jwtValidator(ISSUER, "   ");

        Jwt tokenWithEmptyAud = jwt(ISSUER, List.of());
        assertTrue(validator.validate(tokenWithEmptyAud).getErrors().isEmpty(),
                "Empty aud should be accepted when audience is blank/whitespace");
    }

    @Test
    public void testJwtValidatorWithConfiguredAudienceAcceptsMatchingToken() {
        OAuth2TokenValidator<Jwt> validator = TokenUtil.jwtValidator(ISSUER, "https://api.example.com");

        Jwt token = jwt(ISSUER, List.of("https://api.example.com"));
        OAuth2TokenValidatorResult result = validator.validate(token);
        assertTrue(result.getErrors().isEmpty(), "Token with matching aud should pass");
    }

    @Test
    public void testJwtValidatorWithConfiguredAudienceRejectsNonMatchingToken() {
        OAuth2TokenValidator<Jwt> validator = TokenUtil.jwtValidator(ISSUER, "https://api.example.com");

        Jwt tokenWithWrongAud = jwt(ISSUER, List.of("https://other.example.com"));
        OAuth2TokenValidatorResult resultWrong = validator.validate(tokenWithWrongAud);
        assertFalse(resultWrong.getErrors().isEmpty(), "Token with non-matching aud should be rejected");

        Jwt tokenWithEmptyAud = jwt(ISSUER, List.of());
        OAuth2TokenValidatorResult resultEmpty = validator.validate(tokenWithEmptyAud);
        assertFalse(resultEmpty.getErrors().isEmpty(), "Token with empty aud should be rejected when audience configured");
    }

    @Test
    public void testJwtValidatorRejectsWrongIssuerRegardlessOfAudience() {
        OAuth2TokenValidator<Jwt> validator = TokenUtil.jwtValidator(ISSUER, null);

        Jwt token = jwt("https://other.kinde.com", List.of());
        OAuth2TokenValidatorResult result = validator.validate(token);
        assertFalse(result.getErrors().isEmpty(), "Wrong issuer should be rejected even with audience disabled");
    }

    @Test
    public void testRootIssuer() throws Exception {
        assertTrue (TokenUtil.isRootOrgIssuer("https://sample.kinde.com"));
        assertTrue (!TokenUtil.isRootOrgIssuer("https://sample.kinde.com/oauth2/default"));
    }

    private static Jwt jwt(String issuer, List<String> audiences) {
        Instant now = Instant.now();
        Jwt.Builder builder = Jwt.withTokenValue("token-value")
                .header("alg", "RS256")
                .issuer(issuer)
                .issuedAt(now)
                .expiresAt(now.plus(5, ChronoUnit.MINUTES))
                .subject("test-subject");
        if (audiences != null) {
            // The Spring Security Jwt builder requires a non-null List for the aud claim. An empty
            // list models the real Kinde token shape (no audience configured on the dashboard).
            builder.audience(audiences);
        }
        return builder.build();
    }
}
