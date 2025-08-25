package com.kinde.token;

import com.kinde.accounts.KindeAccountsClient;
import com.nimbusds.jwt.SignedJWT;
import lombok.SneakyThrows;

import java.util.List;

public class IDToken extends BaseToken {

    private IDToken(String token, boolean valid) {
        super(token, valid);
    }

    private IDToken(String token, boolean valid, KindeAccountsClient accountsClient) {
        super(token, valid, accountsClient);
    }

    public static IDToken init(String token, boolean valid) {
        return new IDToken(token, valid);
    }

    public static IDToken init(String token, boolean valid, KindeAccountsClient accountsClient) {
        return new IDToken(token, valid, accountsClient);
    }
}
