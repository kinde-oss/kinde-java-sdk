package com.kinde.token;

import com.kinde.token.jwt.JwtGenerator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AccessTokenTest {

    /**
     * Rigourous Test :-)
     */
    @Test
    public void testAccessTokenTest() throws Exception {
        String tokenStr = JwtGenerator.generateAccessToken();
        KindeToken kindeToken1 = AccessToken.init(tokenStr,false);
        assertTrue( kindeToken1.token().equals(tokenStr) );
        assertTrue( kindeToken1.valid() == false );

        String tokenStr2 = JwtGenerator.generateAccessToken();
        KindeToken kindeToken2 = AccessToken.init(tokenStr2,true);
        assertTrue( kindeToken2.token().equals(tokenStr2) );
        assertTrue( kindeToken2.valid() );

        String tokenString = JwtGenerator.generateAccessToken();

        KindeToken kindeToken4 = AccessToken.init(tokenString,true);

        assertNotNull(tokenString);
        assertNotNull(kindeToken4.getPermissions());
    }
}
