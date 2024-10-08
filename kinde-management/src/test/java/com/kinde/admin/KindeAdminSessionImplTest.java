package com.kinde.admin;

import com.kinde.KindeClient;
import com.kinde.KindeClientSession;
import com.kinde.client.OidcMetaData;
import com.kinde.config.KindeConfig;
import com.kinde.token.AccessToken;
import com.kinde.token.IDToken;
import com.kinde.token.KindeTokens;
import com.kinde.token.RefreshToken;
import com.kinde.token.jwt.JwtGenerator;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.openid.connect.sdk.SubjectType;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.openapitools.client.ApiClient;

import java.net.URI;
import java.util.Arrays;

import static org.mockito.Mockito.when;

public class KindeAdminSessionImplTest {

    @Test
    public void testInitClient() throws Exception {
        KindeAdminSessionImpl kindeAdminSession1 = new KindeAdminSessionImpl(null);
        ApiClient apiClient = kindeAdminSession1.initClient();

        KindeClient kindeClient = Mockito.mock(KindeClient.class);
        KindeClientSession kindeClientSession = Mockito.mock(KindeClientSession.class);
        when(kindeClient.clientSession()).thenReturn(kindeClientSession);
        KindeConfig kindeConfig = Mockito.mock(KindeConfig.class);
        when(kindeClient.kindeConfig()).thenReturn(kindeConfig);
        OidcMetaData oidcMetaData = Mockito.mock(OidcMetaData.class);
        when(kindeClient.oidcMetaData()).thenReturn(oidcMetaData);
        OIDCProviderMetadata oidcProviderMetadata = new OIDCProviderMetadata(new Issuer("https://kinde.com"),
                Arrays.asList(SubjectType.PUBLIC), new URI("https://kinde.com"));
        oidcProviderMetadata.setTokenEndpointURI(new URI("https://kinde.com"));
        when(oidcMetaData.getOpMetadata()).thenReturn(oidcProviderMetadata);
        IDToken idToken = IDToken.init(JwtGenerator.generateIDToken(),true);
        AccessToken accessToken = AccessToken.init(JwtGenerator.generateAccessToken(),true);
        RefreshToken refreshToken = RefreshToken.init(JwtGenerator.refreshToken(),true);
        KindeTokens kindeTokens = new KindeTokens(Arrays.asList(),idToken,accessToken,refreshToken);
        when(kindeClientSession.retrieveTokens()).thenReturn(kindeTokens);
        KindeAdminSessionImpl kindeAdminSession2 = new KindeAdminSessionImpl(kindeClient);
        kindeAdminSession2.initClient();
    }
}
