package com.kinde.client.oidc;

import com.google.inject.Inject;
import com.kinde.client.OidcMetaData;
import com.kinde.config.KindeConfig;
import com.kinde.guice.KindeAnnotations;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import lombok.SneakyThrows;

import java.net.URL;

public class OidcMetaDataImpl implements OidcMetaData {

    private OIDCProviderMetadata opMetadata;

    @Inject
    public OidcMetaDataImpl(KindeConfig kindeConfig) throws Exception {
        // The OpenID provider issuer URL
        Issuer issuer = new Issuer(kindeConfig.domain());

        // Will resolve the OpenID provider metadata automatically
        this.opMetadata = OIDCProviderMetadata.resolve(issuer);
    }

    public OIDCProviderMetadata getOpMetadata() {
        return this.opMetadata;
    }

    @Override
    @SneakyThrows
    public URL getJwkUrl() {
        return this.opMetadata.getJWKSetURI().toURL();
    }
}
