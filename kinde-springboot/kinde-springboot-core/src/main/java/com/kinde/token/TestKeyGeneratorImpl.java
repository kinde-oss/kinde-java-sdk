package com.kinde.token;

import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;

public class TestKeyGeneratorImpl implements TestKeyGenerator {

    private RSAKey jwk;

    @Override
    public Path regenerateKey() {
        try {
            // Generate RSA key pair
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            // Convert to JWK format
            this.jwk = new RSAKey.Builder((RSAPublicKey)keyPair.getPublic())
                    .privateKey((RSAPrivateKey)keyPair.getPrivate())
                    .keyUse(KeyUse.SIGNATURE)
                    .keyID("test-key-123") // Use a consistent key ID for testing
                    .issueTime(new Date())
                    .build();

            // Write JWKS to file
            Path jwksPath = Files.createTempFile("test-jwks", ".json");
            String jwksJson = "{\"keys\":[" + jwk.toJSONString() + "]}";
            Files.write(jwksPath, jwksJson.getBytes());

            System.out.println("The test key generator has been started");
            return jwksPath;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate test key", e);
        }
    }

    @Override
    public RSAKey getJWK() {
        return jwk;
    }
}
