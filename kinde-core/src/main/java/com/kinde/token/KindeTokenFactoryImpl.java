package com.kinde.token;

import com.google.inject.Inject;
import com.kinde.KindeTokenFactory;
import com.kinde.client.KindeClientImpl;
import com.kinde.client.OidcMetaData;
import com.kinde.token.jwk.KindeJwkStore;
import com.nimbusds.jose.jwk.JWKSet;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;

@Slf4j
public class KindeTokenFactoryImpl implements KindeTokenFactory {

    private OidcMetaData oidcMetaData;
    private KindeJwkStore kindeJwkStore;

    @Inject
    public KindeTokenFactoryImpl(OidcMetaData oidcMetaData,KindeJwkStore kindeJwkStore) {
        this.oidcMetaData = oidcMetaData;
        this.kindeJwkStore = kindeJwkStore;
    }




}
