package com.kinde.token;

import com.nimbusds.jwt.SignedJWT;
import lombok.SneakyThrows;

import java.util.List;

public class RefreshToken extends BaseToken {

    private RefreshToken(String token, boolean valid) {
        super(token,valid);
    }

    public static KindeToken init(String token,boolean valid) {
        return new RefreshToken(token,valid);
    }


}
