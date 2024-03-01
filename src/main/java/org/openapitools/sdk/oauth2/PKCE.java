package org.openapitools.sdk.oauth2;

import org.openapitools.sdk.KindeClientSDK;
import org.openapitools.sdk.enums.StorageEnums;
import org.openapitools.sdk.storage.Storage;
import org.openapitools.sdk.utils.Utils;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.servlet.view.RedirectView;

import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Component
public class PKCE {

    private Storage storage;

    public PKCE(Storage storage) {
        this.storage = storage;
    }
//    public PKCE(){
//        this.storage = Storage.getInstance();
//    }

    public RedirectView authenticate(HttpServletResponse response,KindeClientSDK clientSDK, String startPage, Map<String, Object> additionalParameters) {
//        if (additionalParameters==null){
//            additionalParameters=new HashMap<>();
//        }
        storage.removeItem(response,StorageEnums.CODE_VERIFIER.getValue());
        Map<String, String> challenge = Utils.generateChallenge();
        String state = challenge.get("state");
        storage.setState(response,state);

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(clientSDK.getAuthorizationEndpoint())
            .queryParam("redirect_uri", clientSDK.getRedirectUri())
            .queryParam("client_id", clientSDK.getClientId())
            .queryParam("response_type", "code")
            .queryParam("scope", clientSDK.getScopes())
            .queryParam("code_challenge", challenge.get("codeChallenge"))
            .queryParam("code_challenge_method", "S256")
            .queryParam("state", state)
            .queryParam("start_page", startPage);

        // Add additional parameters to the URI
        Map<String, Object> mergedAdditionalParameters = Utils.addAdditionalParameters(clientSDK.getAdditionalParameters(), additionalParameters);
        mergedAdditionalParameters.forEach(uriBuilder::queryParam);

        storage.setCodeVerifier(response,challenge.get("codeVerifier"));

        String authorizationUrl = uriBuilder.build().toUriString();

        // Create a RedirectView to perform the redirection
        RedirectView redirectView = new RedirectView();
        System.out.println(authorizationUrl);
        redirectView.setUrl(authorizationUrl);

        return redirectView;
    }

    public RedirectView authenticate(HttpServletResponse response,KindeClientSDK clientSDK) {
        return authenticate(response,clientSDK,"login",new HashMap<>());
    }

    public RedirectView authenticate(HttpServletResponse response,KindeClientSDK clientSDK, String startPage) {
        return authenticate(response,clientSDK,startPage,new HashMap<>());
    }

    public RedirectView authenticate(HttpServletResponse response,KindeClientSDK clientSDK, Map<String, Object> additionalParameters) {
        return authenticate(response,clientSDK,"login",additionalParameters);
    }
}
