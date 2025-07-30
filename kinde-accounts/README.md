# Kinde Accounts API

This module provides Java SDK functionality for accessing the Kinde Accounts API, which allows querying the current user's permissions, roles, entitlements, and feature flags.

## Overview

The Kinde Accounts API is separate from the Management API and is designed for front-end applications to query the current user's permissions and entitlements. This module provides a clean Java wrapper around the generated OpenAPI client.

## Features

- **Entitlements**: Query user entitlements and billing information
- **Permissions**: Check user permissions and access rights
- **Roles**: Retrieve user roles and role-based access control
- **Feature Flags**: Access feature flags and their values
- **User Profile**: Get current user profile information
- **Organizations**: Retrieve user organization information

## API Endpoints

The following endpoints are available through the Accounts API:

- `GET /account_api/v1/entitlements` - Get all entitlements
- `GET /account_api/v1/entitlement/{key}` - Get specific entitlement
- `GET /account_api/v1/permissions` - Get all permissions
- `GET /account_api/v1/permission/{key}` - Get specific permission
- `GET /account_api/v1/roles` - Get all roles
- `GET /account_api/v1/feature_flags` - Get all feature flags
- `GET /account_api/v1/feature_flags/{key}` - Get specific feature flag
- `GET /account_api/v1/user_organizations` - Get user organizations
- `GET /account_api/v1/user_profile` - Get user profile
- `GET /account_api/v1/current_organization` - Get current organization

## Usage

### Creating an Accounts Client

```java
// Using KindeClient
KindeClient kindeClient = new KindeClientBuilder()
    .withDomain("your-domain.kinde.com")
    .withClientId("your-client-id")
    .build();

KindeAccountsClient accountsClient = new KindeAccountsClient(kindeClient);

// Or using the builder pattern
KindeAccountsClient accountsClient = new KindeAccountsClientBuilder()
    .withKindeClient(kindeClient)
    .build();
```

### Getting Entitlements

```java
// Get all entitlements
CompletableFuture<EntitlementsResponse> entitlementsFuture = accountsClient.getEntitlements();
EntitlementsResponse response = entitlementsFuture.get();

// Get specific entitlement
CompletableFuture<EntitlementResponse> entitlementFuture = accountsClient.getEntitlement("feature-key");
EntitlementResponse entitlement = entitlementFuture.get();
```

### Checking Permissions

```java
// Check if user has a specific permission
CompletableFuture<Boolean> hasPermission = accountsClient.hasPermission("read:users");
boolean canReadUsers = hasPermission.get();

// Check if user has any of multiple permissions
List<String> permissions = Arrays.asList("read:users", "write:users");
CompletableFuture<Boolean> hasAnyPermission = accountsClient.hasAnyPermission(permissions);
boolean hasAny = hasAnyPermission.get();

// Check if user has all permissions
CompletableFuture<Boolean> hasAllPermissions = accountsClient.hasAllPermissions(permissions);
boolean hasAll = hasAllPermissions.get();
```

### Checking Roles

```java
// Check if user has a specific role
CompletableFuture<Boolean> hasRole = accountsClient.hasRole("admin");
boolean isAdmin = hasRole.get();

// Check if user has any of multiple roles
List<String> roles = Arrays.asList("admin", "moderator");
CompletableFuture<Boolean> hasAnyRole = accountsClient.hasAnyRole(roles);
boolean hasAnyRole = hasAnyRole.get();
```

### Working with Feature Flags

```java
// Get all feature flags
CompletableFuture<FeatureFlagsResponse> flagsFuture = accountsClient.getFeatureFlags();
FeatureFlagsResponse flags = flagsFuture.get();

// Get specific feature flag value
CompletableFuture<Object> flagValue = accountsClient.getFeatureFlagValue("new-ui");
Object value = flagValue.get();

// Check if feature flag is enabled
CompletableFuture<Boolean> isEnabled = accountsClient.isFeatureFlagEnabled("new-ui");
boolean enabled = isEnabled.get();
```

### Getting User Information

```java
// Get user profile
CompletableFuture<UserProfileResponse> profileFuture = accountsClient.getUserProfile();
UserProfileResponse profile = profileFuture.get();

// Get user organizations
CompletableFuture<UserOrganizationsResponse> orgsFuture = accountsClient.getUserOrganizations();
UserOrganizationsResponse organizations = orgsFuture.get();

// Get current organization
CompletableFuture<CurrentOrganizationResponse> currentOrgFuture = accountsClient.getCurrentOrganization();
CurrentOrganizationResponse currentOrg = currentOrgFuture.get();
```

## Data Models

### Entitlement

```java
public class Entitlement {
    private String id;
    private Double fixedCharge;
    private String priceName;
    private Double unitAmount;
    private String featureKey;
    private String featureName;
    private Integer entitlementLimitMax;
    private Integer entitlementLimitMin;
    // getters and setters
}
```

### Permission

```java
public class Permission {
    private String id;
    private String name;
    private String description;
    // getters and setters
}
```

### Role

```java
public class Role {
    private String id;
    private String name;
    private String description;
    // getters and setters
}
```

### Feature Flag

```java
public class FeatureFlag {
    private String id;
    private String name;
    private String key;
    private String type;
    private Object value; // Can be String, Boolean, Number, or Object
    // getters and setters
}
```

## Error Handling

The client methods return `CompletableFuture` objects that may throw exceptions:

```java
try {
    CompletableFuture<EntitlementsResponse> future = accountsClient.getEntitlements();
    EntitlementsResponse response = future.get();
} catch (Exception e) {
    // Handle API errors, network issues, etc.
    System.err.println("Failed to get entitlements: " + e.getMessage());
}
```

## Dependencies

This module depends on:
- `kinde-core` - Core Kinde SDK functionality
- OpenAPI generated client code
- Jackson for JSON processing
- Jersey for HTTP client functionality

## Building

To build this module:

```bash
mvn clean compile
```

Note: There may be Java version compatibility issues with Java 24. The module is designed to work with Java 17+.

## Integration

This module is integrated into the main Kinde Java SDK and can be used alongside the existing `kinde-core` and `kinde-management` modules.

## API Specification

The OpenAPI specification for the Accounts API is defined in `kinde-accounts-api.yaml` at the project root. This specification is used to generate the client code automatically. 