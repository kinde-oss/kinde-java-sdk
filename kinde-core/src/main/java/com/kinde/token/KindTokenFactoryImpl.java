package com.kinde.token;

import com.kinde.KindTokenFactory;
import com.kinde.client.KindeClientImpl;

public class KindTokenFactoryImpl implements KindTokenFactory {

    private KindeClientImpl kindeClientImpl;

    public KindTokenFactoryImpl(KindeClientImpl kindeClientImpl) {
        this.kindeClientImpl = kindeClientImpl;
    }


}
