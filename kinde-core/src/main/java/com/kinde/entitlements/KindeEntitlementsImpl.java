package com.kinde.entitlements;

import com.google.inject.Inject;
import com.kinde.KindeClientSession;
import com.kinde.accounts.KindeAccountsClient;
import com.kinde.accounts.dto.EntitlementDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Implementation of KindeEntitlements that uses the KindeAccountsClient
 * to access entitlements functionality from the Kinde Accounts API.
 * This implementation follows the Guice dependency injection pattern and provides clean DTOs.
 */
public class KindeEntitlementsImpl implements KindeEntitlements {

    private static final Logger log = LoggerFactory.getLogger(KindeEntitlementsImpl.class);
    private final KindeAccountsClient accountsClient;

    /**
     * Primary constructor for Guice dependency injection.
     * 
     * @param accountsClient The KindeAccountsClient to use for API calls
     */
    @Inject
    public KindeEntitlementsImpl(KindeAccountsClient accountsClient) {
        if (accountsClient == null) {
            throw new IllegalArgumentException("KindeAccountsClient cannot be null");
        }
        this.accountsClient = accountsClient;
    }

    /**
     * Constructor for testing purposes.
     * 
     * @param session The KindeClientSession to use for creating the accounts client
     */
    public KindeEntitlementsImpl(KindeClientSession session) {
        if (session == null) {
            throw new IllegalArgumentException("KindeClientSession cannot be null");
        }
        this.accountsClient = new KindeAccountsClient(session, true); // Use backward compatibility constructor
    }

    @Override
    public List<EntitlementDto> getEntitlements() {
        log.debug("Getting all entitlements");
        return accountsClient.getEntitlements();
    }

    @Override
    public EntitlementDto getEntitlement(String key) {
        log.debug("Getting entitlement with key: {}", key);
        return accountsClient.getEntitlement(key);
    }
} 