# Kinde Accounts API Example

This example demonstrates how to use the Kinde Accounts API to query user entitlements, permissions, roles, and feature flags.

## Overview

The Kinde Accounts API allows front-end applications to query the current user's permissions and entitlements. This example shows how to:

- Retrieve user entitlements and billing information
- Check user permissions and access rights
- Retrieve user roles and role-based access control
- Access feature flags and their values
- Get user profile and organization information

## Prerequisites

1. A Kinde account with a configured application
2. Your Kinde domain, client ID, and client secret
3. Java 17 or higher
4. Maven

## Configuration

Before running the example, update the configuration in `AccountsExample.java`:

```java
String domain = "your-domain.kinde.com";
String clientId = "your-client-id";
String clientSecret = "your-client-secret";
String redirectUrl = "http://localhost:8080/callback";
```

Replace these values with your actual Kinde application credentials.

## Running the Example

1. Build the project:
   ```bash
   mvn clean compile
   ```

2. Run the example:
   ```bash
   mvn exec:java -Dexec.mainClass="com.kinde.accounts.example.AccountsExample"
   ```

## What the Example Demonstrates

### Entitlements
- Retrieves all entitlements for the current user's organization
- Displays entitlement details including feature keys, pricing, and limits
- Shows how to get a specific entitlement by key

### Permissions
- Lists all permissions assigned to the current user
- Demonstrates checking for specific permissions
- Shows how to check if a user has any or all of a set of permissions

### Roles
- Lists all roles assigned to the current user
- Demonstrates checking for specific roles
- Shows how to check if a user has any or all of a set of roles

### Feature Flags
- Retrieves all feature flags available to the current user
- Shows how to get specific feature flag values
- Demonstrates checking if boolean feature flags are enabled

### User Information
- Retrieves the current user's profile information
- Lists all organizations the user belongs to
- Shows the current organization context

## Expected Output

The example will output information about the current user's entitlements, permissions, roles, and feature flags. The exact output will depend on your Kinde application configuration and the current user's permissions.

Example output:
```
=== Entitlements Example ===
Organization Code: org_abc123
Number of entitlements: 3
  - Premium Features (premium_features)
    Price: Premium Plan, Unit Amount: 29.99
    Limits: 1 - 100

=== Permissions Example ===
Number of permissions: 5
  - read:users (perm_123)
    Description: Can read user information
Has permission 'read:users': true
Has permission 'write:users': false
Has permission 'admin:access': false
Has any of the permissions: true
Has all permissions: false

=== Roles Example ===
Number of roles: 2
  - user (role_456)
    Description: Standard user role
  - moderator (role_789)
    Description: Moderator role
Has role 'admin': false
Has role 'user': true
Has role 'moderator': true
Has any of the roles: true
Has all roles: false

=== Feature Flags Example ===
Number of feature flags: 2
  - New UI (new-ui)
    Type: boolean, Value: true
  - Beta Features (beta-features)
    Type: string, Value: enabled
Feature flag 'new-ui' value: true
Feature flag 'new-ui' enabled: true
Feature flag 'beta-features' value: enabled
Feature flag 'beta-features' enabled: false

=== User Information Example ===
User Profile:
  ID: user_123
  Email: user@example.com
  Name: John Doe
User Organizations:
  Number of organizations: 1
  - Example Corp (org_abc123)
Current Organization:
  Name: Example Corp
  Code: org_abc123
```

## Error Handling

The example includes basic error handling for API calls. If there are issues with authentication or API access, appropriate error messages will be displayed.

## Integration Notes

This example demonstrates the core functionality of the Kinde Accounts API. In a real application, you would typically:

1. Integrate this with your authentication flow
2. Cache results appropriately
3. Handle errors more gracefully
4. Use the information to control application features and access

## Dependencies

- `kinde-core` - Core Kinde SDK functionality
- `kinde-accounts` - Accounts API client
- JUnit for testing (optional)

## Troubleshooting

If you encounter issues:

1. Verify your Kinde application credentials are correct
2. Ensure your application has the necessary scopes configured
3. Check that the user has appropriate permissions
4. Verify network connectivity to your Kinde domain 