# Kinde-Core-Example

This project demonstrates how to use the Kinde-Core library for authentication and identity management, utilizing the JUnit framework for unit testing.

## Setup

To configure the unit tests, you need to set up a `.env` file, which is located at the root of this project. This file contains the necessary environment variables required for the tests to run successfully.

### Required Environment Variables:

```shell
KINDE_DOMAIN=https://< replace >.kinde.com
KINDE_CLIENT_ID=< replace >
KINDE_CLIENT_SECRET=< replace >
KINDE_REDIRECT_URI=http://localhost:8080/kinde-j2ee-app/login
KINDE_GRANT_TYPE=CODE
KINDE_SCOPES=openid
```

#### Explanation of Each Variable:
- **KINDE_DOMAIN**: Replace `< replace >` with your specific Kinde domain (e.g., `myapp.kinde.com`).
- **KINDE_CLIENT_ID**: Replace `< replace >` with your application's client ID, which can be obtained from the Kinde dashboard.
- **KINDE_CLIENT_SECRET**: Replace `< replace >` with your application's client secret, also available from the Kinde dashboard.
- **KINDE_REDIRECT_URI**: Replace `< replace >` with the appropriate URI where Kinde will redirect the user after authentication. This is usually set to `http://localhost:8080/kinde-j2ee-app/login` during testing.
- **KINDE_GRANT_TYPE**: This should be set to `CODE`, which indicates that the application will use the Authorization Code Grant flow.
- **KINDE_SCOPES**: Typically set to `openid` to request an ID token from the identity provider.

Be sure to replace the placeholder values (`< replace >`) with your specific configuration details before running the tests.

## Execution

You can execute the unit tests either through your Integrated Development Environment (IDE) or by using Maven from the command line.

### Running Tests with Maven:

To run the tests using Maven, use the following command:

```shell
mvn -Dtest=KindeCoreExampleTest test
```

This command will execute the KindeCoreExampleTest class, running all the tests defined within it. It must be run from this projects directory.