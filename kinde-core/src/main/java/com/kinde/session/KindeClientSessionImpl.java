package com.kinde.session;

import com.kinde.KindeClientSession;
import com.kinde.client.KindeClientImpl;

public class KindeClientSessionImpl implements KindeClientSession {
    private KindeClientImpl kindeClientImpl;

    public KindeClientSessionImpl(KindeClientImpl kindeClientImpl) {
        this.kindeClientImpl = kindeClientImpl;
    }
}
