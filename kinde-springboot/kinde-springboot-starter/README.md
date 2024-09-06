# Kinde Springboot Starter

The spring boot starter project. This project manages all the dependencies required by a spring boot application connecting to Kinde.
## Project Dependancies

### Maven
In order to use configure Spring Boot to use Kinde for authentication include the following dependency.
```xml
    <dependency>
      <groupId>com.kinde.spring</groupId>
      <artifactId>kinde-springboot-starter</artifactId>
      <version>2.0.0</version>
    </dependency>
```
### Gradle
In order to use the SDK with a Gradle build process please use the following dependancy.
```groovy
    configuration('com.kinde.spring:kinde-springboot-starter:2.0.0')
```
## Usage
This library can be configured in different ways, via environmental variables, via .env file and via the spring boot application.yaml file.

### Environmetal configuration
```shell
export KINDE_DOMAIN=https://<replace>.kinde.com # This is the domain you setup at kinde
export KINDE_CLIENT_ID=<replace> # the id for the client connecting to Kinde
export KINDE_CLIENT_SECRET=<replace> # the secret used to authenticate the client against Kinde
export KINDE_REDIRECT_URI=http://localhost:< replace with port of application server >/kinde-j2ee-app/login
export KINDE_GRANT_TYPE=CODE
export KINDE_SCOPES=profile,email,openid
```

### `.env` 
```shell
KINDE_DOMAIN=https://<replace>.kinde.com # This is the domain you setup at kinde
KINDE_CLIENT_ID=<replace> # the id for the client connecting to Kinde
KINDE_CLIENT_SECRET=<replace> # the secret used to authenticate the client against Kinde
KINDE_REDIRECT_URI=http://localhost:< replace with port of application server >/kinde-j2ee-app/login
KINDE_GRANT_TYPE=CODE
KINDE_SCOPES=profile,email,openid
```

### `application.yaml`
```yaml
kinde:
  oauth2:
    domain: https://< replace >.kinde.com
    client-id: < replace >
    client-secret: < replace >
    scopes: openid,email,profile
```
