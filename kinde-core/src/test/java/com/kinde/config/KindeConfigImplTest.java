package com.kinde.config;

import com.kinde.authorization.AuthorizationType;
import com.kinde.token.AccessToken;
import com.kinde.token.KindeToken;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class KindeConfigImplTest{

    /**
     * Rigourous Test :-)
     */
    @Test
    public void testKindeConfigTest() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(KindeParameters.DOMAIN.getValue(),"test-domain");
        parameters.put(KindeParameters.REDIRECT_URI.getValue(),"test-redirect-uri");
        parameters.put(KindeParameters.LOGOUT_REDIRECT_URI.getValue(),"test-logout-redirect-uri");
        parameters.put(KindeParameters.OPENID_ENDPOINT.getValue(),"test-openid-endpoint");
        parameters.put(KindeParameters.AUTHORIZATION_ENDPOINT.getValue(),"test-authorization-endpoint");
        parameters.put(KindeParameters.TOKEN_ENDPOINT.getValue(),"test-token-endpoint");
        parameters.put(KindeParameters.LOGOUT_ENDPOINT.getValue(),"test-logout-endpoint");
        parameters.put(KindeParameters.CLIENT_ID.getValue(),"test-client-id");
        parameters.put(KindeParameters.CLIENT_SECRET.getValue(),"test-client-secret");
        parameters.put(KindeParameters.GRANT_TYPE.getValue(), AuthorizationType.CODE);
        parameters.put(KindeParameters.SCOPES.getValue(), Arrays.asList("test-grant-types1","test-grant-types2"));
        parameters.put(KindeParameters.PROTOCOL.getValue(),"test-protocol");
        parameters.put(KindeParameters.AUDIENCE.getValue(), Arrays.asList("test-grant-types1","test-grant-types2"));
        parameters.put(KindeParameters.HAS_SUCCESS_PAGE.getValue(),true);
        parameters.put(KindeParameters.LANG.getValue(),"eng");
        parameters.put(KindeParameters.ORG_CODE.getValue(),"org_code");
        parameters.put("long value",Long.valueOf(10));
        KindeConfigImpl kindeConfigImpl = new KindeConfigImpl(parameters);

        assertTrue( kindeConfigImpl.domain().equals("test-domain") );
        assertTrue( kindeConfigImpl.redirectUri().equals("test-redirect-uri") );
        assertTrue( kindeConfigImpl.logoutRedirectUri().equals("test-logout-redirect-uri") );
        assertTrue( kindeConfigImpl.openidEndpoint().equals("test-openid-endpoint") );
        assertTrue( kindeConfigImpl.authorizationEndpoint().equals("test-authorization-endpoint") );
        assertTrue( kindeConfigImpl.tokenEndpoint().equals("test-token-endpoint") );
        assertTrue( kindeConfigImpl.logoutEndpoint().equals("test-logout-endpoint") );
        assertTrue( kindeConfigImpl.clientId().equals("test-client-id") );
        assertTrue( kindeConfigImpl.clientSecret().equals("test-client-secret") );
        assertTrue( kindeConfigImpl.grantType() == AuthorizationType.CODE );
        assertTrue( kindeConfigImpl.scopes().equals(Arrays.asList("test-grant-types1","test-grant-types2")) );
        assertTrue( kindeConfigImpl.protocol().equals("test-protocol") );
        assertTrue( kindeConfigImpl.audience().equals(Arrays.asList("test-grant-types1","test-grant-types2")) );
        assertTrue( kindeConfigImpl.lang().equals("eng") );
        assertTrue( kindeConfigImpl.hasSuccessPage().equals(Boolean.TRUE) );
        assertTrue( kindeConfigImpl.orgCode().equals("org_code") );

        assertTrue(kindeConfigImpl.getLongValue("long value") == 10);
        assertTrue(kindeConfigImpl.getStringValue(KindeParameters.DOMAIN.getValue()).equals("test-domain"));
        assertTrue(kindeConfigImpl.getValue(KindeParameters.DOMAIN.getValue()).equals("test-domain"));
    }
}
