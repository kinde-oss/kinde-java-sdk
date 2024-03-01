package org.openapitools.sdk.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openapitools.sdk.enums.StorageEnums;
import org.openapitools.sdk.utils.Utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class Storage extends BaseStorage {



    private static Storage instance;
    private static Long tokenTimeToLive;

    private static final Map<String, String> storageMap = new ConcurrentHashMap<>();

    public static Storage getInstance() {
        if (instance == null) {
            instance = new Storage();
        }
        return instance;
    }

//    public static Map<String, String> getStorageMap() {
//        return storageMap;
//    }

    public static Map<String, Object> getToken(HttpServletRequest request) {
        try{
            String token = getItem(request,StorageEnums.TOKEN.getValue());
            //        return Optional.ofNullable(token).map(Utils::jsonDecode);
            //        String token = getStorageMap().get(StorageEnums.TOKEN);
            //        return token != null ? token : null;
            if (token.equals("")){
                return null;
            }
            String decodedToken = java.net.URLDecoder.decode(token, "UTF-8");
            return new ObjectMapper().readValue(decodedToken, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            return null;
        }
    }

    public static void setToken(HttpServletResponse response,Object token) {
//        getStorageMap().put(StorageEnums.TOKEN.getValue(), token);
//        setTokenTimeToLive(tokenTimeToLive);
        String tok="";
        if (token instanceof String) {
            tok= (String) token;
        } else if(token instanceof Map){
            try {
                tok = URLEncoder.encode(new ObjectMapper().writeValueAsString((Map<String, Object>) token), "UTF-8");
            }catch (Exception e){
                throw new IllegalArgumentException("Invalid output");
            }
        }

        setItem(response,StorageEnums.TOKEN.getValue(), tok, getTokenTimeToLive().intValue());
    }

    public static String getAccessToken(HttpServletRequest request) {
        Map<String,Object> token = getToken(request);
        return token != null ? (String) token.get("access_token") : null;
    }

    public static String getIdToken(HttpServletRequest request) {
        Map<String,Object> token = getToken(request);
        return token != null ? (String) token.get("id_token") : null;
    }

    public static String getRefreshToken(HttpServletRequest request) {
        Map<String,Object> token = getToken(request);
        return token != null ? (String) token.get("refresh_token") : null;
    }

    public static Long getExpiredAt(HttpServletRequest request) {
        String accessToken = getAccessToken(request);
//        return accessToken != null ? Long.parseLong((String) Utils.parseJWT(accessToken).get("exp")) : 0L;
        return accessToken != null ? ((Integer) Utils.parseJWT(accessToken).get("exp")).longValue() : 0L;
    }

    public static Long getTokenTimeToLive() {
        return tokenTimeToLive != null ? tokenTimeToLive : System.currentTimeMillis() + 3600 * 24 * 15 * 1000; // Live in 15 days
    }

    public static void setTokenTimeToLive(Long tokenTTL) {
        tokenTimeToLive = tokenTTL;
    }
//
    public static String getState(HttpServletRequest request) {
        return getItem(request,StorageEnums.STATE.getValue());
    }

    public static void setState( HttpServletResponse response,String newState) {
        setItem(response,StorageEnums.STATE.getValue(), newState,(int) ((long) (System.currentTimeMillis() + 3600 *2 )));
        // set expiration time for state
    }

    public static String getCodeVerifier(HttpServletRequest request) {
        return getItem(request,StorageEnums.CODE_VERIFIER.getValue());
    }

    public static void setCodeVerifier(HttpServletResponse response,String newCodeVerifier) {
        setItem(response,StorageEnums.CODE_VERIFIER.getValue(), newCodeVerifier,(int) ((long) (System.currentTimeMillis() + 3600 *2 )));
        // set expiration time for code verifier
    }


    public static Map<String, Object> getUserProfile(HttpServletRequest request) {
        Map<String, Object> token = getToken(request);
        String idToken = getIdToken(request);
        Map<String, Object> payload = Utils.parseJWT(idToken);

        Map<String, Object> userProfile = new ConcurrentHashMap<>();
        userProfile.put("id", payload.containsKey("sub") && payload.get("sub")!=null ? payload.get("sub") : "");
        userProfile.put("given_name",payload.containsKey("given_name") && payload.get("given_name")!=null ? payload.get("given_name") : "");
        userProfile.put("family_name", payload.containsKey("family_name") && payload.get("family_name")!=null ? payload.get("family_name") : "");
        userProfile.put("email",payload.containsKey("email") && payload.get("email")!=null ? payload.get("email") : "");
        userProfile.put("picture",payload.containsKey("picture") && payload.get("picture")!=null ? payload.get("picture") : "");

        return userProfile;
    }
}
