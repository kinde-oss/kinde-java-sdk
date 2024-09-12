package com.kinde.token;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@Getter
@ToString
public class KindeTokens {
    List<String> scopes;
    IDToken idToken;
    AccessToken accessToken;
    RefreshToken refreshToken;
}
