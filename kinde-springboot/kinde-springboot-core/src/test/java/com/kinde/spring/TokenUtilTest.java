package com.kinde.spring;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class TokenUtilTest {

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

    @Test
    public void testRootIssuer() throws Exception {
        assertTrue (TokenUtil.isRootOrgIssuer("https://sample.kinde.com"));
        assertTrue (!TokenUtil.isRootOrgIssuer("https://sample.kinde.com/oauth2/default"));
    }


}
