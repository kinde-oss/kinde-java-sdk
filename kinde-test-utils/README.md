# Kinde Test Utils

This module contains shared test utilities and classes that are used across multiple modules in the Kinde Java SDK to eliminate code duplication.

## Purpose

The main goal of this module is to consolidate common test classes, particularly `KindeTokenGuiceTestModule`, which was previously duplicated across 8 different modules.

## Contents

### KindeTokenGuiceTestModule

A Guice module that provides test implementations for token-related classes. It supports two variants:

1. **Simple variant** (default constructor): Only binds `TestTokenGenerator`
2. **Extended variant** (with key generator): Binds both `TestTokenGenerator` and `TestKeyGenerator`

#### Usage

```java
// Simple variant - only token generator
KindeTokenGuiceTestModule module = new KindeTokenGuiceTestModule();

// Extended variant - includes key generator
KindeTokenGuiceTestModule module = new KindeTokenGuiceTestModule(true);
```

## Migration Guide

To use this shared module in your tests:

1. Add the test-utils dependency to your module's `pom.xml`:

```xml
<dependency>
    <groupId>com.kinde</groupId>
    <artifactId>kinde-test-utils</artifactId>
    <version>${project.version}</version>
    <scope>test</scope>
    <type>test-jar</type>
</dependency>
```

2. Update your imports to use the shared module:

```java
import com.kinde.token.KindeTokenGuiceTestModule;
```

3. Remove the local duplicate of `KindeTokenGuiceTestModule` from your module.

## Benefits

- **Eliminates duplication**: Single source of truth for test modules
- **Easier maintenance**: Changes only need to be made in one place
- **Consistent behavior**: All modules use the same test implementations
- **Better organization**: Clear separation between production and test utilities
