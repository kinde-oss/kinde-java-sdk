package test.java.com.kinde.token;

import java.nio.file.Path;

public interface TestKeyGenerator {
    Path regenerateKey();
    com.nimbusds.jose.jwk.RSAKey getJWK();
}
