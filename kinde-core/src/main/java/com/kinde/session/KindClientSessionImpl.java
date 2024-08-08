package com.kinde.session;

import com.kinde.KindeClientSession;
import com.kinde.client.KindeClientImpl;

public class KindClientSessionImpl implements KindeClientSession {
    private KindeClientImpl kindeClientImpl;

    public KindClientSessionImpl(KindeClientImpl kindeClientImpl) {
        this.kindeClientImpl = kindeClientImpl;
    }
}
