package com.kinde.user;

import lombok.Data;

@Data
public class UserInfo {

    com.nimbusds.openid.connect.sdk.claims.UserInfo userInfo;
    public UserInfo(com.nimbusds.openid.connect.sdk.claims.UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    private String subject;
    private String email;
    private String name;
}
