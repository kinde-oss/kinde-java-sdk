package org.openapitools.sdk;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.openapitools.sdk.enums.StorageEnums;
import org.openapitools.sdk.storage.Storage;
import org.openapitools.sdk.utils.Utils;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.Map;

//@Controller
public class CallbackController {




    private KindeClientSDK kindeClientSDK;

    private Storage storage;

    public CallbackController(KindeClientSDK kindeClientSDK) {
        this.kindeClientSDK=kindeClientSDK;
        this.storage = this.kindeClientSDK.getStorage();
    }

//    @GetMapping("/api/auth/kinde_callback")
    public RedirectView callback(
            String code,
            String state,
            HttpServletResponse response,
            HttpServletRequest request
    ) {
        String codeVerifierCookie = storage.getState(request);
        if (codeVerifierCookie != null) {
            try {
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                headers.add("Kinde-SDK", "Java/1.2.0");

                MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
                body.add("client_id", kindeClientSDK.getClientId());
                body.add("client_secret", kindeClientSDK.getClientSecret());
                body.add("code", code);
                if(kindeClientSDK.getGrantType().equals("authorization_code_flow_pkce")){
//                  body.add("code_verifier", codeVerifierCookie);
                    String codeVerifier = storage.getCodeVerifier(request);
                    body.add("grant_type", "authorization_code");
                    body.add("code_verifier",codeVerifier);
                }else{
                    body.add("scope",kindeClientSDK.getScopes());
                    body.add("grant_type", kindeClientSDK.getGrantType());
                }
                body.add("redirect_uri", kindeClientSDK.getRedirectUri());

                HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

                ResponseEntity<Object> responseEntity = restTemplate.exchange(
                        kindeClientSDK.getDomain() + "/oauth2/token",
                        HttpMethod.POST,
                        requestEntity,
                        Object.class
                );

                Object data_ = responseEntity.getBody();
                Map<String, Object> data=(Map<String, Object>) data_;
                String accessToken = (String) data.get("access_token");

                Map<String, Object> payload=Utils.parseJWT(accessToken);
                boolean isAudienceValid = true;
//                if (audience != null && !audience.toString().equals("") && !audience.toString().equals("[]")) {
//                    String payloadAudience = payload.get("aud").toString();
//                    isAudienceValid = audience.toString().equals(payloadAudience);
//                }
                if (
                        payload.get("iss").equals(kindeClientSDK.getDomain()) &&
//                        payload.get("alg").equals("RS256") &&
                        isAudienceValid &&
                        (long) (Integer) payload.get("exp") > System.currentTimeMillis() / 1000L
                ) {

                    String newKey = "kinde" + '_' + StorageEnums.TOKEN.getValue();
                    Cookie cookie = new Cookie(newKey, URLEncoder.encode(new ObjectMapper().writeValueAsString((Map<String, Object>) data_), "UTF-8"));
                    Long exp = System.currentTimeMillis() + 3600 * 24 * 15 * 1000;
                    cookie.setMaxAge(exp.intValue());
//                    long currentTimeSeconds = System.currentTimeMillis() / 1000;
//                    cookie.setMaxAge((int) ((long) (Integer) payload.get("exp")- currentTimeSeconds));
                    cookie.setPath("/");
//                cookie.setDomain(domain);
                    cookie.setSecure(true);
                    cookie.setHttpOnly(true);
                    response.addCookie(cookie);
                }else{
                    System.out.println("One or more of the claims were not verified.");
                }
            } catch (Exception e) {
                System.err.println(e);
            }

//            String redirectUrl = appConfig.getPostLoginURL() != null
//                    ? appConfig.getPostLoginURL()
//                    : logoutRedirectUri;
            String redirectUrl = kindeClientSDK.getLogoutRedirectUri();
            return new RedirectView(redirectUrl);
//            return new RedirectView("");
        } else {

            String logoutUrl=UriComponentsBuilder.fromHttpUrl(kindeClientSDK.getLogoutEndpoint())
                    .queryParam("redirect", kindeClientSDK.getLogoutRedirectUri())
                    .build()
                    .toUriString();
            return new RedirectView(logoutUrl);
//            String logoutURL = kindeClientSDK.getDomain() + "/logout";
////            logoutURL += "?redirect=" + appConfig.getPostLogoutRedirectURL();
//            logoutURL += "?redirect=" + kindeClientSDK.getDomain() + "/logout";
//            return new RedirectView(logoutURL);
////            return new RedirectView("");
        }
    }
}
