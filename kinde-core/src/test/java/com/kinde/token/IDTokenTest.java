package com.kinde.token;

import com.kinde.token.jwt.JwtGenerator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class IDTokenTest {

    /**
     * Rigourous Test :-)
     */
    @Test
    public void testIDTokenTest() throws Exception {
        String token1 = JwtGenerator.generateIDToken();
        KindeToken kindeToken1 = IDToken.init(token1,false);
        assertTrue( kindeToken1.token().equals(token1) );
        assertTrue( kindeToken1.valid() == false );

        String token2 = JwtGenerator.generateIDToken();
        KindeToken kindeToken2 = IDToken.init(token2,true);
        assertTrue( kindeToken2.token().equals(token2) );
        assertTrue( kindeToken2.valid() );


        String tokenString = JwtGenerator.generateIDToken();
        System.out.println(tokenString);

        KindeToken kindeToken4 = IDToken.init(tokenString,true);

        System.out.println(kindeToken4.getPermissions());
    }
}
