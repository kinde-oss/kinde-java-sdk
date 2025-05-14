package com.kinde.token;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtValidatorTest {

    @Test
    public void isJwtReturnsTrueForValidJwt() {
        String validJwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        assertTrue(JwtValidator.isJwt(validJwt));
    }

    @Test
    public void isJwtReturnsFalseForNullToken() {
        assertFalse(JwtValidator.isJwt(null));
    }

    @Test
    public void isJwtReturnsFalseForEmptyToken() {
        assertFalse(JwtValidator.isJwt(""));
    }

    @Test
    public void isJwtReturnsFalseForTokenWithLessThanThreeParts() {
        String invalidJwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ";
        assertFalse(JwtValidator.isJwt(invalidJwt));
    }

    @Test
    public void isJwtReturnsFalseForTokenWithMoreThanThreeParts() {
        String invalidJwt = "part1.part2.part3.part4";
        assertFalse(JwtValidator.isJwt(invalidJwt));
    }

}
