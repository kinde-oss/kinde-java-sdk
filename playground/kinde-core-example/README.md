# Kinde-Core-Example
This project uses the Junit framework to provide an example on how to use the Kinde-Core.

## Setup

The unit tests are configured via a .env file. This file is is found in the base of this project.

```shell
KINDE_DOMAIN=https://< replace >.kinde.com
KINDE_CLIENT_ID=< replace >
KINDE_CLIENT_SECRET=< replace >
KINDE_REDIRECT_URI=http://localhost:8080/kinde-j2ee-app/login
KINDE_GRANT_TYPE=CODE
KINDE_SCOPES=openid
```

Replace the tags with the appropriate configuration.

## Execution

The tests can be executed either via your IDE or through maven.

```shell
mvn -Dtest=KindeCoreExampleTest test
```