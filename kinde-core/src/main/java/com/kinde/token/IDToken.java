package com.kinde.token;

import com.nimbusds.jwt.SignedJWT;
import lombok.SneakyThrows;

import java.util.List;

public class IDToken extends BaseToken {

    private IDToken(String token, boolean valid) {
        super(token,valid);
    }

    public static KindeToken init(String token, boolean valid) {
        return new IDToken(token,valid);
    }


}
