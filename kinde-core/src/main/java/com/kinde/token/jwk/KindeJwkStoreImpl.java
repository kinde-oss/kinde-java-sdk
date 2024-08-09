package com.kinde.token.jwk;

import com.google.inject.Inject;
import com.kinde.client.KindeClientImpl;
import com.kinde.client.OidcMetaData;
import com.nimbusds.jose.jwk.JWKSet;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KindeJwkStoreImpl implements KindeJwkStore {

    private OidcMetaData oidcMetaData;
    private JWKSet publicKeys;

    @Inject
    public KindeJwkStoreImpl(OidcMetaData oidcMetaData) throws Exception {
        this.oidcMetaData = oidcMetaData;
        log.debug("Load the JWKSET : {}",oidcMetaData.getJwkUrl());
        this.publicKeys =  JWKSet.load(oidcMetaData.getJwkUrl());
    }

    @Override
    public JWKSet publicKeys() {
        return this.publicKeys;
    }
}
