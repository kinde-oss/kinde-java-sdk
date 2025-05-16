package com.kinde.token;

import com.nimbusds.jwt.SignedJWT;

import java.text.ParseException;

public class JwtValidator {
    public static SignedJWT isJwt(String token) {
        if (token == null || token.trim().isEmpty()) {
            return null;
        }

        try {
            return SignedJWT.parse(token);
        } catch (ParseException e) {
            return null;
        }
    }
}