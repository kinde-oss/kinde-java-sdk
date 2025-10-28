package com.kinde.spring.sdk;

import com.kinde.KindeClient;
import com.kinde.KindeClientBuilder;
import com.kinde.authorization.AuthorizationType;
import com.kinde.spring.config.KindeOAuth2Properties;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.annotation.ApplicationScope;

@ApplicationScope
public class KindeSdkClient {

    @Autowired
    private KindeOAuth2Properties kindeOAuth2Properties;

    private KindeClientBuilder kindeClientBuilder;

    @PostConstruct
    public void init() {
        this.kindeClientBuilder = KindeClientBuilder.builder();
        this.kindeClientBuilder.clientId(kindeOAuth2Properties.getClientId());
        this.kindeClientBuilder.clientSecret(kindeOAuth2Properties.getClientSecret());
        // default to code as that is the only type currently supported by Kinde
        this.kindeClientBuilder.grantType(AuthorizationType.CODE);
        this.kindeClientBuilder.domain(kindeOAuth2Properties.getDomain());
        this.kindeClientBuilder.redirectUri(kindeOAuth2Properties.getRedirectUri());
    }

    public KindeClient getClient() {
        return this.kindeClientBuilder.build();
    }

    public KindeClientBuilder getClientBuilder() {
        return this.kindeClientBuilder;
    }
}
