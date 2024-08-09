package com.kinde.client.oidc;

import com.kinde.client.OidcMetaData;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import lombok.SneakyThrows;

import java.io.File;
import java.net.URL;

public class OidcMetaDataTestImpl implements OidcMetaData {


    @Override
    public OIDCProviderMetadata getOpMetadata() {
        return null;
    }

    @Override
    @SneakyThrows
    public URL getJwkUrl() {
        return new File("example.jwks").toURI().toURL();
    }
}
