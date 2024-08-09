package com.kinde.client.oidc;

import com.google.inject.Inject;
import com.kinde.client.OidcMetaData;
import com.kinde.token.TestKeyGenerator;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import lombok.SneakyThrows;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;

public class OidcMetaDataTestImpl implements OidcMetaData {

    private TestKeyGenerator testKeyGenerator;
    private Path jwksPath;

    @Inject
    public OidcMetaDataTestImpl(TestKeyGenerator testKeyGenerator) {
        this.testKeyGenerator = testKeyGenerator;
        this.jwksPath = testKeyGenerator.regenerateKey();
    }


    @Override
    public OIDCProviderMetadata getOpMetadata() {
        return null;
    }

    @Override
    @SneakyThrows
    public URL getJwkUrl() {
        return jwksPath.toUri().toURL();
    }
}
