package com.kinde.client;

import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;

public class OidcMetaData {

    private OIDCProviderMetadata opMetadata;

    public OidcMetaData(String domain) throws Exception {
        // The OpenID provider issuer URL
        Issuer issuer = new Issuer(domain);

        // Will resolve the OpenID provider metadata automatically
        OIDCProviderMetadata opMetadata = OIDCProviderMetadata.resolve(issuer);
    }

}
