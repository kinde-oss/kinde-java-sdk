package com.kinde.session;

import com.google.inject.Inject;
import com.kinde.KindeClientSession;
import com.kinde.client.KindeClientImpl;
import com.kinde.config.KindeConfig;
import com.kinde.guice.KindeAnnotations;

import java.util.Map;

public class KindeClientSessionImpl implements KindeClientSession {

    protected KindeConfig kindeConfig;

    @Inject
    public KindeClientSessionImpl(KindeConfig kindeConfig) {
        this.kindeConfig = kindeConfig;
    }

    @Override
    public String getValue() {
        return "";
    }
}
