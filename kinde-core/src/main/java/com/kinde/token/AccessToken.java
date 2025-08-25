package com.kinde.token;

import com.kinde.accounts.KindeAccountsClient;

public class AccessToken extends BaseToken {

    private AccessToken(String token, boolean valid) {
        super(token, valid);
    }

    private AccessToken(String token, boolean valid, KindeAccountsClient accountsClient) {
        super(token, valid, accountsClient);
    }

    public static AccessToken init(String token, boolean valid) {
        return new AccessToken(token, valid);
    }

    public static AccessToken init(String token, boolean valid, KindeAccountsClient accountsClient) {
        return new AccessToken(token, valid, accountsClient);
    }
}
