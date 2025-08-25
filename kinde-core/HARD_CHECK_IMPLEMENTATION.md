# Hard Check Implementation for Kinde Java SDK

## Overview

This document describes the implementation of the "hard check" functionality in the Kinde Java SDK. The hard check system provides a fallback mechanism that automatically switches from token-based checks to API calls when information is not available in the JWT token.

## Problem Statement

When permissions, roles, or feature flags are not included in the JWT token (due to token size limitations or configuration), the system needs to fall back to the Kinde Accounts API to perform the necessary checks. This ensures that applications can always verify user access rights, even when token data is incomplete.

## Solution Architecture

### Core Components

1. **KindeTokenChecker**: Main utility class that implements the hard check logic
2. **KindeTokenCheckerBuilder**: Builder class for creating token checker instances
3. **KindeAccountsClient**: API client for making fallback calls to the Kinde Accounts API
4. **KindeToken**: Interface for accessing token data

### Flow Diagram

```
User Request → Check Token Data → Data Available? → Use Token Data
                                    ↓ No
                              Fallback to API → Make API Call → Return Result
```

## Implementation Details

### 1. KindeTokenChecker Class

The `KindeTokenChecker` class provides methods for checking permissions, roles, and feature flags with automatic API fallback:

#### Permission Checks
- `hasPermission(String permissionKey)`: Check if user has a specific permission
- `hasAnyPermission(List<String> permissionKeys)`: Check if user has any of the specified permissions
- `hasAllPermissions(List<String> permissionKeys)`: Check if user has all of the specified permissions

#### Role Checks
- `hasRole(String roleKey)`: Check if user has a specific role
- `hasAnyRole(List<String> roleKeys)`: Check if user has any of the specified roles
- `hasAllRoles(List<String> roleKeys)`: Check if user has all of the specified roles

#### Feature Flag Checks
- `isFeatureFlagEnabled(String flagKey)`: Check if a feature flag is enabled
- `getFeatureFlagValue(String flagKey)`: Get the value of a feature flag

#### Comprehensive Checks
- `hasAll(List<String> permissions, List<String> roles, List<String> featureFlags)`: Check if user has ALL requirements
- `hasAny(List<String> permissions, List<String> roles, List<String> featureFlags)`: Check if user has ANY requirements

### 2. Fallback Logic

Each check method follows this pattern:

1. **Check Token First**: Attempt to get data from the JWT token
2. **Evaluate Token Data**: If token data exists and is valid, use it
3. **Fallback to API**: If token data is missing or empty, make API call
4. **Error Handling**: Gracefully handle API failures and return appropriate defaults

### 3. Error Handling

The implementation includes comprehensive error handling:

- **API Failures**: Return `false` for permission/role checks, `null` for feature flag values
- **Network Issues**: Log errors and fall back to safe defaults
- **Invalid Data**: Handle malformed token data gracefully

## Usage Examples

### Basic Usage

```java
// Create a Kinde client and session
KindeClient client = KindeClientBuilder.builder()
    .domain("https://your-domain.kinde.com")
    .clientId("your-client-id")
    .clientSecret("your-client-secret")
    .build();

KindeClientSession session = client.clientSession();
KindeToken token = session.retrieveTokens().getAccessToken();

// Create a token checker
KindeTokenChecker checker = KindeTokenCheckerBuilder.builder()
    .token(token)
    .session(session)
    .build();

// Check permissions with automatic fallback
CompletableFuture<Boolean> hasPermission = checker.hasPermission("read:users");
hasPermission.thenAccept(hasAccess -> {
    if (hasAccess) {
        System.out.println("User has read:users permission");
    } else {
        System.out.println("User does not have read:users permission");
    }
});
```

### Advanced Usage

```java
// Comprehensive access control
CompletableFuture<Boolean> canAccessAdminPanel = checker.hasAll(
    Arrays.asList("read:admin", "write:admin"),  // Required permissions
    Arrays.asList("admin"),                      // Required roles
    Arrays.asList("admin_dashboard")             // Required feature flags
);

canAccessAdminPanel.thenAccept(hasAccess -> {
    if (hasAccess) {
        // Show admin panel
        showAdminPanel();
    } else {
        // Show access denied
        showAccessDenied();
    }
});
```

### Real-World Application Example

```java
public class AccessControlService {
    private final KindeTokenChecker checker;
    
    public AccessControlService(KindeToken token, KindeClientSession session) {
        this.checker = KindeTokenCheckerBuilder.builder()
            .token(token)
            .session(session)
            .build();
    }
    
    public CompletableFuture<Boolean> canManageUsers() {
        return checker.hasAll(
            Arrays.asList("read:users", "write:users", "delete:users"),
            Arrays.asList("admin", "moderator"),
            Arrays.asList("user_management")
        );
    }
    
    public CompletableFuture<Boolean> canViewAnalytics() {
        return checker.hasAny(
            Arrays.asList("read:analytics", "read:reports"),
            Arrays.asList("admin", "analyst"),
            Arrays.asList("analytics", "advanced_analytics")
        );
    }
    
    public CompletableFuture<Boolean> canAccessBetaFeatures() {
        return checker.isFeatureFlagEnabled("beta_features");
    }
}
```

## Performance Considerations

### Token-First Strategy
- **Fast Path**: When data is available in the token, checks are performed in-memory
- **No Network Calls**: Avoids unnecessary API requests when token data is sufficient
- **Reduced Latency**: Token-based checks are typically sub-millisecond

### API Fallback Strategy
- **Lazy Loading**: API calls are only made when token data is insufficient
- **Caching**: Consider implementing caching for frequently accessed data
- **Batch Operations**: For multiple checks, consider batching API calls

