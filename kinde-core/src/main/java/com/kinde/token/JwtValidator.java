package com.kinde.token;

import com.nimbusds.jwt.SignedJWT;

import java.text.ParseException;

public class JwtValidator {
    public static boolean isJwt(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }

        try {
            SignedJWT.parse(token);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}