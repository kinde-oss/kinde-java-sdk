package com.kinde.user;

import com.nimbusds.jwt.JWTClaimsSet;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.ToString;

@ToString
@Getter
public class UserInfo {

    @SneakyThrows
    public UserInfo(com.nimbusds.openid.connect.sdk.claims.UserInfo userInfo) {
        this.userInfo = userInfo;
        this.subject = userInfo.getSubject().toString();
        this.email = userInfo.getEmailAddress();
        this.givenName = userInfo.getGivenName();
        this.id = userInfo.getName();
        this.familyName = userInfo.getFamilyName();
        this.picture = userInfo.getPicture() == null? null : userInfo.getPicture().toString();
    }

    private com.nimbusds.openid.connect.sdk.claims.UserInfo userInfo;
    private String subject;
    private String givenName;
    private String id;
    private String familyName;
    private String email;
    private String picture;


}
