# Kinde-SpringBoot-JWT-Example

This project demonstrates how to use Spring Security and spring-boot-starter-oauth2-resource-server for authentication
with Spring Boot. Kinde is used as a OAuth2 Provider

## Setup

Configure your application by editing `src/main/resources/application.properties`:

```
spring.security.oauth2.resourceserver.jwt.issuer-uri=https://<your-kinde-domain>.kinde.com
```

Replace `<your-kinde-domain>` with your actual Kinde domain (e.g., `myapp.kinde`).

## Running the Application

You can run the application using your IDE or with Maven:

```shell
./mvnw spring-boot:run
```
