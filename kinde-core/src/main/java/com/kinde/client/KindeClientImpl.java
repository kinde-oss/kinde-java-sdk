package com.kinde.client;

import com.kinde.KindTokenFactory;
import com.kinde.KindeClient;
import com.kinde.KindeClientSession;
import com.kinde.session.KindClientSessionImpl;
import com.kinde.token.KindTokenFactoryImpl;
import com.kinde.token.KindeToken;

import java.util.Map;

public class KindeClientImpl implements KindeClient {

    private Map<String, Object> parameters;

    public KindeClientImpl(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    @Override
    public KindeClientSession initClientSession(KindeToken accessToken) {
        return new KindClientSessionImpl(this);
    }

    @Override
    public KindeClientSession initClientSession() {
        return new KindClientSessionImpl(this);
    }

    @Override
    public KindTokenFactory initTokenFactory() {
        return new KindTokenFactoryImpl(this);
    }
}
