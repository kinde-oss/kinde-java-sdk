package com.kinde.client;

import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;

import java.net.URL;

public interface OidcMetaData {

    OIDCProviderMetadata getOpMetadata();

    URL getJwkUrl();
}
