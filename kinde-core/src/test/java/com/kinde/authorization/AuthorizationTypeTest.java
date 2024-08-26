package com.kinde.authorization;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.fail;

public class AuthorizationTypeTest {

    @Test
    public void testAuthorizationCodeTest() {
        AuthorizationType code = AuthorizationType.fromValue("CODE");
        Assert.assertTrue(code == AuthorizationType.CODE);
        Assert.assertTrue(code.getValue().equals(AuthorizationType.CODE.getValue()));
    }

    @Test
    public void testAuthorizationCustomTest() {
        AuthorizationType custom = AuthorizationType.fromValue("CUSTOM");
        Assert.assertTrue(custom == AuthorizationType.CUSTOM);
        Assert.assertTrue(custom.getValue().equals(AuthorizationType.CUSTOM.getValue()));
    }

    @Test
    public void testAuthorizationImplicitTest() {
        AuthorizationType implicit = AuthorizationType.fromValue("IMPLICIT");
        Assert.assertTrue(implicit == AuthorizationType.IMPLICIT);
        Assert.assertTrue(implicit.getValue().equals(AuthorizationType.IMPLICIT.getValue()));
    }

    @Test
    public void testUnkownTest() {
        try {
            AuthorizationType unknown = AuthorizationType.fromValue("UNKNOWN");
            fail("Failing");
        } catch (IllegalArgumentException ex) {
            Assert.assertNotNull(ex);
        }
    }
}