### Error Handling
- **Graceful Degradation**: API failures don't break the application
- **Default Values**: Sensible defaults when checks fail
- **Logging**: Comprehensive logging for debugging and monitoring

## Configuration

### Token Customization
To include permissions, roles, and feature flags in the token:

1. **Kinde Dashboard**: Configure token customization in your Kinde application settings
2. **Token Claims**: Add `permissions`, `roles`, and `feature_flags` to the token
3. **Size Limits**: Be mindful of JWT token size limitations

### API Configuration
The hard check system uses the existing `KindeAccountsClient` configuration:

- **Authentication**: Uses the session's access token for API calls
- **Base URL**: Automatically configured from the session's domain
- **Error Handling**: Inherits the client's error handling and retry logic

## Testing

### Unit Tests
The implementation includes comprehensive unit tests:

```java
@Test
void testHasPermission_WithTokenData() {
    // Test token-based permission check
}

@Test
void testHasPermission_WithoutTokenData_FallsBackToAPI() {
    // Test API fallback for permission check
}

@Test
void testErrorHandling_WhenAPIThrowsException() {
    // Test error handling
}
```

### Integration Tests
For full integration testing:

1. **Real Token**: Use actual JWT tokens with and without claims
2. **API Mocking**: Mock the Kinde Accounts API responses
3. **Error Scenarios**: Test network failures and API errors

## Migration Guide

### From Direct Token Access
**Before (Token-only)**:
```java
List<String> permissions = token.getPermissions();
boolean hasPermission = permissions.contains("read:users");
```

**After (Hard Check)**:
```java
KindeTokenChecker checker = KindeTokenCheckerBuilder.builder()
    .token(token)
    .session(session)
    .build();
CompletableFuture<Boolean> hasPermission = checker.hasPermission("read:users");
```

### From Direct API Calls
**Before (API-only)**:
```java
KindeAccountsClient client = new KindeAccountsClient(session);
CompletableFuture<Boolean> hasPermission = client.hasPermission("read:users");
```

**After (Hard Check)**:
```java
KindeTokenChecker checker = KindeTokenCheckerBuilder.builder()
    .token(token)
    .session(session)
    .build();
CompletableFuture<Boolean> hasPermission = checker.hasPermission("read:users");
```

## Best Practices

### 1. Use Builder Pattern
Always use the builder pattern for creating token checkers:

```java
// Good
KindeTokenChecker checker = KindeTokenCheckerBuilder.builder()
    .token(token)
    .session(session)
    .build();

// Avoid
KindeTokenChecker checker = new KindeTokenChecker(token, session);
```

### 2. Handle Async Results
Always handle the asynchronous nature of the checks:

```java
// Good
checker.hasPermission("read:users")
    .thenAccept(hasAccess -> {
        if (hasAccess) {
            // Grant access
        } else {
            // Deny access
        }
    })
    .exceptionally(throwable -> {
        // Handle errors
        return null;
    });

// Avoid
Boolean hasAccess = checker.hasPermission("read:users").get(); // Blocking call
```

### 3. Use Comprehensive Checks
For complex access control, use the comprehensive check methods:

```java
// Good - Single check for multiple requirements
checker.hasAll(permissions, roles, featureFlags);

// Avoid - Multiple separate checks
boolean hasPermissions = checker.hasAllPermissions(permissions).get();
boolean hasRoles = checker.hasAllRoles(roles).get();
boolean hasFlags = checker.isFeatureFlagEnabled("flag").get();
```

### 4. Implement Caching
For frequently accessed data, consider implementing caching:

```java
public class CachedTokenChecker {
    private final KindeTokenChecker checker;
    private final Map<String, CompletableFuture<Boolean>> permissionCache = new ConcurrentHashMap<>();
    
    public CompletableFuture<Boolean> hasPermission(String permission) {
        return permissionCache.computeIfAbsent(permission, 
            key -> checker.hasPermission(key));
    }
}
```

## Troubleshooting

### Common Issues

1. **NullPointerException**: Ensure both token and session are provided to the builder
2. **API Failures**: Check network connectivity and API credentials
3. **Token Expiration**: Ensure the session has a valid access token
4. **Permission Denied**: Verify the application has the necessary API permissions

### Debugging

Enable debug logging to trace the hard check flow:

```java
// Add to application.properties or logback.xml
logging.level.com.kinde.token=DEBUG
```

### Monitoring

Monitor the following metrics:

- **Token Hit Rate**: Percentage of checks resolved from token data
- **API Call Rate**: Number of fallback API calls per minute
- **Error Rate**: Percentage of failed checks
- **Response Time**: Average time for permission/role checks

## Future Enhancements

### Planned Features

1. **Caching Layer**: Built-in caching for API responses
2. **Batch Operations**: Optimized batch checking for multiple permissions/roles
3. **Real-time Updates**: WebSocket-based real-time permission updates
4. **Advanced Conditions**: Support for complex permission conditions
5. **Metrics Integration**: Built-in metrics and monitoring

### Extension Points

The hard check system is designed to be extensible:

- **Custom Checkers**: Implement custom logic for specific use cases
- **Plugin System**: Add custom fallback strategies
- **Condition Engine**: Support for complex permission conditions

## Conclusion

The hard check implementation provides a robust, performant solution for access control in the Kinde Java SDK. By automatically falling back to API calls when token data is insufficient, it ensures that applications can always verify user permissions, roles, and feature flags while maintaining optimal performance.

The implementation follows Java best practices, provides comprehensive error handling, and includes extensive documentation and examples to help developers integrate it into their applications effectively.
