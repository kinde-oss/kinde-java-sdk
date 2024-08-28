package com.kinde.user;

import lombok.Data;

@Data
public class UserInfo {

    private com.nimbusds.openid.connect.sdk.claims.UserInfo userInfo;

    public UserInfo(com.nimbusds.openid.connect.sdk.claims.UserInfo userInfo) {
        this.userInfo = userInfo;
        this.email = userInfo.getEmailAddress();
        this.subject = userInfo.getSubject().toString();
        this.name = userInfo.getName();
    }

    private String subject;
    private String email;
    private String name;
}
