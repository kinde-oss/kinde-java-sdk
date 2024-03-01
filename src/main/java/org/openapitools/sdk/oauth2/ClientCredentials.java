package org.openapitools.sdk.oauth2;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openapitools.sdk.KindeClientSDK;
import org.openapitools.sdk.enums.GrantType;
import org.openapitools.sdk.storage.Storage;
import org.openapitools.sdk.utils.Utils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Service
public class ClientCredentials {

    private Storage storage;

    public ClientCredentials(Storage storage) {
        this.storage = storage;
    }

    public Map<String, Object> authenticate(HttpServletResponse resp,KindeClientSDK clientSDK, Map<String, Object> additionalParameters) {
//        if (additionalParameters==null){
//            additionalParameters=new HashMap<>();
//        }
        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.add("Kinde-SDK", "Java/1.2.0");

            MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
            formData.add("client_id", clientSDK.getClientId());
            formData.add("client_secret", clientSDK.getClientSecret());
            formData.add("grant_type", GrantType.CLIENT_CREDENTIALS.getValue());
            formData.add("scope", clientSDK.getScopes());

            Map<String, Object> mergedAdditionalParameters = Utils.addAdditionalParameters(clientSDK.getAdditionalParameters(), additionalParameters);
            formData.setAll(mergedAdditionalParameters);

            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity(formData, headers);
            ResponseEntity<Object> response = restTemplate.postForEntity(
                    clientSDK.getTokenEndpoint(),
                    requestEntity,
                    Object.class
            );

            Object token = response.getBody();
            storage.setToken(resp,token);

//            return Utils.parseJWT(token);

            Map<String, Object> tokenMap = null;
            if (token instanceof Map) {
                // Convert tokenObject to Map
                tokenMap = (Map<String, Object>) token;

            } else {
                JsonNode jsonNode = new ObjectMapper().readTree((String) token);

                Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
                while (fields.hasNext()) {
                    Map.Entry<String, JsonNode> field = fields.next();
                    tokenMap.put(field.getKey(), field.getValue().asText());
                }
            }


            return tokenMap;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new HashMap<>();
//            throw new RuntimeException("Error during authentication: " + e.getMessage(), e);
        }
    }

    public Map<String, Object> authenticate(HttpServletResponse resp,KindeClientSDK clientSDK){
        return authenticate(resp,clientSDK,new HashMap<>());

    }
}
