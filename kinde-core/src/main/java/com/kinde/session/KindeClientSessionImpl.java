package com.kinde.session;

import com.google.inject.Inject;
import com.kinde.KindeClientSession;
import com.kinde.client.KindeClientImpl;
import com.kinde.client.OidcMetaData;
import com.kinde.config.KindeConfig;
import com.kinde.guice.KindeAnnotations;

import java.util.Map;

public class KindeClientSessionImpl implements KindeClientSession {

    protected KindeConfig kindeConfig;
    protected OidcMetaData oidcMetaData;

    @Inject
    public KindeClientSessionImpl(KindeConfig kindeConfig, OidcMetaData oidcMetaData) {
        this.kindeConfig = kindeConfig;
        this.oidcMetaData = oidcMetaData;
    }

    @Override
    public String getValue() {
        return "";
    }
}
