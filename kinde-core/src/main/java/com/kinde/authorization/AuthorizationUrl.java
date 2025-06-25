package com.kinde.authorization;

import com.nimbusds.oauth2.sdk.pkce.CodeVerifier;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.net.URL;

@Getter
@AllArgsConstructor
public class AuthorizationUrl {
    private URL url;
    private CodeVerifier codeVerifier;
}
