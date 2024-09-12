package com.kinde.token;

import com.kinde.token.jwt.JwtGenerator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RefreshTokenTest {

    /**
     * Rigourous Test :-)
     */
    @Test
    public void testRefreshTokenTest() throws Exception {
        String token1 = JwtGenerator.refreshToken();
        KindeToken kindeToken1 = RefreshToken.init(token1,false);
        assertTrue( kindeToken1.token().equals(token1) );
        assertTrue( kindeToken1.valid() == false );

        String token2 = JwtGenerator.refreshToken();
        KindeToken kindeToken2 = RefreshToken.init(token2,true);
        assertTrue( kindeToken2.token().equals(token2) );
        assertTrue( kindeToken2.valid() );

        String tokenString = JwtGenerator.refreshToken();

        KindeToken kindeToken4 = RefreshToken.init(tokenString,true);

        assertNotNull(tokenString);
        assertNotNull(kindeToken4.getPermissions());
    }
}
