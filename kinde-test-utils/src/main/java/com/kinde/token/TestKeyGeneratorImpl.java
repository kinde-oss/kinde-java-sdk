package com.kinde.token;

import com.google.inject.Singleton;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Singleton
public class TestKeyGeneratorImpl implements TestKeyGenerator {

    private Path tmpPath;
    private JWK jwk;

    public TestKeyGeneratorImpl() {
        this.tmpPath = Paths.get(System.getProperty("java.io.tmpdir"), "kinde-test-jwks.json");
        this.jwk = generateJWK();
    }

    @Override
    public Path regenerateKey() {
        this.jwk = generateJWK();
        writeJWKToFile();
        return tmpPath;
    }

    @Override
    public Path getTmpPath() {
        return tmpPath;
    }

    @Override
    public JWK getJWK() {
        return jwk;
    }

    private JWK generateJWK() {
        try {
            RSAKey rsaKey = new RSAKeyGenerator(2048)
                    .keyUse(KeyUse.SIGNATURE)
                    .keyID("test-key-id")
                    .generate();

            return rsaKey;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate test JWK", e);
        }
    }

    private void writeJWKToFile() {
        try {
            JWKSet jwkSet = new JWKSet(jwk);
            String jwksJson = jwkSet.toString();
            Files.write(tmpPath, jwksJson.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Failed to write JWK to file", e);
        }
    }
}
