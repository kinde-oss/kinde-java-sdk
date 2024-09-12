# Spring Boot SDK Library

Welcome to the Spring Boot SDK Library!

## Overview

This project consists of a Spring Boot core responsible for configuring a Spring Boot project to use Kinde PKCE authentication, alongside the Kinde-Springboot-Starter project, which wraps the core and bootstraps spring.

## PKCE Authentication

PKCE authentication is a crucial part of securing OAuth 2.0 flows, and our library currently provides robust support for it. If you need to implement PKCE authentication in your Spring Boot application, this library is here to help.

## Features

- **Authentication**: This library configures spring to use Kinde for authentication.
- **Authorization**: This library configures spring to use Kinde for authorization.
- **Feature Flags**: This library provides access to the Kinde feature flags.

## Sub Projects

| Project                  | URL                                                    |
| ------------------------ |--------------------------------------------------------|
| kinde-springboot-core    | [kinde-springboot-core](./kinde-springboot-core)       |
| kinde-springboot-starter | [kinde-springboot-starter](./kinde-springboot-starter) |
