# Spring Boot SDK Library

Welcome to the Spring Boot SDK Library!

## Overview

This project consists of a springboot core which is responsible for configuring a spring boot project use Kinde PKCE authentication. This is used in conjunction with the Kinde-Springboot-Starer project which wraps the core and bootstraps spring.

## PKCE Authentication

PKCE authentication is a crucial part of securing OAuth 2.0 flows, and our library currently provides robust support for it. If you need to implement PKCE authentication in your Spring Boot application, this library is here to help.

## Features

- **Authentication**: This library configures spring to use Kinde for authentication.
- **Authorization**: This library configures spring to use Kinde for authorization.
- **Feature Flags**: This library provides access to the Kinde feature flags.

## Examples

Please take a look at our spring boot examples. We provide two stand alone projects. These detail how to use Kinde for authentication and authorization without the inclusion of any Kinde specific libraries. And we include an example of the Kinde Spring Boot Starer in action.

| Project                  | URL                                                          |
|--------------------------|--------------------------------------------------------------|
| PKCE stand alone example | [PKCE Example](../playground/springboot-pkce-client-example) |
| Full stand alone example | [PKCE Example](../playground/springboot-pkce-client-example) |

Stay tuned for more updates!