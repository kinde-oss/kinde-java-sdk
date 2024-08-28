# Kinde-J2EE-App

This is an example J2EE servlet application designed to demonstrate how to implement authentication using Kinde. The application can be deployed on Apache Tomcat, a widely-used J2EE application server.

## Pre-Setup

Before you can run this authentication example, you'll need to register for an account on Kinde. This is required to configure the authentication settings for your application.

### Steps to Register on Kinde:
1. Open your web browser and visit [Kinde's website](https://kinde.com).
2. Click on the "Start for Free" button to begin the registration process.
3. Fill in the necessary information to create an account. Follow the instructions provided by Kinde to complete the setup.

If you have already registered on Kinde, you can skip this section.

## Download Apache Tomcat

To deploy and run this J2EE application, you'll need to have Apache Tomcat installed. Tomcat is a popular open-source implementation of the Java Servlet, JavaServer Pages, Java Expression Language, and Java WebSocket technologies.

### Steps to Download Tomcat:
1. Visit the [Apache Tomcat website](https://tomcat.apache.org).
2. Download the version of Tomcat that matches your system and Java version.
3. Follow the installation instructions provided on the Tomcat website.

## Setup

Before deploying the application to Tomcat, you must configure several environment variables. These variables are essential for the application to interact with the Kinde service.

### Required Environment Variables:

```shell
export KINDE_DOMAIN=https://< replace >.kinde.com
export KINDE_CLIENT_ID=< replace >
export KINDE_CLIENT_SECRET=< replace >
export KINDE_REDIRECT_URI=http://localhost:< replace with port of application server >/kinde-j2ee-app/login
export KINDE_GRANT_TYPE=CODE
export KINDE_SCOPES=openid
```
#### Explanation of Each Variable:
- **KINDE_DOMAIN**: Replace `< replace >` with your specific Kinde domain (e.g., `myapp.kinde.com`).
- **KINDE_CLIENT_ID**: Replace `< replace >` with your application's client ID, which you can obtain from the Kinde dashboard.
- **KINDE_CLIENT_SECRET**: Replace `< replace >` with your application's client secret, also available from the Kinde dashboard.
- **KINDE_REDIRECT_URI**: Replace `< replace with port of application server >` with the appropriate port on which your Tomcat server is running. This URI is where Kinde will redirect the user after authentication.
- **KINDE_GRANT_TYPE**: This should be set to `CODE`, indicating that the application will use the Authorization Code Grant flow.
- **KINDE_SCOPES**: This is typically set to `openid` to request an ID token from the identity provider.

Make sure to replace the placeholder values (`< replace >`) with the actual configuration details specific to your application.

## Execution

Once you have configured the environment variables and prepared the application, you can deploy it to your J2EE application server, such as Apache Tomcat.

### Deployment Instructions:
1. Build the application to generate a `.war` file.
2. Deploy the `.war` file to the `webapps` directory of your Tomcat server.
3. Start your Tomcat server if it's not already running.
4. Access the application by navigating to the URL corresponding to your configured redirect URI (e.g., `http://localhost:8080/kinde-j2ee-app/login`).

This example application will then interact with Kinde to authenticate users via OAuth 2.0.