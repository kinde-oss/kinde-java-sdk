# KINDE SPRINGBOOT PKCE Example

This is a standalone Spring Boot example that demonstrates how to quickly set up a Spring client using PKCE (Proof Key for Code Exchange) authentication. While this example focuses on PKCE, please note that the functionality to pull through user permissions is currently under development.

## Setup

### `application.yaml` Configuration

To configure the Spring Boot application, you'll need to set up the `application.yaml` file, which is located in the `src/main/resources/` directory. This file will contain all the necessary configuration settings required for the application to authenticate using PKCE.

Below is an example of what your `application.yaml` might look like:

```yaml
spring:
  security:
    oauth2:
      client:
        provider:
          kinde:
            issuer-uri: https://< replace >.kinde.com
        registration:
          pkce:
            provider: kinde
            client-id: <replace>
            client-secret: <replace>
            scope: openid,email
```
#### Explanation of Key Configuration Properties:
- **`client-id`**: Replace with your Kinde application's client ID, which you can obtain from the Kinde dashboard.
- **`client-secret`**: Replace with your Kinde application's client secret, also available from the Kinde dashboard.
- **`issuer-uri`**: Replace with the Kinde domain you have created.
- **`token-uri`**: The URL to which the authorization code is sent to obtain the access token. Replace `<replace-with-your-domain>` with your specific Kinde domain.

Ensure all placeholder values (`<replace-with-...>`) are replaced with actual configuration details specific to your application before running the tests.

## Execution

Once your `application.yaml` file is properly configured, you can execute the Spring Boot application using one of the following methods:

### Running from the Command Line

If you want to run the Spring Boot application from the command line, use the following command:

```shell
mvn spring-boot:run
```

Alternatively, you can build the application into a JAR file and run it directly:

```shell
mvn clean package
java -jar target/kinde-springboot-pkce-example-0.0.1-SNAPSHOT.jar
```
### Explanation:
- **`./mvnw clean package`**: This command uses Maven to clean the project, which removes any previously compiled files and artifacts, ensuring a fresh build. It then packages the application into a JAR file, which can be found in the `target` directory.
- **`java -jar target/kinde-springboot-pkce-example-0.0.1-SNAPSHOT.jar`**: This command runs the Spring Boot application directly from the JAR file. The `-jar` option specifies the JAR file to execute. This allows you to run your application as a standalone Java process, independent of an IDE or other tools.

### Running from an IDE

If you are using an Integrated Development Environment (IDE) like IntelliJ IDEA or Eclipse, you can run the application directly from the IDE:

1. **Import the Project**: Import the project into your IDE as a Maven project.
2. **Navigate to the Main Application Class**: Locate the `KindeSpringbootPkceExampleApplication` class in the `src/main/java/com/yourcompany/kinde` package.
3. **Run the Application**: Right-click the `KindeSpringbootPkceExampleApplication` class and select "Run" or "Debug" to start the Spring Boot application.

### Verifying the Application Startup

Once the application is running, you should see output in the console indicating that Spring Boot has started successfully. The application will be accessible at the address specified by the `server.port` property in your `application.yaml` file (default is `http://localhost:8080/`).

## Testing

Once the Spring Boot application is up and running, you can perform browser-based testing to verify that PKCE authentication is working correctly.

### Testing Steps:
1. **Open Your Browser**: Launch your preferred web browser (e.g., Chrome, Firefox, Safari).
2. **Navigate to the Application**: Go to `http://localhost:8080/` (or the appropriate URL based on your server configuration).
3. **Trigger the Authentication Flow**: If your application has a login button, click it to initiate the authentication process. This should redirect you to the Kinde login page.
4. **Log in to Kinde**: Enter your credentials on the Kinde login page. Once authenticated, Kinde will redirect you back to your application at the URL specified in the `redirect-uri`.
5. **Verify the Login**: Upon successful login, the application should now be able to display some user information retrieved from Kinde, such as the user's profile or a welcome message.

### Additional Testing
- **Inspect the Access Token**: If possible, inspect the JWT access token received from Kinde to ensure it contains the correct scopes and claims.
- **Test Logout Functionality**: Ensure that your application’s logout functionality correctly invalidates the session and redirects the user as needed.
- **Check Error Handling**: Test various scenarios, such as invalid credentials or expired tokens, to ensure your application handles errors gracefully.

By following these steps, you can ensure that your Spring Boot application is correctly set up to use PKCE for authentication and is functioning as expected.
