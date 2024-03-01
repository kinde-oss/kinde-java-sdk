package org.openapitools.sdk;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.openapitools.sdk.enums.AdditionalParameters;
import org.openapitools.sdk.enums.GrantType;
import org.openapitools.sdk.enums.StorageEnums;
import org.openapitools.sdk.enums.TokenType;
import org.openapitools.sdk.oauth2.AuthorizationCode;
import org.openapitools.sdk.oauth2.ClientCredentials;
import org.openapitools.sdk.oauth2.PKCE;
import org.openapitools.sdk.storage.Storage;
import org.openapitools.sdk.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.context.annotation.Primary;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.*;


public class KindeClientSDK {

    private String domain;
    private String redirectUri;
    private String logoutRedirectUri;
    private String clientId;
    private String clientSecret;
    private String authorizationEndpoint;
    private String tokenEndpoint;
    private String logoutEndpoint;
    private String grantType;
    private Map<String, Object> additionalParameters;
    private String scopes;
    private String protocol;
    private Storage storage;

    public String getDomain() {
        return domain;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public String getLogoutRedirectUri() {
        return logoutRedirectUri;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getAuthorizationEndpoint() {
        return authorizationEndpoint;
    }

    public String getTokenEndpoint() {
        return tokenEndpoint;
    }

    public String getLogoutEndpoint() {
        return logoutEndpoint;
    }

    public String getGrantType() {
        return grantType;
    }

    public Map<String, Object> getAdditionalParameters() {
        return additionalParameters;
    }

    public String getScopes() {
        return scopes;
    }

    public String getProtocol() {
        return protocol;
    }

    public Storage getStorage() {
        return storage;
    }

    public KindeClientSDK(){}

    public KindeClientSDK(
            String domain,
            String redirectUri,
            String clientId,
            String clientSecret,
            String grantType,
            String logoutRedirectUri,
            String scopes,
            Map<String, Object> additionalParameters,
            String protocol
    ) {
        if (domain == null || domain.isEmpty()) {
            throw new IllegalArgumentException("Please provide domain");
        }
        if (!Utils.validateURL(domain)) {
            throw new IllegalArgumentException("Please provide valid domain");
        }
        this.domain = domain;

        if (redirectUri == null || redirectUri.isEmpty()) {
            throw new IllegalArgumentException("Please provide redirect_uri");
        }
        if (!Utils.validateURL(redirectUri)) {
            throw new IllegalArgumentException("Please provide valid redirect_uri");
        }
        this.redirectUri = redirectUri;

        if (clientSecret == null || clientSecret.isEmpty()) {
            throw new IllegalArgumentException("Please provide client_secret");
        }
        this.clientSecret = clientSecret;

        if (clientId == null || clientId.isEmpty()) {
            throw new IllegalArgumentException("Please provide client_id");
        }
        this.clientId = clientId;

        if (grantType == null || grantType.isEmpty()) {
            throw new IllegalArgumentException("Please provide grant_type");
        }
        this.grantType = grantType;

        if (logoutRedirectUri == null || logoutRedirectUri.isEmpty()) {
            throw new IllegalArgumentException("Please provide logout_redirect_uri");
        }
        if (!Utils.validateURL(logoutRedirectUri)) {
            throw new IllegalArgumentException("Please provide valid logout_redirect_uri");
        }

        if (additionalParameters==null){
            additionalParameters=new HashMap<>();
        }
        this.additionalParameters = Utils.checkAdditionalParameters(additionalParameters);

        this.logoutRedirectUri = logoutRedirectUri;

        if (scopes==null || scopes.equals("")){
            scopes="openid profile email offline";
        }
        this.scopes = scopes;

        if (protocol==null){
            protocol="";
        }
        this.protocol = protocol;

        // Other endpoints
        this.authorizationEndpoint = this.domain + "/oauth2/auth";
        this.tokenEndpoint = this.domain + "/oauth2/token";
        this.logoutEndpoint = this.domain + "/logout";

        this.storage = Storage.getInstance();
    }

    public KindeClientSDK(
            String domain,
            String redirectUri,
            String clientId,
            String clientSecret,
            String grantType,
            String logoutRedirectUri,
            String scopes,
            Map<String, Object> additionalParameters
    ){
        this(domain,redirectUri,clientId,clientSecret,grantType,logoutRedirectUri,scopes,additionalParameters,null);
    }

    public KindeClientSDK(
            String domain,
            String redirectUri,
            String clientId,
            String clientSecret,
            String grantType,
            String logoutRedirectUri,
            String scopes
    ){
        this(domain,redirectUri,clientId,clientSecret,grantType,logoutRedirectUri,scopes,null,null);
    }

    public KindeClientSDK(
            String domain,
            String redirectUri,
            String clientId,
            String clientSecret,
            String grantType,
            String logoutRedirectUri
    ){
        this(domain,redirectUri,clientId,clientSecret,grantType,logoutRedirectUri,null,null,null);
    }

    public KindeClientSDK(
            String domain,
            String redirectUri,
            String clientId,
            String clientSecret,
            String grantType,
            String logoutRedirectUri,
            Map<String, Object> additionalParameters
    ){
        this(domain,redirectUri,clientId,clientSecret,grantType,logoutRedirectUri,null,additionalParameters,null);
    }

    public Object login(HttpServletResponse response,Map<String, Object> additionalParameters) {
//        if (additionalParameters==null){
//            additionalParameters=new HashMap<>();
//        }
        cleanStorage(response);

        try {
            switch (this.grantType) {
                case "client_credentials":
                    ClientCredentials clientCredentials = new ClientCredentials(this.storage);
                    return clientCredentials.authenticate(response,this, additionalParameters);
                case "authorization_code":
                    AuthorizationCode authorizationCode = new AuthorizationCode(this.storage);
                    return authorizationCode.authenticate(response,this,"login", additionalParameters);
                case "authorization_code_flow_pkce":
                    PKCE pkce = new PKCE(this.storage);
                    return pkce.authenticate(response,this, "login", additionalParameters);
                default:
                    throw new IllegalArgumentException("Please provide correct grant_type");
            }
        } catch (Throwable th) {
            throw new RuntimeException(th); // Adjust exception handling as needed
        }
    }

    public Object login(HttpServletResponse response){
        return login(response,new HashMap<>());
    }

    public Object register(HttpServletResponse resp,Map<String, Object> additionalParameters) {
//        if (additionalParameters==null){
//            additionalParameters=new HashMap<>();
//        }
//        PKCE pkce=new PKCE(this.storage);
//        this.grantType = "authorization_code";

//        return pkce.authenticate(this, "registration", additionalParameters);
        switch (this.grantType) {
            case "client_credentials":
                ClientCredentials clientCredentials = new ClientCredentials(this.storage);
                return clientCredentials.authenticate(resp,this, additionalParameters);
            case "authorization_code":
                AuthorizationCode authorizationCode = new AuthorizationCode(this.storage);
                return authorizationCode.authenticate(resp,this,"registration", additionalParameters);
            case "authorization_code_flow_pkce":
                PKCE pkce = new PKCE(this.storage);
                return pkce.authenticate(resp,this, "registration", additionalParameters);
            default:
                throw new IllegalArgumentException("Please provide correct grant_type");
        }
    }

    public Object register(HttpServletResponse response){
        return register(response,new HashMap<>());
    }

    public Object createOrg(HttpServletResponse resp,Map<String, Object> additionalParameters) {
        additionalParameters.put("is_create_org", "true");
        if (!additionalParameters.containsKey("org_name")){
            additionalParameters.put("org_name", "");
        }
        return this.register(resp,additionalParameters);
    }

    public Object createOrg(HttpServletResponse resp, String orgName) {
        Map<String, Object> additionalParameters_=new HashMap<>();
        additionalParameters_.put("is_create_org", "true");
        additionalParameters_.put("org_name", orgName);
        return this.register(resp,additionalParameters_);
    }

    public Object createOrg(HttpServletResponse response){
        return createOrg(response,new HashMap<>());
    }

    public RedirectView logout(HttpServletResponse response) {
        cleanStorage(response);

        String logoutUrl = UriComponentsBuilder.fromHttpUrl(logoutEndpoint)
                .queryParam("redirect", logoutRedirectUri)
                .build()
                .toUriString();

        return new RedirectView(logoutUrl);
    }

    public Object getToken(HttpServletResponse response,HttpServletRequest req) throws Exception {
        if (GrantType.CLIENT_CREDENTIALS.getValue().equals(this.grantType)) {
            return login(response,new HashMap<>());
        }

        // Check authenticated
        if (this.isAuthenticated(req,response)) {
            Object token = storage.getToken(req);
            if (token != null) {
                return token;
            }
        }

        String newGrantType = getGrantType(this.grantType);
        MultiValueMap<String, String> formParams = new LinkedMultiValueMap<>();
        formParams.add("client_id", this.getClientId());
        formParams.add("client_secret", this.getClientSecret());
        formParams.add("grant_type", newGrantType);
        formParams.add("redirect_uri", this.getRedirectUri());
        formParams.add("response_type", "code");

        String url = req.getRequestURL().toString();
        String queryString = req.getQueryString();
        if (queryString != null) {
            url += "?" + queryString;
        }
        MultiValueMap<String, String> params = UriComponentsBuilder.fromUriString(url).build().getQueryParams();
        String stateServer = params.containsKey("state") && params.getFirst("state")!=null ? params.getFirst("state") : "";

        checkStateAuthentication(req,stateServer);

        String error = params.containsKey("error") && params.getFirst("error")!=null ? params.getFirst("error") : "";
        if (!StringUtils.isEmpty(error)) {
            String errorDescription = params.containsKey("error_description") && params.getFirst("error_description")!=null ? params.getFirst("error_description") : "";
            String msg = !StringUtils.isEmpty(errorDescription) ? errorDescription : error;
            throw new IllegalArgumentException(msg);
        }

        String authorizationCode = params.containsKey("code") && params.getFirst("code")!=null ? params.getFirst("code") : "";
        if (StringUtils.isEmpty(authorizationCode)) {
            throw new IllegalArgumentException("Not found code param");
        }

        formParams.add("code", authorizationCode);
        String codeVerifier = storage.getCodeVerifier(req);

        if (!StringUtils.isEmpty(codeVerifier)) {
            formParams.add("code_verifier", codeVerifier);
        } else if (GrantType.PKCE.getValue().equals(this.getGrantType())) {
            throw new IllegalArgumentException("Not found code_verifier");
        }

        return fetchToken(response,formParams);
    }

    public Map<String, Object> getUserDetails(HttpServletRequest req) {
        return storage.getUserProfile(req);
    }

    public Map<String, Object> getClaim(HttpServletRequest req,String keyName,String tokenType) {
//        if (tokenType==null){
//            tokenType=TokenType.ACCESS_TOKEN.getValue();
//        }
        Map<String, Object> data = getClaims(req,tokenType);

        Map<String,Object> result=new HashMap<>();
        result.put("name", keyName);
        result.put("value",data.containsKey(keyName) && data.get(keyName)!=null ? data.get(keyName) : null);
        return result;
    }

    public Map<String, Object> getClaim(HttpServletRequest req,String keyName) {
        return getClaim(req,keyName,TokenType.ACCESS_TOKEN.getValue());
    }

    public Map<String, Object> getPermissions(HttpServletRequest req) {
        Map<String, Object> claims = getClaims(req);

        Map<String, Object> response = new HashMap<>();
        response.put("orgCode", claims.get("org_code"));
        response.put("permissions", claims.get("permissions"));

        return response;
    }

    public Map<String, Object> getPermission(HttpServletRequest req,String permission) {
        Map<String, Object> allClaims = getClaims(req);
        List<String> permissions = (List<String>) allClaims.get("permissions");

        Map<String, Object> result = new HashMap<>();
        result.put("orgCode", allClaims.get("org_code"));
        result.put("isGranted", permissions.contains(permission));
        return result;
    }

    public Map<String, Object> getOrganization(HttpServletRequest req) {
        Map<String, Object> response = new HashMap<>();
        response.put("orgCode", getClaim(req,"org_code").get("value"));
        return response;
    }

    public Map<String, Object> getUserOrganizations(HttpServletRequest req) {
        Map<String, Object> response = new HashMap<>();
        response.put("orgCodes", getClaim(req,"org_codes", TokenType.ID_TOKEN.getValue()).get("value"));
        return response;
    }

    public Map<String, Object> getBooleanFlag(HttpServletRequest req,String flagName, Object defaultValue) {
        Map<String, Object> options = new HashMap<>();
        options.put("defaultValue", defaultValue);
        return getFlag(req,flagName, options, "b");
    }

    public Map<String, Object> getBooleanFlag(HttpServletRequest req,String flagName) {
        return getBooleanFlag(req,flagName,null);
    }

    public Map<String, Object> getStringFlag(HttpServletRequest req,String flagName, Object defaultValue) {
        Map<String, Object> options = new HashMap<>();
        options.put("defaultValue", defaultValue);
        return getFlag(req,flagName, options, "s");
    }

    public Map<String, Object> getStringFlag(HttpServletRequest req,String flagName) {
        return getStringFlag(req,flagName,null);
    }

    public Map<String, Object> getIntegerFlag(HttpServletRequest req,String flagName, Object defaultValue) {
        Map<String, Object> options = new HashMap<>();
        options.put("defaultValue", defaultValue);
        return getFlag(req,flagName, options, "i");
    }

    public Map<String, Object> getIntegerFlag(HttpServletRequest req,String flagName) {
        return getIntegerFlag(req,flagName,null);
    }

    public Map<String, Object> getFlag(HttpServletRequest req,String flagName, Map<String, Object> options, String flagType) {

//        if (options==null){
//            options=new HashMap<>();
//        }

        boolean isUsedDefault = false;
        Map<String, Object> flag = getFeatureFlags(req,flagName);

//        if (flag == null) {
        if (flag == null || flag.isEmpty()) {
            isUsedDefault = true;
            flag = new HashMap<>();
            flag.put("v", options.get("defaultValue"));
            flag.put("t", flagType);
        }

        if (!flag.containsKey("v") || flag.get("v")==null) {
            throw new IllegalArgumentException("This flag '" + flagName + "' was not found, and no default value has been provided");
        }

        String flagTypeParsed = Utils.listType.get(flag.get("t"));

        String requestType = Utils.listType.get(flagType);
        if (requestType != null && !flagTypeParsed.equals(requestType)) {
            throw new IllegalArgumentException("Flag '" + flagName + "' is type " + flagTypeParsed + " - requested type " + requestType);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("code", flagName);
        result.put("type", flagTypeParsed);
        result.put("value", flag.get("v"));
        result.put("is_default", isUsedDefault);

        return result;
    }

    public Map<String, Object> getFlag(HttpServletRequest req,String flagName){
        return getFlag(req,flagName,new HashMap<>(),null);
    }

    public Map<String, Object> getFlag(HttpServletRequest req,String flagName, Map<String, Object> options){
        return getFlag(req,flagName,options,null);
    }

    public Map<String, Object> getFlag(HttpServletRequest req,String flagName, String flagType){
        return getFlag(req,flagName,new HashMap<>(),flagType);
    }

//    public function __get($key)
//    {
//        if (!property_exists($this, $key) && $key === 'isAuthenticated') {
//            return $this->isAuthenticated();
//        }
//
//        return $this->$key;
//    }

    private Map<String, Object> getFeatureFlags(HttpServletRequest req,String name) {
        Map<String, Object> flags = (Map<String, Object>) getClaim(req,"feature_flags").get("value");

        if (name != null && flags !=null && !flags.isEmpty()) {
            return (Map<String, Object>) flags.get(name);
        }

        return flags;
    }

    private Map<String, Object> getFeatureFlags(HttpServletRequest req){
        return getFeatureFlags(req,null);
    }

    private Object fetchToken(HttpServletResponse resp,MultiValueMap<String, String> formParams) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Kinde-SDK", "Java/1.2.0"); // current SDK version

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(formParams, headers);
        ResponseEntity<Object> response = restTemplate.postForEntity(
                tokenEndpoint,
                requestEntity,
                Object.class
        );

        Object token = response.getBody();
        storage.setToken(resp,token);
        Map<String, Object> tokenMap = null;
        if (token instanceof Map) {
            // Convert tokenObject to Map
            tokenMap = (Map<String, Object>) token;

        } else {
            try {
                JsonNode jsonNode = new ObjectMapper().readTree((String) token);

                Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
                while (fields.hasNext()) {
                    Map.Entry<String, JsonNode> field = fields.next();
                    tokenMap.put(field.getKey(), field.getValue().asText());
                }
            }catch (Exception e){

            }
        }
        // Cleaning
        storage.removeItem(resp,StorageEnums.CODE_VERIFIER.getValue());
        storage.removeItem(resp,StorageEnums.STATE.getValue());
        return tokenMap;
    }

    public boolean isAuthenticated(HttpServletRequest req,HttpServletResponse resp) {
        long timeExpired = storage.getExpiredAt(req);
        boolean authenticated = timeExpired > System.currentTimeMillis() / 1000;

        if (authenticated) {
            return true;
        }

        // Using refresh token to get new access token
        try {
            String refreshToken = storage.getRefreshToken(req);
            if (!StringUtils.isEmpty(refreshToken)) {
                MultiValueMap<String, String> formParams = new LinkedMultiValueMap<>();
                formParams.add("client_id", clientId);
                formParams.add("client_secret", clientSecret);
                formParams.add("grant_type", "refresh_token");
                formParams.add("refresh_token", refreshToken);

                Map<String,Object> token =(Map<String, Object>) fetchToken(resp,formParams);
                if (token != null && (int) token.get("expires_in") > 0) {
                    return true;
                }
            }
        } catch (Throwable th) {
            // Handle exceptions if needed
        }

        return false;
    }

    public Map<String, Object> getClaims(HttpServletRequest req,String tokenType) {
//        if (tokenType==null){
//            tokenType=TokenType.ACCESS_TOKEN.getValue();
//        }

        if (!tokenType.equals(TokenType.ACCESS_TOKEN.getValue()) && !tokenType.equals(TokenType.ID_TOKEN.getValue())) {
            throw new IllegalArgumentException("Please provide valid token (access_token or id_token) to get claim");
        }

        String token = tokenType.equals(TokenType.ACCESS_TOKEN.getValue()) ? storage.getAccessToken(req) : storage.getIdToken(req);
        if (token == null || token.isEmpty()) {
            throw new RuntimeException("Request is missing required authentication credential");
        }

        return Utils.parseJWT(token);
    }

    public Map<String, Object> getClaims(HttpServletRequest req){
        return getClaims(req,TokenType.ACCESS_TOKEN.getValue());
    }

    private void cleanStorage(HttpServletResponse response) {
        storage.clear(response);
    }

    private String getProtocol(HttpServletRequest req) {
        if (this.protocol != null && !this.protocol.isEmpty()) {
            return this.protocol;
        }

        return req.isSecure() ? "https" : "http";
    }

    private void checkStateAuthentication(HttpServletRequest req,String stateServer) {
        String storageOAuthState = storage.getState(req);

        if (storageOAuthState == null || storageOAuthState.equals("") || !stateServer.equals(storageOAuthState)) {
            throw new IllegalArgumentException("Authentication failed because it tries to validate state");
        }
    }

    private String getGrantType(String grantType) {
        switch (grantType) {
            case "authorization_code":
            case "authorization_code_flow_pkce":
                return "authorization_code";
            case "client_credentials":
                return "client_credentials";
            default:
                throw new IllegalArgumentException("Please provide correct grant_type");
        }
    }

}
