package com.kinde.token.jwk;

import com.nimbusds.jose.jwk.JWKSet;

public interface KindeJwkStore {

    JWKSet publicKeys();
}
