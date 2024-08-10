package com.kinde.session;

import com.google.inject.Inject;
import com.kinde.KindeClientSession;
import com.kinde.client.KindeClientImpl;
import com.kinde.config.KindeConfig;
import com.kinde.guice.KindeAnnotations;
import com.kinde.token.KindeToken;

import java.util.Map;

public class KindeClientAccessTokenSessionImpl extends KindeClientSessionImpl {

    private KindeToken accessToken;

    @Inject
    public KindeClientAccessTokenSessionImpl(
            KindeConfig kindeConfig,
            @KindeAnnotations.AccessToken KindeToken accessToken) {
        super(kindeConfig);
        this.accessToken = accessToken;
    }

    @Override
    public String getValue() {
        return "";
    }
}
