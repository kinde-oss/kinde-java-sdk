# Spring Boot SDK Library

Welcome to the Spring Boot SDK Library!

## Overview

This project consists of a Spring Boot core which is responsible for configuring a Spring Boot project use Kinde PKCE authentication. This is used in conjunction with the Kinde-Springboot-Starter project which wraps the core and bootstraps spring.

## PKCE Authentication

PKCE authentication is a crucial part of securing OAuth 2.0 flows, and our library currently provides robust support for it. If you need to implement PKCE authentication in your Spring Boot application, this library is here to help.

## Features

- **Authentication**: This library configures spring to use Kinde for authentication.
- **Authorization**: This library configures spring to use Kinde for authorization.
- **Feature Flags**: This library provides access to the Kinde feature flags.

## Sub Projects

| Project                  | URL                                                       |
| ------------------------ | --------------------------------------------------------- |
| kinde-springboot-core    | [kinde-springboot-core](./springboot-pkce-client-example) |
| kinde-sprinbboot-starter | [kinde-sprinbboot-starter](./kinde-sprinbboot-starter)    |
