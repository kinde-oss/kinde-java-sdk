package com.kinde.client;

import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;

public interface OidcMetaData {

    OIDCProviderMetadata getOpMetadata();
}
