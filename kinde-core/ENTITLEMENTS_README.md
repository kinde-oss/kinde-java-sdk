# Entitlements Functionality in Kinde Core

This document describes the entitlements functionality that has been integrated into the Kinde Core library.

## Overview

The entitlements functionality allows you to access user entitlements from the Kinde Accounts API. This provides access to information that is too large to come from the token, or to perform real-time checks when permissions are not in the token.

## Architecture

The entitlements functionality is implemented as follows:

1. **KindeEntitlements Interface**: Defines the contract for entitlements operations
2. **KindeEntitlementsImpl**: Implementation that uses the KindeAccountsClient
3. **Integration with KindeClientSession**: Entitlements are accessible through the session

## Usage

### Basic Usage

```java
// Create a Kinde client
KindeClient client = KindeClientBuilder.builder()
    .domain("https://your-domain.kinde.com")
    .clientId("your-client-id")
    .clientSecret("your-client-secret")
    .redirectUri("http://localhost:8080/callback")
    .build();

// Get a client session (M2M or user session)
KindeClientSession session = client.clientSession();

// Access entitlements functionality
KindeEntitlements entitlements = session.entitlements();

// Get all entitlements
CompletableFuture<EntitlementsResponse> allEntitlements = entitlements.getEntitlements();
allEntitlements.thenAccept(result -> {
    System.out.println("All entitlements: " + result);
});

// Get a specific entitlement
CompletableFuture<EntitlementResponse> specificEntitlement = entitlements.getEntitlement("premium-feature");
specificEntitlement.thenAccept(result -> {
    System.out.println("Premium feature entitlement: " + result);
});
```

### API Methods

#### `getEntitlements()`
Returns all entitlements for the current user's organization.

**Returns**: `CompletableFuture<EntitlementsResponse>`

#### `getEntitlement(String key)`
Returns a specific entitlement by key.

**Parameters**:
- `key` (String): The entitlement key to retrieve

**Returns**: `CompletableFuture<EntitlementResponse>`

## Dependencies

The entitlements functionality depends on:
- `kinde-accounts`: Provides the API client for accessing the Kinde Accounts API
- `kinde-core`: Core functionality for authentication and session management

## Implementation Details

### KindeEntitlements Interface

```java
public interface KindeEntitlements {
    CompletableFuture<EntitlementsResponse> getEntitlements();
    CompletableFuture<EntitlementResponse> getEntitlement(String key);
}
```

### KindeEntitlementsImpl

The implementation uses the `KindeAccountsClient` to make API calls to the Kinde Accounts API. It requires a `KindeClientSession` for authentication.

### Session Integration

The `KindeClientSession` interface has been extended with:
- `entitlements()`: Returns a `KindeEntitlements` instance
- `getDomain()`: Returns the domain for API calls
- `getAccessToken()`: Returns the access token for authentication

## Error Handling

The entitlements methods return `CompletableFuture` objects that may complete exceptionally. Common error scenarios include:
- Network connectivity issues
- Authentication failures
- Invalid entitlement keys
- API rate limiting

## Future Enhancements

The current implementation provides basic entitlements functionality. Future enhancements may include:
- Caching of entitlements
- Batch operations for multiple entitlements
- Real-time entitlements updates
- Integration with other Kinde API features

## Notes

- The entitlements functionality requires a valid session with an access token
- API calls are made asynchronously using `CompletableFuture`
- The implementation automatically handles authentication using the session's access token
- Entitlements are scoped to the current user's organization 