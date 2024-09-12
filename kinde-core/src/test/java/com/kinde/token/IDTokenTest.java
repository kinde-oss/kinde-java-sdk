package com.kinde.token;

import com.kinde.token.jwt.JwtGenerator;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

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

        assertTrue(kindeToken2.getStringFlag("test_str").equals("test_str"));
        assertTrue(kindeToken2.getIntegerFlag("test_integer").equals(1));
        assertTrue(kindeToken2.getBooleanFlag("test_boolean").equals(false));
        assertTrue(((BaseToken)kindeToken2).getFlags() instanceof Map);
        assertTrue(kindeToken2.getClaim("permissions") != null);
        assertTrue(kindeToken2.getOrganisations() instanceof List);
        assertTrue(kindeToken2.getUser().equals("test"));

    }
}
