# Kinde Java SDK

The Kinde SDK for Java.

[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat-square)](https://makeapullrequest.com) [![Kinde Docs](https://img.shields.io/badge/Kinde-Docs-eee?style=flat-square)](https://kinde.com/docs/developer-tools) [![Kinde Community](https://img.shields.io/badge/Kinde-Community-eee?style=flat-square)](https://thekindecommunity.slack.com)

## Development

This project contains an SDK package with a class called KindClientSDK. Inside this class, you will find all the main functions and methods that you need to use in another project. Any changes related to the SDK should be made through this file.

Additionally, within the SDK package, there is a callback controller class where we have implemented a method for handling callback requests. If you need to make any changes to the callback request processing, you should do so in this file.

Furthermore, the SDK package includes sub-packages for enums, OAuth2, storage, and utilities. In the storage package, you will find functionality related to cookie storage, and within the OAuth2 package, we have classes for handling authorization code, client credentials, and PKCE (Proof Key for Code Exchange).

These components collectively make up the SDK, and you should make any necessary modifications or enhancements within the respective files to maintain and extend the SDK's functionality.


### Initial set up

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

### Project Setup
Setup the following environment variables

```
export KINDE_DOMAIN=https://<replace>.kinde.com
export KINDE_CLIENT_ID=<replace>
export KINDE_CLIENT_SECRET=<replace>
```

If you deploying the playground j2ee project in tomcat the following are required.
```
export KINDE_REDIRECT_URI=http://localhost:8080/login
export KINDE_REDIRECT_URI=http://localhost:8080/kinde-j2ee-app/login
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
