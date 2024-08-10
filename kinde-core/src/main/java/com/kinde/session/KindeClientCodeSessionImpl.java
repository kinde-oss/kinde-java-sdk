package com.kinde.session;

import com.google.inject.Inject;
import com.kinde.client.OidcMetaData;
import com.kinde.config.KindeConfig;
import com.kinde.guice.KindeAnnotations;
import com.kinde.token.KindeToken;

public class KindeClientCodeSessionImpl extends KindeClientSessionImpl {

    private String code;

    @Inject
    public KindeClientCodeSessionImpl(
            KindeConfig kindeConfig,
            OidcMetaData oidcMetaData,
            @KindeAnnotations.KindeCode String code) {
        super(kindeConfig,oidcMetaData);
        this.code = code;
    }

    @Override
    public String getValue() {
        return "";
    }
}
