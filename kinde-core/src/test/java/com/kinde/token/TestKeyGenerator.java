package com.kinde.token;

import com.nimbusds.jose.jwk.JWK;

import java.nio.file.Path;

public interface TestKeyGenerator {
    Path regenerateKey();

    JWK getJWK();
}
