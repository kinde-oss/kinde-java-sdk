package com.kinde.authorization;

import com.nimbusds.oauth2.sdk.pkce.CodeVerifier;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.net.URL;

@Data
@AllArgsConstructor
public class AuthorizationUrl {
    private URL url;
    private CodeVerifier codeVerifier;
}
