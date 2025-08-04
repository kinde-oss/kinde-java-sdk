package com.kinde.entitlements;

import com.kinde.KindeClientSession;
import com.kinde.accounts.KindeAccountsClient;
import org.openapitools.client.model.EntitlementResponse;
import org.openapitools.client.model.EntitlementsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

/**
 * Implementation of KindeEntitlements that uses the KindeAccountsClient
 * to access entitlements functionality from the Kinde Accounts API.
 */
public class KindeEntitlementsImpl implements KindeEntitlements {

    private static final Logger log = LoggerFactory.getLogger(KindeEntitlementsImpl.class);

        private final KindeAccountsClient accountsClient;

    /**
     * Creates a new KindeEntitlementsImpl using the provided KindeClientSession.
     *
     * @param session The KindeClientSession instance to use for authentication
     */
    public KindeEntitlementsImpl(KindeClientSession session) {
        this.accountsClient = new KindeAccountsClient(session);
    }

    @Override
    public CompletableFuture<EntitlementsResponse> getEntitlements() {
        log.debug("Getting all entitlements");
        return accountsClient.getEntitlements();
    }

    @Override
    public CompletableFuture<EntitlementResponse> getEntitlement(String key) {
        log.debug("Getting entitlement with key: {}", key);
        return accountsClient.getEntitlement(key);
    }
} 