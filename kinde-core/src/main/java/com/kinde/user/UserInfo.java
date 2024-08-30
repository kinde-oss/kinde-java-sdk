package com.kinde.user;

import lombok.Data;
import lombok.Getter;

@Getter
public class UserInfo {


    public UserInfo(com.nimbusds.openid.connect.sdk.claims.UserInfo userInfo) {
        this.userInfo = userInfo;
        this.subject = userInfo.getSubject().toString();
        this.email = userInfo.getEmailAddress();
        this.givenName = userInfo.getGivenName();
        this.id = userInfo.getName();
        this.familyName = userInfo.getFamilyName();
        this.picture = userInfo.getPicture().toString();
    }

    private com.nimbusds.openid.connect.sdk.claims.UserInfo userInfo;
    private String subject;
    private String givenName;
    private String id;
    private String familyName;
    private String email;
    private String picture;


}
