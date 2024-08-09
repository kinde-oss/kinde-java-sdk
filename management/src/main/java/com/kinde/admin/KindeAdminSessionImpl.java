package com.kinde.admin;

import com.kinde.KindeAdminSession;
import com.kinde.KindeClient;
import com.kinde.KindeClientSession;

public class KindeAdminSessionImpl implements KindeAdminSession {
    private KindeClient kindeClient;

    public KindeAdminSessionImpl(KindeClient kindeClient) {
        this.kindeClient = kindeClient;
    }
}
