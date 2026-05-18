# MIGRATION

## V 2.x (Spring Boot 3) to V 2.2 (Spring Boot 4)

`kinde-springboot-starter` and `kinde-springboot-core` were upgraded to
[Spring Boot 4.0.x](https://spring.io/blog/2025/11/20/spring-boot-4-0-0-available-now)
and [Spring Security 7.x](https://docs.spring.io/spring-security/reference/7.0.0/migration/index.html).
If you depend on either, here is what changes for you.

### Baseline requirements

- **Java 17 or later.** Spring Boot 4 raised the minimum JVM. The SDK
  itself is built on Java 25 but produces Java 17-compatible bytecode.
- **Spring Boot 4.x** in your application. Spring Boot 3.x consumers
  should stay on the previous Kinde SDK release line.

### Behaviour change: JWT audience validation is now opt-in

Previously the resource-server JWT validator was hardcoded to require an
audience claim of `"api://default"` (a leftover sample value). Real Kinde
access tokens carry an empty `aud` array unless you explicitly configure
an API resource on the Kinde dashboard, so the previous default rejected
every default Kinde token with a 401:

```
"This aud claim is not equal to the configured audience"
```

The validator now skips audience checking unless you explicitly configure
an expected audience:

```yaml
kinde:
  oauth2:
    audience: https://your-api.example.com   # optional; only set if you
                                              # configured an API resource
                                              # on the Kinde dashboard
```

If you previously relied on the implicit default and your tokens actually
contained `api://default` as their audience (highly unlikely for Kinde
deployments), set `kinde.oauth2.audience: api://default` to preserve the
old behaviour.

### Configuration property rename: `okta.oauth2.post-logout-redirect-uri` → `kinde.oauth2.post-logout-redirect-uri`

RP-Initiated logout (redirect to the IdP's logout endpoint after a local
sign-out) is wired in by the SDK when this property is set. The previous
property name was an Okta-fork leftover that nobody in a Kinde deployment
could plausibly have set. To enable RP-Initiated logout now:

```yaml
kinde:
  oauth2:
    post-logout-redirect-uri: http://localhost:8080   # where to land
                                                       # after Kinde
                                                       # clears its
                                                       # session
```

The same rename applies to the reactive (WebFlux) auto-configuration.

### New transitive dependencies for `kinde-springboot-starter`

Spring Boot 4 split OAuth2 auto-configuration out of
`spring-boot-autoconfigure` into dedicated starters. The
`kinde-springboot-starter` pom now pulls them in for you:

- `spring-boot-starter-oauth2-client`
- `spring-boot-starter-oauth2-resource-server`

You do **not** need to add them to your own pom unless you previously
declared them explicitly and want to keep that explicit (which is fine).

### Spring Security 7 source-level changes (only affects custom code)

If you previously customized security by writing your own
`SecurityFilterChain` against Spring Security 6 APIs, some method
signatures have changed in Spring Security 7. Notable items:

- `oauth2Login(Customizer)` and `oauth2ResourceServer(Customizer)`
  signatures changed slightly; the lambda forms still work but the
  no-arg deprecated forms are gone.
- `WebSecurityConfigurerAdapter` (long deprecated) is fully removed.
- Several token-endpoint client classes moved packages. The SDK does
  this internally; you are only affected if you wired your own
  `OAuth2AccessTokenResponseClient` bean.

The [official Spring Security 7 migration guide](https://docs.spring.io/spring-security/reference/7.0.0/migration/index.html)
is the source of truth.

## V 1.0.0 to V 2.0.0

The original implementation was based on Spring Boot controllers. It provided a controller that could be instantiated inline and then invoked to perform the PKCE authentication. Version 2.0.0 implements a core OAuth and OpenID library. This library provides a rich set of functions, from token management to OpenID authentication.

### Version 1.0.0 Implementation
Setting up the client requires a programmatic configuration. The following is performed through Spring injection.

#### Programmatic Configuration
```java
// all values are injected as follow
@Value("${kinde.host}")
public String domain;

// initialize the Kinde sdk
this.kindeClientSDK = new KindeClientSDK(
        domain,
        redirectUri,
        clientId,
        clientSecret,
        grantType,
        logoutRedirectUri);
```

#### Invoking the inline controller to authenticate the callback
```java
// Invoke the callback
RedirectView redirectView=new CallbackController(this.kindeClientSDK).callback(code,state,response,request);
```

The callback is responsible for setting up the kinde_token, which is the 15-day token and is maintained on the client's browser.

### Version 2.0.0 Implementation

As of Version 2.0.0, this approach has changed significantly. Configuration is now provided through environmental variables, .env files, or programmatic methods.

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

#### Processing the response
Processing the login response.
```java
AuthorizationUrl authorizationUrl = (AuthorizationUrl)req.getSession().getAttribute("AuthorizationUrl");
List<KindeToken> tokens = kindeClient.getKindeClient().initClientSession(code,authorizationUrl).retrieveTokens();

tokens.stream().filter(token->token instanceof AccessToken).forEach(token->req.getSession().setAttribute("access_token",token.token()));
tokens.stream().filter(token->token instanceof IDToken).forEach(token->req.getSession().setAttribute("id_token",token.token()));
tokens.stream().filter(token->token instanceof RefreshToken).forEach(token->req.getSession().setAttribute("refresh_token",token.token()));
resp.sendRedirect("<replace>");
```