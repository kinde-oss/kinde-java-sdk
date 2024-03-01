package org.openapitools.sdk.oauth2;

import org.openapitools.sdk.KindeClientSDK;
import org.openapitools.sdk.enums.GrantType;
import org.openapitools.sdk.utils.Utils;
import org.openapitools.sdk.storage.Storage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Component
public class AuthorizationCode {

    private final Storage storage;

    @Autowired
    public AuthorizationCode(Storage storage) {
        this.storage = storage;
    }

    public RedirectView authenticate(HttpServletResponse response,KindeClientSDK clientSDK,String startPage, Map<String, Object> additionalParameters) {
//        if (additionalParameters==null){
//            additionalParameters=new HashMap<>();
//        }

        String state = Utils.randomString();
        storage.setState(response,state);

//        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
//        queryParams.add("client_id", clientSDK.getClientId());
//        queryParams.add("grant_type", GrantType.AUTHORIZATION_CODE.getValue());
//        queryParams.add("redirect_uri", clientSDK.getLogoutRedirectUri());
//        queryParams.add("response_type", "code");
//        queryParams.add("scope", clientSDK.getScopes());
//        queryParams.add("state", state);
//        queryParams.add("start_page", "login");

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(clientSDK.getAuthorizationEndpoint())
                .queryParam("client_id", clientSDK.getClientId())
                .queryParam("grant_type", GrantType.AUTHORIZATION_CODE.getValue())
                .queryParam("redirect_uri", clientSDK.getRedirectUri())
                .queryParam("response_type", "code")
                .queryParam("scope", clientSDK.getScopes())
                .queryParam("state", state)
                .queryParam("start_page", startPage);

//        Map<String, String> mergedAdditionalParameters = Utils.addAdditionalParameters(clientSDK.getAdditionalParameters(), additionalParameters);
//        queryParams.setAll(mergedAdditionalParameters);
//
//        String authorizationUrl = clientSDK.getAuthorizationEndpoint() + "?" + queryParams.toSingleValueMap();

        Map<String, Object> mergedAdditionalParameters = Utils.addAdditionalParameters(clientSDK.getAdditionalParameters(), additionalParameters);
        mergedAdditionalParameters.forEach(uriBuilder::queryParam);
        String authUrl = uriBuilder.build().toUriString();

        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(authUrl);
        return redirectView;
//        return new ModelAndView(redirectView);
    }

    public RedirectView authenticate(HttpServletResponse response,KindeClientSDK clientSDK,String startPage){
        return authenticate(response,clientSDK,startPage,new HashMap<>());
    }
}
