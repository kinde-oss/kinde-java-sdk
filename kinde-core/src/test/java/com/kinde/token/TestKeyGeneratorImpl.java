package com.kinde.token;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.nimbusds.jose.jwk.*;
import lombok.SneakyThrows;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.UUID;

@Singleton
public class TestKeyGeneratorImpl implements TestKeyGenerator{

    private Path tmpPath;
    private JWK jwk;

    @Inject
    public TestKeyGeneratorImpl() {
        System.out.println("The test key generator has been started");
    }

    @Override
    @SneakyThrows
    public Path regenerateKey() {
        this.tmpPath = Files.createTempFile(null, ".jwks");
        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
        gen.initialize(2048);
        KeyPair keyPair = gen.generateKeyPair();

        // Convert to JWK format
        this.jwk = new RSAKey.Builder((RSAPublicKey)keyPair.getPublic())
                .privateKey((RSAPrivateKey)keyPair.getPrivate())
                .keyUse(KeyUse.SIGNATURE)
                .keyID(UUID.randomUUID().toString())
                .issueTime(new Date())
                .build();
        JWKSet jwkSet = new JWKSet(jwk);

        // Step 4: Write the JSON string to the temporary file
        try (FileWriter writer = new FileWriter(this.tmpPath.toFile())) {
            writer.write(jwkSet.toJSONObject().toString());
        }

        return this.tmpPath;
    }

    @Override
    public Path getTmpPath() {
        return this.tmpPath;
    }

    @Override
    public JWK getJWK() {
        return this.jwk;
    }


}
