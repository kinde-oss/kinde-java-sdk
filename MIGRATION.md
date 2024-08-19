# MIGRATION

## V 1.0.0 to V 2.0.0

The original implementation was based on Spring Boot controllers. It provided a controller that could be instanciated in line and then invoked to perform the PCKE authentication. Version 2.0.0 implementats a core Oauth and OpenID library. This library provides a rich set of functions, from token management all the way through to OpenID authentication.

### Version 1.0.0 Implementation
Setup the client requires a programtic configuration. The below is performed through spring injection.

#### Programmatic Configuration
```java
this.kindeClientSDK=new KindeClientSDK(
        domain,
        redirectUri,
        clientId,
        clientSecret,
        grantType,
        logoutRedirectUri);
```

#### Invoking the inline controller to authenticate the callback.
```java
// Invoke the callback
RedirectView redirectView=new CallbackController(this.kindeClientSDK).callback(code,state,response,request);
```
The callback is responsible for setting up the kinde_token. Which is the 15 day token and is maintained on the clients browser.

### Version 2.0.0 Implementation
As of Version 2.0.0 this approach ash change significantly. Configuration is now provided either through, environmental, .env configuration or programmtic.

#### Environmental Configuration
Shell configuration
```shell
export KINDE_DOMAIN=https://<replace>.kinde.com
export KINDE_CLIENT_ID=<replace>
export KINDE_CLIENT_SECRET=<replace>
export KINDE_REDIRECT_URI=http://localhost:8080/kinde-j2ee-app/login
export KINDE_GRANT_TYPE=CODE
export KINDE_SCOPES=openid
```
.env configuration
```shell
KINDE_DOMAIN=https://<replace>.kinde.com
KINDE_CLIENT_ID=<replace>
KINDE_CLIENT_SECRET=<replace>
KINDE_REDIRECT_URI=http://localhost:8080/kinde-j2ee-app/login
KINDE_GRANT_TYPE=CODE
KINDE_SCOPES=openid
```
programmatic configuration
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


#### Retrieving the redirect url
It is now possible to get the KindeClient to build a redirect url.
```java
AuthorizationUrl authorizationUrl = kindeClient.getKindeClient().clientSession().authorizationUrl();
req.getSession().setAttribute("AuthorizationUrl",authorizationUrl);
resp.sendRedirect(authorizationUrl.getUrl().toString());
```

#### Processing the response.
Processing the login response.
```java
AuthorizationUrl authorizationUrl = (AuthorizationUrl)req.getSession().getAttribute("AuthorizationUrl");
List<KindeToken> tokens = kindeClient.getKindeClient().initClientSession(code,authorizationUrl).retrieveTokens();

tokens.stream().filter(token->token instanceof AccessToken).forEach(token->req.getSession().setAttribute("access_token",token.token()));
tokens.stream().filter(token->token instanceof IDToken).forEach(token->req.getSession().setAttribute("id_token",token.token()));
tokens.stream().filter(token->token instanceof RefreshToken).forEach(token->req.getSession().setAttribute("refresh_token",token.token()));
resp.sendRedirect("<replace>");
```