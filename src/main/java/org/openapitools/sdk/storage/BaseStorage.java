package org.openapitools.sdk.storage;

import org.openapitools.sdk.enums.StorageEnums;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
//import org.springframework.web.util.CookieUtils;

public class BaseStorage {

//    private static final String PREFIX = "kinde";
//
//    public static String getStorage(HttpServletRequest request) {
//        Cookie[] cookies = request.getCookies();
//        if (cookies != null) {
//            for (Cookie cookie : cookies) {
//                if (cookie.getName().equals(PREFIX)) {
//                    return cookie.getValue();
//                }
//            }
//        }
//        return null;
//    }
//
//    public static String getItem(HttpServletRequest request, String key) {
//        String newKey = getKey(key);
//        Cookie[] cookies = request.getCookies();
//        if (cookies != null) {
//            for (Cookie cookie : cookies) {
//                if (cookie.getName().equals(newKey)) {
//                    return cookie.getValue();
//                }
//            }
//        }
//        return null;
//    }
//
//    public static void setItem(HttpServletResponse response, String key, String value, int expiresOrOptions, String path, String domain, boolean secure, boolean httpOnly) {
//        String newKey = getKey(key);
//        Cookie cookie = new Cookie(newKey, value);
//        cookie.setMaxAge(expiresOrOptions);
//        cookie.setPath(path);
//        cookie.setDomain(domain);
//        cookie.setSecure(secure);
//        cookie.setHttpOnly(httpOnly);
//        response.addCookie(cookie);
//    }
//
//    public static void removeItem(HttpServletResponse response, String key) {
//        String newKey = getKey(key);
//        Cookie cookie = new Cookie(newKey, "");
//        cookie.setMaxAge(0);
//        response.addCookie(cookie);
//    }
//
//    public static void clear(HttpServletResponse response) {
//        removeItem(response, StorageEnums.TOKEN.getValue());
//        removeItem(response, StorageEnums.STATE.getValue());
//        removeItem(response, StorageEnums.CODE_VERIFIER.getValue());
//        removeItem(response, StorageEnums.USER_PROFILE.getValue());
//    }

//
//    private static String getKey(String key) {
//        return PREFIX + '_' + key;
//    }
//


    private static final String PREFIX = "kinde";
    private static Map<String, String> storage;
//    public static Map<String, String> getStorage() {
//        if (storage == null) {
//            storage = new HashMap<>();
//        }
//        return storage;
//    }

    public static String getItem(HttpServletRequest request, String key) {
        String cookieName = getKey(key);
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    return cookie.getValue();
                }
            }
        }
        return "";
    }

    public static void setItem(HttpServletResponse response,String key, String value, int expiresOrOptions, String path, String domain, boolean secure, boolean httpOnly) {
        String newKey = getKey(key);
        Cookie cookie = new Cookie(newKey, value);
        cookie.setMaxAge(expiresOrOptions);
        cookie.setPath(path);
        cookie.setDomain(domain);
        cookie.setSecure(secure);
        cookie.setHttpOnly(httpOnly);
        response.addCookie(cookie);
    }

    public static void setItem(HttpServletResponse response,String key, String value){
        setItem(response,key,value,0,"","",true,false);
    }

    public static void setItem(HttpServletResponse response,String key, String value, int expiresOrOptions){
        setItem(response,key,value,expiresOrOptions,"","",true,false);
    }

    public static void removeItem(HttpServletResponse response, String key) {
        String cookieName = getKey(key);
        Cookie cookie = new Cookie(cookieName, "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public static void clear(HttpServletResponse response) {
        removeItem(response,StorageEnums.TOKEN.getValue());
        removeItem(response,StorageEnums.STATE.getValue());
        removeItem(response,StorageEnums.CODE_VERIFIER.getValue());
        removeItem(response,StorageEnums.USER_PROFILE.getValue());
    }


    private static String getKey(String key) {
        return PREFIX + '_' + key;
    }


//    private static final String PREFIX = "kinde";
//
//    private static Map<String, String> storage;
//
//    public static Map<String, String> getStorage() {
//        if (storage == null) {
//            storage = new HashMap<>();
//            String kindeCookie = CookieUtils.getCookie(PREFIX);
//            if (kindeCookie != null) {
//                String[] keyValuePairs = kindeCookie.split(";");
//                for (String keyValuePair : keyValuePairs) {
//                    String[] keyAndValue = keyValuePair.split("=");
//                    storage.put(keyAndValue[0], keyAndValue[1]);
//                }
//            }
//        }
//        return storage;
//    }
//
//    public static String getItem(String key) {
//        return getStorage().get(key);
//    }
//
//    public static void setItem(
//            String key,
//            String value,
//            int expires_or_options,
//            String path,
//            String domain,
//            boolean secure,
//            boolean httpOnly
//    ) {
//        String newKey = getKey(key);
//        storage.put(newKey, value);
//        CookieUtils.setCookie(newKey, value, expires_or_options, path, domain, secure, httpOnly);
//    }
//
//    public static void removeItem(String key) {
//        String newKey = getKey(key);
//        if (storage.containsKey(newKey)) {
//            storage.remove(newKey);
//            CookieUtils.deleteCookie(newKey);
//        }
//    }
//
//    public static void clear() {
//        for (StorageEnums storageEnum : StorageEnums.values()) {
//            removeItem(storageEnum.getValue());
//        }
//    }
//
//    private static String getKey(String key) {
//        return PREFIX + "_" + key;
//    }
}
