# Kinde Core

## Development

This is the Kinde Core Library, and contains the core components needed to manage a client connection to the Kinde OIDC end points.

## Project Dependancies

### Maven
In order to use this SDK include following POM dependancy.
```xml
    <dependency>
      <groupId>com.kinde</groupId>
      <artifactId>kinde-core</artifactId>
      <version>2.0.0</version>
    </dependency>
```
### Gradle
In order to use the SDK with a Gradle build process please use the following dependancy.
```groovy
    configuration('com.kinde:kinde-core:2.0.0')
```

## Building the SDK from Source

1. Clone the repository to your machine:

   ```bash
   git clone https://github.com/kinde-oss/kinde-java-sdk
   ```

2. Go into the project:

   ```bash
   cd kinde-java-sdk
   ```

3. Install the dependencies:

   ```bash
   mvn clean install
   ```

## Documentation

For details on integrating this SDK into your project, head over to the [Kinde docs](https://kinde.com/docs/) and see the [Java SDK](<[link-to-kinde-doc](https://kinde.com/docs/developer-tools/)>) doc 👍🏼.

Maven will automatically download the dependency from your local repository and make it available in your project.

### Library Usage

#### Setup the following environment variables
The following basic environmental variables are required at a mimimum for connecting to Kinde. This will enable the development of a M2M client service.
```shell
export KINDE_DOMAIN=https://<replace>.kinde.com # This is the domain you setup at kinde
export KINDE_CLIENT_ID=<replace> # the id for the client connecting to Kinde
export KINDE_CLIENT_SECRET=<replace> # the secret used to authenticate the client against Kinde
```
If a user login is to be validated against Kinde a redirect uri must be provided.
```shell
export KINDE_REDIRECT_URI=http://localhost:8080/kinde-j2ee-app/login
```
The redirect URI/URL used post successfull login. It is the URL that the PKCE client CODE will be set to. A query parameter of ?code='value' must be processed

#### Setup .env environmental files.
The Kinde library supports .env files. The must be located in the directory from which the application is executed.
```shell
KINDE_DOMAIN=https://<replace>.kinde.com
KINDE_CLIENT_ID=<replace>
KINDE_CLIENT_SECRET=<replace>
KINDE_REDIRECT_URI=http://localhost:8080/kinde-j2ee-app/login
```

#### Server Example
In order to make a M2M server token request onto Kinde first setup the appropriate environmental variables
##### By shell export
Run these exports before running your service.
```shell
export KINDE_DOMAIN=https://<replace>.kinde.com # This is the domain you setup at kinde
export KINDE_CLIENT_ID=<replace> # the id for the client connecting to Kinde
export KINDE_CLIENT_SECRET=<replace> # the secret used to authenticate the client against Kinde
```
##### By .env file config
Place this .env file in the directory from which you run your service.
```shell
KINDE_DOMAIN=https://<replace>.kinde.com
KINDE_CLIENT_ID=<replace>
KINDE_CLIENT_SECRET=<replace>
```
##### Programmatic configuration
If you want to pass in configuration programmatically the KindeClientBuilder supports this use the following approach.
```java
KindeClient kindeClient = KindeClientBuilder
        .builder()
        .domain("<replace>")
        .clientId("<replace>")
        .clientSecret("<replace>")
        .build();
```

##### Java Code to retrieve a M2M token.
The example below details how to implement a server level token request. This is needed for M2M communication and authorization.
```java
KindeClient kindeClient = KindeClientBuilder
        .builder()
        .build();
KindeClientSession kindeClientSession = kindeClient.clientSession();
KindTokens tokens = kindeClientSession.retrieveTokens();
        
```

### User Code Authorization Example (PKCE)
Inorder to authenticate a user on a client the appropriate configuration must be in place.

##### By shell export
Run these exports before running your service.
```shell
export KINDE_DOMAIN=https://<replace>.kinde.com # This is the domain you setup at kinde
export KINDE_CLIENT_ID=<replace> # the id for the client connecting to Kinde
export KINDE_CLIENT_SECRET=<replace> # the secret used to authenticate the client against Kinde
export KINDE_REDIRECT_URI=openid # the open id
```
##### By .env file config
Place this .env file in the directory from which you run your service.
```shell
KINDE_DOMAIN=https://<replace>.kinde.com
KINDE_CLIENT_ID=<replace>
KINDE_CLIENT_SECRET=<replace>
KINDE_REDIRECT_URI=<replace>
KINDE_SCOPES=openid
```
##### Programmatic configuration
If you want to pass in configuration programmatically the KindeClientBuilder supports this use the following approach.
```java
KindeClient kindeClient = KindeClientBuilder
        .builder()
        .domain("<replace>")
        .clientId("<replace>")
        .clientSecret("<replace>")
        .redirectUri("replace")
        .addScope("openid")
        .build();
```

##### Java code to generate the redirect URL
Before the PKCE code can be processed a user must be direct to Kinde to login. The client library can generate this URL as follows
```java
KindeClient kindeClient = KindeClientBuilder
        .builder()
        .build();
KindeClientSession kindeClientSession = kindeClient.clientSession();
AuthorizationUrl authorizationURL = kindeClientSession.authorizationUrl();
```
The AuthorizationUrl contains the url and CodeVerify information. If using a code grant the code verify needs to be stored for the redirct call. This can be done using the J2EE session. Here is an example
```java
req.getSession().setAttribute("AuthorizationUrl",authorizationUrl);
resp.sendRedirect(authorizationUrl.getUrl().toString());
```

##### Code to request tokens upon redirect
If it is a code auth then the AuthorizationUrl needs to be retrieved.
```java
AuthorizationUrl authorizationUrl = (AuthorizationUrl)req.getSession().getAttribute("AuthorizationUrl");
```
The token request looks like the following.
```java
KindeClient kindeClient = KindeClientBuilder
        .builder()
        .build();
KindeTokens tokens = kindeClient.getKindeClient().initClientSession(code,authorizationUrl).retrieveTokens();
```

## License

By contributing to Kinde, you agree that your contributions will be licensed under its MIT License.
