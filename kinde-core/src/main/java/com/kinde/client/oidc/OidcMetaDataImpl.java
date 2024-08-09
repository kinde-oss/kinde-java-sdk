package com.kinde.client.oidc;

import com.google.inject.Inject;
import com.kinde.client.OidcMetaData;
import com.kinde.guice.KindeAnnotations;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import lombok.SneakyThrows;

import java.net.URL;

public class OidcMetaDataImpl implements OidcMetaData {

    private OIDCProviderMetadata opMetadata;

    @Inject
    public OidcMetaDataImpl(@KindeAnnotations.ClientConfigDomain String domain) throws Exception {
        // The OpenID provider issuer URL
        Issuer issuer = new Issuer(domain);

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
