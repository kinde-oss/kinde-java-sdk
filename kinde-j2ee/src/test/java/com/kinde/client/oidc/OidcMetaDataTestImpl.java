package com.kinde.client.oidc;

import com.google.inject.Inject;
import com.kinde.client.OidcMetaData;
import com.kinde.token.TestKeyGenerator;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.openid.connect.sdk.SubjectType;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;

import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.Arrays;

public class OidcMetaDataTestImpl implements OidcMetaData {

    private TestKeyGenerator testKeyGenerator;
    private Path jwksPath;
    private OIDCProviderMetadata opMetadata;

    @Inject
    public OidcMetaDataTestImpl(TestKeyGenerator testKeyGenerator) {
        this.testKeyGenerator = testKeyGenerator;
        this.jwksPath = testKeyGenerator.regenerateKey();
        this.opMetadata = createMockOIDCProviderMetadata();
    }

    private OIDCProviderMetadata createMockOIDCProviderMetadata() {
        Issuer issuer = new Issuer("http://localhost:8089");
        OIDCProviderMetadata metadata = new OIDCProviderMetadata(
            issuer,
            Arrays.asList(SubjectType.PUBLIC),
            URI.create("http://localhost:8089/oauth2/auth")
        );
        metadata.setAuthorizationEndpointURI(URI.create("http://localhost:8089/oauth2/auth"));
        metadata.setTokenEndpointURI(URI.create("http://localhost:8089/oauth2/token"));
        metadata.setEndSessionEndpointURI(URI.create("http://localhost:8089/logout"));
        metadata.setJWKSetURI(jwksPath.toUri());
        metadata.setUserInfoEndpointURI(URI.create("http://localhost:8089/oauth2/v2/user_profile"));
        return metadata;
    }

    @Override
    public OIDCProviderMetadata getOpMetadata() {
        return opMetadata;
    }

    @Override
    public URL getJwkUrl() {
        try {
            return jwksPath.toUri().toURL();
        } catch (java.net.MalformedURLException e) {
            throw new RuntimeException("Failed to create URL from path: " + jwksPath, e);
        }
    }
}
