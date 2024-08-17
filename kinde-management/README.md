# The Kinde Management API
## Development

This is the Kinde Management Library, and contains the components needed to access the Kinde Management API. This consists of an OpenAPI generated stub and a KindeAdminSession tool. That will instanciate the ApiClient using the Oidc details.

## Project Dependancies

### Maven
In order to use this SDK include following POM dependancy.
```xml
    <dependency>
      <groupId>com.kinde</groupId>
      <artifactId>kinde-management</artifactId>
      <version>2.0.0</version>
    </dependency>
```
### Gradle
In order to use the SDK with a Gradle build process please use the following dependancy.
```groovy
    configuration('com.kinde:kinde-management:2.0.0')
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
The following basic environmental variables are required at a mimimum for connecting to the Kinde Management API.
```shell
export KINDE_DOMAIN=https://<replace>.kinde.com # This is the domain you setup at kinde
export KINDE_CLIENT_ID=<replace> # the id for the client connecting to Kinde
export KINDE_CLIENT_SECRET=<replace> # the secret used to authenticate the client against Kinde
export KINDE_SCOPES=openid # the scope as we are using an OpenID connection
export KINDE_AUDIENCE=https://<replace>.kinde.com/api # the audience we need access to
```
#### Setup .env environmental files.
The Kinde library supports .env files. The must be located in the directory from which the application is executed.
```shell
KINDE_DOMAIN=https://burntjam.kinde.com
KINDE_CLIENT_ID=<replace>
KINDE_CLIENT_SECRET=<replace>
KINDE_SCOPES=openid
KINDE_AUDIENCE=https://<replace>.kinde.com/api
```

##### Programmatic configuration
If you want to pass in configuration programmatically the KindeClientBuilder supports this use the following approach.
```java
KindeClient kindeClient = KindeClientBuilder
        .builder()
        .domain("<replace>")
        .clientId("<replace>")
        .clientSecret("<replace>")
        .addScope("<replace>")
        .addAudience("https://<replace>.kinde.com/api")
        .build();
```

##### Java Code to get an ApiClient
This example gets an ApiClient instance and then creates an ApplicationApi instance using the ApiClient.
```java
KindeClient kindeClient = KindeClientBuilder
        .builder()
        .build();
KindeAdminSession kindeAdminSession = KindeAdminSessionBuilder.builder().client(kindeClient).build();
ApiClient apiClient = kindeAdminSession.initClient();
ApplicationsApi applicationsApi = new ApplicationsApi(apiClient);
```

## License

By contributing to Kinde, you agree that your contributions will be licensed under its MIT License.
