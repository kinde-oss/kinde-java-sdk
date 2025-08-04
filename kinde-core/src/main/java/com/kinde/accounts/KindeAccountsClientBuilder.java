package com.kinde.accounts;

import com.kinde.KindeClient;
import com.kinde.KindeClientSession;

/**
 * Builder for creating KindeAccountsClient instances.
 */
public class KindeAccountsClientBuilder {
    
    private KindeClient kindeClient;
    private KindeClientSession session;
    
    /**
     * Creates a new KindeAccountsClientBuilder.
     */
    public KindeAccountsClientBuilder() {
    }
    
    /**
     * Sets the KindeClient to use for authentication.
     * 
     * @param kindeClient The KindeClient instance
     * @return This builder instance
     */
    public KindeAccountsClientBuilder withKindeClient(KindeClient kindeClient) {
        this.kindeClient = kindeClient;
        return this;
    }
    
    /**
     * Sets the KindeClientSession to use for authentication.
     * 
     * @param session The KindeClientSession instance
     * @return This builder instance
     */
    public KindeAccountsClientBuilder withSession(KindeClientSession session) {
        this.session = session;
        return this;
    }
    
    /**
     * Builds a new KindeAccountsClient instance.
     * 
     * @return A new KindeAccountsClient instance
     * @throws IllegalStateException if neither KindeClient nor KindeClientSession is provided
     */
    public KindeAccountsClient build() {
        if (kindeClient != null) {
            return new KindeAccountsClient(kindeClient.clientSession());
        } else if (session != null) {
            return new KindeAccountsClient(session);
        } else {
            throw new IllegalStateException("Either KindeClient or KindeClientSession must be provided");
        }
    }
} 