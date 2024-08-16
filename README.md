# Kinde Java SDK

The Kinde SDK for Java.

[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat-square)](https://makeapullrequest.com) [![Kinde Docs](https://img.shields.io/badge/Kinde-Docs-eee?style=flat-square)](https://kinde.com/docs/developer-tools) [![Kinde Community](https://img.shields.io/badge/Kinde-Community-eee?style=flat-square)](https://thekindecommunity.slack.com)

## Development

This project contains an SDK package with a class called KindClientSDK. Inside this class, you will find all the main functions and methods that you need to use in another project. Any changes related to the SDK should be made through this file.

Additionally, within the SDK package, there is a callback controller class where we have implemented a method for handling callback requests. If you need to make any changes to the callback request processing, you should do so in this file.

Furthermore, the SDK package includes sub-packages for enums, OAuth2, storage, and utilities. In the storage package, you will find functionality related to cookie storage, and within the OAuth2 package, we have classes for handling authorization code, client credentials, and PKCE (Proof Key for Code Exchange).

These components collectively make up the SDK, and you should make any necessary modifications or enhancements within the respective files to maintain and extend the SDK's functionality.

### Project Dependancies

#### Maven
In order to use this SDK include following POM dependancy.
```xml
    <dependency>
      <groupId>com.kinde</groupId>
      <artifactId>kinde-core</artifactId>
      <version>2.0.0</version>
    </dependency>
```
#### Gradle
In order to use the SDK with a Gradle build process please use the following dependancy.
```groovy
    configuration('com.kinde:kinde-core:2.0.0')
```

### Building the SDK from Source

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
List<KindeToken> tokens = kindeClientSession.retrieveTokens();
        
```

### User Code Authorization Example (PKCE)
Inorder to authenticate a user on a client the appropriate configuration must be in place.

##### By shell export
Run these exports before running your service.
```shell
export KINDE_DOMAIN=https://<replace>.kinde.com # This is the domain you setup at kinde
export KINDE_CLIENT_ID=<replace> # the id for the client connecting to Kinde
export KINDE_CLIENT_SECRET=<replace> # the secret used to authenticate the client against Kinde
export KINDE_REDIRECT_URI=<replace> # the redirect URL
```
##### By .env file config
Place this .env file in the directory from which you run your service.
```shell
KINDE_DOMAIN=https://<replace>.kinde.com
KINDE_CLIENT_ID=<replace>
KINDE_CLIENT_SECRET=<replace>
KINDE_REDIRECT_URI=<replace>
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
        .build();
```

##### Java code to generate the redirect URL
Before the PKCE code can be processed a user must be direct to Kinde to login. The client library can generate this URL as follows
```java
KindeClient kindeClient = KindeClientBuilder
        .builder()
        .build();
KindeClientSession kindeClientSession = kindeClient.clientSession();
URL authorizationURL = kindeClientSession.authorizationUrl(); 
```

##### Java code to generate the redirect URL
In order to validate the code and retrieve the appropriate tokens.
```java
KindeClient kindeClient = KindeClientBuilder
        .builder()
        .build();
KindeClientSession kindeClientSession = kindeClient.clientSession();
URL authorizationURL = kindeClientSession.authorizationUrl(); 
```

## Publishing

The core team handles publishing.

- [ ] If we need to update the version of the SDK, we will have to update the value of the version in the <version> tag in the pom.xml file.
- [ ] After that, we will need to run mvn clean install.
- [ ] Next, you will need to update the version value in both places - within the -Dversion parameter in the mvn install:install-file command and in the <version> tag in the dependency entry added to the other project's pom.xml. Ensure that both places have the same version value to maintain consistency.

## Contributing

Please refer to Kinde’s [contributing guidelines](https://github.com/kinde-oss/.github/blob/489e2ca9c3307c2b2e098a885e22f2239116394a/CONTRIBUTING.md).

## License

By contributing to Kinde, you agree that your contributions will be licensed under its MIT License.
