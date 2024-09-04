# Kinde J2EE

## Development

This is the Kinde J2EE Library, and contains the core components needed to manage a client connection to the Kinde from a J2EE servlet container.

## Project Dependancies

### Maven
In order to use this SDK include following POM dependancy.
```xml
    <dependency>
      <groupId>com.kinde</groupId>
      <artifactId>kinde-j2ee</artifactId>
      <version>2.0.0</version>
    </dependency>
```
### Gradle
In order to use the SDK with a Gradle build process please use the following dependancy.
```groovy
    configuration('com.kinde:kinde-j2ee:2.0.0')
```

## Project Configuration
The web.xml file needs to be configured to utilize these servlets.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee
         http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd"
         version="3.1">
    <servlet>
        <servlet-name>KindeLoginServlet</servlet-name>
        <servlet-class>com.kinde.servlet.KindeLoginServlet</servlet-class>
    </servlet>
    <servlet>
        <servlet-name>KindeLogoutServlet</servlet-name>
        <servlet-class>com.kinde.servlet.KindeLogoutServlet</servlet-class>
    </servlet>
    <servlet>
        <servlet-name>KindeRegisterServlet</servlet-name>
        <servlet-class>com.kinde.servlet.KindeRegistrationServlet</servlet-class>
    </servlet>
    <filter>
        <filter-name>KindeLoginFilter</filter-name>
        <filter-class>com.kinde.filter.KindeLoginFilter</filter-class>
    </filter>
    <servlet-mapping>
        <servlet-name>KindeLoginServlet</servlet-name>
        <url-pattern>/login</url-pattern>
    </servlet-mapping>
    <servlet-mapping>
        <servlet-name>KindeRegisterServlet</servlet-name>
        <url-pattern>/register</url-pattern>
    </servlet-mapping>
    <servlet-mapping>
        <servlet-name>KindeLogoutServlet</servlet-name>
        <url-pattern>/logout</url-pattern>
    </servlet-mapping>
    <filter-mapping>
        <filter-name>KindeLoginFilter</filter-name>
        <url-pattern>/private/*</url-pattern> <!-- Apply to all URLs or specify specific patterns -->
    </filter-mapping>
</web-app>
```

## J2EE Servlets and Filters
The above project configuration sets up an example web.xml, using the Kinde-J2EE servlets and filters. These are all you need to secure a j2ee project and can be used with Spring Boot or Quarkus:
1. KindeLoginServlet: The servlet responsible for managing a login. It manages the re-direct to the Kinde Domain and and in the example above is setup to run from /login with the J2EE context.
2. KindeRegisterServlet: This servlet is responsible for triggering the registration flow. It redirects to the configured Kinde Domain and provides Kinde with the flags to indicate this is a user registration flow.
3. KindeLogoutServlet: This servlet is responsible for triggering the logout flow, and for removing from the application any active session for the user.
4. KindeLoginFilter: This is a J2EE filter and acts as a gateway to the private section of your site. It will deny users access to anything its scope covers unless they have succesfully authenticated. It also sets up roles or permissions for the logged in user.

## Environmental Configuration
Configuration can either be performed by exports or through a .env file

### Shell
```shell
export KINDE_DOMAIN=https://<replace>.kinde.com # This is the domain you setup at kinde
export KINDE_CLIENT_ID=<replace> # the id for the client connecting to Kinde
export KINDE_CLIENT_SECRET=<replace> # the secret used to authenticate the client against Kinde
export KINDE_REDIRECT_URI=http://localhost:8080/kinde-j2ee-app/login
```

### .env
The Kinde library supports .env files. They must be located in the directory from which the application is executed.
```shell
KINDE_DOMAIN=https://<replace>.kinde.com
KINDE_CLIENT_ID=<replace>
KINDE_CLIENT_SECRET=<replace>
KINDE_REDIRECT_URI=http://localhost:8080/kinde-j2ee-app/login
```
