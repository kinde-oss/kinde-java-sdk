package com.kinde.session;

import com.google.inject.Inject;
import com.kinde.KindeClientSession;
import com.kinde.client.KindeClientImpl;
import com.kinde.guice.KindeAnnotations;
import com.kinde.token.KindeToken;

public class KindeClientAccessTokenSessionImpl implements KindeClientSession {

    private KindeToken accessToken;

    @Inject
    public KindeClientAccessTokenSessionImpl(@KindeAnnotations.AccessToken KindeToken accessToken) {
        this.accessToken = accessToken;
    }
}
