package com.kinde.session;

import com.kinde.KindeClientSession;
import com.kinde.authorization.AuthorizationType;
import com.kinde.authorization.AuthorizationUrl;
import com.kinde.client.OidcMetaData;
import com.kinde.config.KindeConfig;
import com.kinde.exceptions.StepUpNotSupportedException;
import com.kinde.stepup.AcrValue;
import com.kinde.stepup.StepUpRequest;
import com.nimbusds.openid.connect.sdk.claims.ACR;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class KindeClientSessionStepUpTest {

    private static KindeClientSessionImpl session(boolean stepUpSupported) {
        KindeConfig config = mock(KindeConfig.class);
        when(config.clientId()).thenReturn("test-client-id");
        when(config.redirectUri()).thenReturn("http://localhost/callback");
        when(config.grantType()).thenReturn(AuthorizationType.CODE);
        when(config.scopes()).thenReturn(List.of("openid", "email"));

        OidcMetaData meta = mock(OidcMetaData.class);
        OIDCProviderMetadata metadata = mock(OIDCProviderMetadata.class);
        when(metadata.getAuthorizationEndpointURI()).thenReturn(URI.create("http://localhost:8089/oauth2/auth"));
        when(metadata.getACRs()).thenReturn(stepUpSupported ? List.of(new ACR("mfa"), new ACR("phr")) : null);
        when(meta.getOpMetadata()).thenReturn(metadata);
        return new KindeClientSessionImpl(config, meta);
    }

    private static Map<String, String> query(AuthorizationUrl url) {
        Map<String, String> params = new HashMap<>();
        for (String pair : url.getUrl().getQuery().split("&")) {
            int eq = pair.indexOf('=');
            params.put(URLDecoder.decode(pair.substring(0, eq), StandardCharsets.UTF_8),
                    URLDecoder.decode(pair.substring(eq + 1), StandardCharsets.UTF_8));
        }
        return params;
    }

    @Test
    @DisplayName("stepUp sends acr_values and max_age on a normal code + PKCE request")
    void stepUpSendsParameters() {
        AuthorizationUrl url = session(true).stepUp(StepUpRequest.of(AcrValue.MFA, 300));

        Map<String, String> params = query(url);
        assertEquals("mfa", params.get("acr_values"));
        assertEquals("300", params.get("max_age"));
        assertEquals("code", params.get("response_type"));
        assertEquals("S256", params.get("code_challenge_method"));
        assertEquals("true", params.get("supports_reauth"));
        assertNotNull(url.getCodeVerifier());
        assertEquals(params.get("state"), url.getState());
        assertEquals("mfa", url.getAcrValues());
        assertEquals(300, url.getMaxAge());
    }

    @Test
    @DisplayName("stepUp with max_age only sends no acr_values, and max_age=0 is sent")
    void stepUpMaxAgeOnly() {
        AuthorizationUrl url = session(true).stepUp(StepUpRequest.ofMaxAge(0));
        Map<String, String> params = query(url);
        assertEquals("0", params.get("max_age"));
        assertFalse(params.containsKey("acr_values"));
        assertNull(url.getAcrValues());
        assertEquals(0, url.getMaxAge());
    }

    @Test
    @DisplayName("stepUp forwards extra parameters, and raw step-up parameters override the typed ones")
    void stepUpPassthroughParameters() {
        AuthorizationUrl url = session(true).stepUp(StepUpRequest.builder()
                .acr(AcrValue.MFA)
                .parameter("org_code", "org_1")
                .parameter("acr_values", "urn:okta:loa:2fa:any")
                .parameter("max_age", "60")
                .build());
        Map<String, String> params = query(url);
        assertEquals("org_1", params.get("org_code"));
        assertEquals("urn:okta:loa:2fa:any", params.get("acr_values"));
        assertEquals("60", params.get("max_age"));
    }

    @Test
    @DisplayName("stepUp rejects a request that asks for nothing")
    void stepUpRequiresSomething() {
        KindeClientSessionImpl session = session(true);
        assertThrows(IllegalArgumentException.class, () -> session.stepUp(StepUpRequest.builder().build()));
        assertThrows(IllegalArgumentException.class, () -> session.stepUp(null));
    }

    @Test
    @DisplayName("stepUp rejects prompt=none, unrecognised acr_values and a malformed max_age")
    void stepUpRejectsInvalidRequests() {
        KindeClientSessionImpl session = session(true);
        assertThrows(IllegalArgumentException.class, () -> session.stepUp(
                StepUpRequest.builder().acr(AcrValue.MFA).parameter("prompt", "none").build()));
        assertThrows(IllegalArgumentException.class, () -> session.stepUp(
                StepUpRequest.builder().parameter("acr_values", "gold").build()));
        assertThrows(IllegalArgumentException.class, () -> session.stepUp(
                StepUpRequest.builder().acr(AcrValue.MFA).parameter("max_age", "NaN").build()));
    }

    @Test
    @DisplayName("stepUp refuses to send when the environment does not advertise step-up")
    void stepUpNotSupported() {
        KindeClientSessionImpl session = session(false);
        assertFalse(session.isStepUpSupported());
        assertThrows(StepUpNotSupportedException.class, () -> session.stepUp(StepUpRequest.of(AcrValue.MFA)));
        assertTrue(session(true).isStepUpSupported());
    }

    @Test
    @DisplayName("authorizationUrlWithParameters drops unset step-up parameters and rejects malformed ones")
    void authorizationUrlNormalizesStepUpParameters() {
        KindeClientSessionImpl session = session(true);
        Map<String, String> blank = new HashMap<>();
        blank.put("acr_values", " ");
        blank.put("max_age", "");
        Map<String, String> params = query(session.authorizationUrlWithParameters(blank));
        assertFalse(params.containsKey("acr_values"));
        assertFalse(params.containsKey("max_age"));

        AuthorizationUrl url = session.authorizationUrlWithParameters(Map.of("acr_values", "phr", "max_age", "120"));
        assertEquals("phr", query(url).get("acr_values"));
        assertEquals("phr", url.getAcrValues());
        assertEquals(120, url.getMaxAge());

        assertThrows(IllegalArgumentException.class,
                () -> session.authorizationUrlWithParameters(Map.of("max_age", "undefined")));
    }

    @Test
    @DisplayName("every authorization URL carries its state")
    void loginCarriesState() {
        AuthorizationUrl url = session(true).login();
        assertNotNull(url.getState());
        assertEquals(query(url).get("state"), url.getState());
    }

    @Test
    @DisplayName("implementations without step-up say so")
    void defaultImplementation() {
        KindeClientSession session = mock(KindeClientSession.class, org.mockito.Mockito.CALLS_REAL_METHODS);
        assertFalse(session.isStepUpSupported());
        assertThrows(UnsupportedOperationException.class, () -> session.stepUp(StepUpRequest.of(AcrValue.MFA)));
    }
}
