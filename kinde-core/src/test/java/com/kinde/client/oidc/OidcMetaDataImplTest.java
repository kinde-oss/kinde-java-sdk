package com.kinde.client.oidc;

import com.kinde.config.KindeConfigImpl;
import com.kinde.config.KindeParameters;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class OidcMetaDataImplTest {

    /**
     * Rigourous Test :-)
     */
    @Test
    public void testOidcMetaDataImplTest() throws Exception {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(KindeParameters.DOMAIN.getValue(),"https://burntjam.kinde.com");
        KindeConfigImpl kindeConfig = new KindeConfigImpl(parameters);

        OidcMetaDataImpl oidcMetaData = new OidcMetaDataImpl(kindeConfig);
        assertTrue(oidcMetaData.getOpMetadata()!= null);
        assertTrue( oidcMetaData.getJwkUrl() != null );
    }
}
