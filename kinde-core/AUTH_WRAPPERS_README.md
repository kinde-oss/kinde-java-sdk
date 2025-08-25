# Kinde Auth Wrappers for Java SDK

## Overview

This document describes the new authentication wrapper classes that have been added to the Kinde Java SDK, following the same pattern as the Python SDK. These wrappers provide simplified access to claims, permissions, feature flags, roles, and entitlements functionality.

## Architecture

The auth wrappers are implemented as follows:

1. **BaseAuth**: Abstract base class that provides shared methods for accessing sessions and tokens
2. **Claims**: Wrapper for accessing token claims
3. **Permissions**: Wrapper for checking user permissions
4. **FeatureFlags**: Wrapper for accessing feature flags
5. **Roles**: Wrapper for checking user roles
6. **Entitlements**: Wrapper for accessing user entitlements
7. **Auth**: Main client that provides access to all wrapper classes

## Usage

### Basic Usage

```java
// Create the main Auth client
Auth auth = new Auth();

// Access different functionality through the wrapper classes
Claims claims = auth.claims();
Permissions permissions = auth.permissions();
FeatureFlags featureFlags = auth.featureFlags();
Roles roles = auth.roles();
Entitlements entitlements = auth.entitlements();
```

### Claims

The Claims wrapper provides access to token claims:

```java
Claims claims = auth.claims();

// Get a specific claim
Map<String, Object> userClaim = claims.getClaim("sub");
System.out.println("User claim: " + userClaim);

// Get all claims
Map<String, Object> allClaims = claims.getAllClaims();
System.out.println("All claims: " + allClaims);
```

### Permissions

The Permissions wrapper provides methods for checking user permissions:

```java
Permissions permissions = auth.permissions();

// Check if user has a specific permission
boolean hasPermission = permissions.hasPermission("create:todos");

// Check if user has any of multiple permissions
List<String> permissionKeys = Arrays.asList("create:todos", "read:todos", "update:todos");
boolean hasAnyPermission = permissions.hasAnyPermission(permissionKeys);

// Check if user has all permissions
boolean hasAllPermissions = permissions.hasAllPermissions(permissionKeys);

// Get all permissions
Map<String, Object> allPermissions = permissions.getPermissions();
```

### Feature Flags

The FeatureFlags wrapper provides access to feature flags:

```java
FeatureFlags featureFlags = auth.featureFlags();

// Check if a feature flag is enabled
boolean isEnabled = featureFlags.isFeatureFlagEnabled("new_ui");

// Get feature flag value
Object flagValue = featureFlags.getFeatureFlagValue("max_items");

// Get typed feature flag values
String stringFlag = featureFlags.getFeatureFlagString("theme");
Integer intFlag = featureFlags.getFeatureFlagInteger("max_items");

// Get all feature flags
Map<String, FeatureFlags.FeatureFlag> allFlags = featureFlags.getFeatureFlags();
```

### Roles

The Roles wrapper provides methods for checking user roles:

```java
Roles roles = auth.roles();

// Check if user has a specific role
boolean isAdmin = roles.hasRole("admin");

// Check if user has any of multiple roles
List<String> roleKeys = Arrays.asList("admin", "moderator", "user");
boolean hasAnyRole = roles.hasAnyRole(roleKeys);

// Check if user has all roles
boolean hasAllRoles = roles.hasAllRoles(roleKeys);

// Get all roles
Map<String, Object> allRoles = roles.getRoles();
```

### Entitlements

The Entitlements wrapper provides access to user entitlements from the Kinde Accounts API:

```java
Entitlements entitlements = auth.entitlements();

// Check if user has a specific entitlement
boolean hasPremium = entitlements.hasEntitlement("premium_features");

// Check if user has any of multiple entitlements
List<String> entitlementKeys = Arrays.asList("premium_features", "advanced_analytics");
boolean hasAnyEntitlement = entitlements.hasAnyEntitlement(entitlementKeys);

// Check if user has all entitlements
boolean hasAllEntitlements = entitlements.hasAllEntitlements(entitlementKeys);

// Get all entitlements
List<Map<String, Object>> allEntitlements = entitlements.getAllEntitlements();

// Get a specific entitlement with full details
Map<String, Object> entitlement = entitlements.getEntitlement("premium_features");
```

## Complex Access Control

You can combine multiple wrappers for complex access control scenarios:

```java
// Check if user can access a premium feature
boolean canAccessPremium = permissions.hasPermission("access:premium") && 
                          featureFlags.isFeatureFlagEnabled("premium_features") &&
                          entitlements.hasEntitlement("premium_features");

// Check if user is an admin or has specific permissions
boolean isAuthorized = roles.hasRole("admin") || 
                      permissions.hasAnyPermission(Arrays.asList("manage:users", "manage:system"));
```

## Integration with Existing SDK

The auth wrappers are designed to work alongside the existing Kinde Java SDK functionality. They provide a simplified interface for common authentication and authorization tasks while maintaining compatibility with the existing API.

### Session Management

The wrappers automatically access the current session through the Guice singleton pattern:

```java
// The wrappers automatically get the session from KindeGuiceSingleton
Auth auth = new Auth();
// No need to manually pass session - it's handled internally
```

### Token Access

The wrappers access tokens through the session:

```java
// Tokens are automatically retrieved from the current session
Claims claims = auth.claims();
Map<String, Object> userClaim = claims.getClaim("sub");
```

## Error Handling

The wrappers include comprehensive error handling:

- **Missing Tokens**: Returns appropriate default values when no token is available
- **Invalid Claims**: Handles missing or malformed claims gracefully
- **API Failures**: Logs errors and returns fallback values when API calls fail
- **Type Conversion**: Safely handles type conversion for feature flag values

## Logging

All wrapper classes include logging for debugging and monitoring:

```java
// Logging is automatically configured for each wrapper class
// Debug messages are logged for token access and API calls
// Warning messages are logged for errors and fallbacks
```

## Future Enhancements

The current implementation provides comprehensive wrapper functionality with full API integration. Future enhancements may include:

- **Caching**: Caching of frequently accessed data
- **Async Support**: Asynchronous versions of methods for better performance
- **Batch Operations**: Batch checking of multiple permissions, roles, or feature flags
- **Real-time Updates**: Real-time updates when permissions or roles change

## Migration from Python SDK

If you're migrating from the Python SDK, the Java wrappers follow the same patterns:

| Python SDK | Java SDK |
|------------|----------|
| `auth.claims.get_claim("sub")` | `auth.claims().getClaim("sub")` |
| `auth.permissions.get_permission("create:todos")` | `auth.permissions().hasPermission("create:todos")` |
| `auth.feature_flags.get_feature_flag("new_ui")` | `auth.featureFlags().isFeatureFlagEnabled("new_ui")` |
| `auth.roles.get_role("admin")` | `auth.roles().hasRole("admin")` |
| `auth.entitlements.get_entitlement("premium")` | `auth.entitlements().hasEntitlement("premium")` |

## Examples

See `AuthExample.java` for comprehensive examples of how to use all the wrapper classes.

## Dependencies

The auth wrappers depend on:
- `kinde-core`: Core functionality for authentication and session management
- `slf4j`: Logging framework
- Java 8+ for stream operations and Optional support
