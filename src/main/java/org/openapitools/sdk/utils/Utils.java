package org.openapitools.sdk.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openapitools.sdk.enums.AdditionalParameters;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;
import java.util.regex.Pattern;

public class Utils {
    public static Map<String, String> listType = new HashMap<>();
    static {
        listType.put("s", "string");
        listType.put("i", "integer");
        listType.put("b", "boolean");
    }

    public static String base64UrlEncode(String str) {
        String base64 = Base64.getEncoder().encodeToString(str.getBytes());
        base64 = base64.replaceAll("=", "");
        base64 = base64.replaceAll("\\+", "");
        base64 = base64.replaceAll("/", "");
        String base64url = base64;
//        String base64url = base64.replace('+', '-').replace('/', '_');
        return base64url;
    }

    public static String sha256(String str) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(str.getBytes());
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    public static String randomString(int length) {
        byte[] randomBytes = new byte[length];
        new SecureRandom().nextBytes(randomBytes);
        return base64UrlEncode(new String(randomBytes));
    }


    public static String randomString() {
        return randomString(32);
    }


    public static Map<String, String> generateChallenge() {
        String state = randomString();
//        String codeVerifier = randomString();
        String codeVerifier = generateRandomCodeVerifier();
//        String codeChallenge = base64UrlEncode(hash("SHA-256", codeVerifier));
//        String codeChallenge = base64UrlEncode(sha256(codeVerifier));
        String codeChallenge = generateCodeChallenge(codeVerifier);
        Map<String, String> result = new HashMap<>();
        result.put("state", state);
        result.put("codeVerifier", codeVerifier);
        result.put("codeChallenge", codeChallenge);
        return result;
    }

    public static String generateRandomCodeVerifier() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[32]; // At least 43 bytes for a valid PKCE code verifier

        // Generate random bytes
        secureRandom.nextBytes(randomBytes);

        // Encode the random bytes to a URL-safe Base64 string
        String codeVerifier = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

        return codeVerifier;
    }

    public static String generateCodeChallenge(String codeVerifier) {
        try {
            // Create a message digest instance for SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Generate a byte array from the codeVerifier string
            byte[] codeVerifierBytes = codeVerifier.getBytes();

            // Update the digest with the codeVerifier bytes
            digest.update(codeVerifierBytes);

            // Calculate the digest value
            byte[] digestBytes = digest.digest();

            // Base64 URL-encode the digest value
            String codeChallenge = Base64.getUrlEncoder().withoutPadding().encodeToString(digestBytes);

            return codeChallenge;
        } catch (NoSuchAlgorithmException e) {
            // Handle the exception appropriately (e.g., log or throw a custom exception)
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    public static boolean validateURL(String url) {
        String pattern = "https?://(?:w{1,3}\\.)?[^\\s.]+(?:\\.[a-z]+)*(?::\\d+)?(?![^<]*(?:<\\/?\\w+>|\\/?>))";
//        return Pattern.matches(pattern,url);
        return url.matches(pattern);
    }

    public static Map<String, Object> parseJWT(String token) {
        try {

            String[] tokenParts = token.split("\\.");
            String encodedPayload = tokenParts[1];

            // Replace underscores with slashes and dashes with plus signs for valid base64 encoding
            encodedPayload = encodedPayload.replace('_', '/').replace('-', '+');

            // Decode the base64-encoded payload
//            String decodedPayload = new String(Base64.getUrlDecoder().decode(encodedPayload));
//            return new HashMap<>((Map<String, Object>) JSON.parse(Base64.getDecoder().decode(encodedPayload)));
            byte[] decodedBytes = Base64.getDecoder().decode(encodedPayload);
            String decodedPayload = new String(decodedBytes);
//            return new ObjectMapper().readValue(decodedPayload, HashMap.class);
            return new ObjectMapper().readValue(decodedPayload, new TypeReference<Map<String,Object>>(){});
        } catch (Exception e) {
            return null;
        }
    }

//    public static Map<String, String> checkAdditionalParameters(Map<String, String> additionalParameters) {
//        if (additionalParameters.isEmpty()) {
//            return new HashMap<>();
//        }
//        Map<String, String> additionalParametersValid = AdditionalParameters.ADDITIONAL_PARAMETER;
//        Set<String> keysAvailable = additionalParametersValid.keySet();
//        for (String key : additionalParameters.keySet()) {
//            if (!keysAvailable.contains(key)) {
//                throw new IllegalArgumentException("Please provide correct additional, " + key);
//            }
//            if (listType.get(key) != additionalParameters.get(key).getClass().getSimpleName()) {
//                throw new IllegalArgumentException("Please supply a valid " + key + ". Expected: " + listType.get(key));
//            }
//        }
//        return additionalParameters;
//    }

    public static Map<String, Object> checkAdditionalParameters(Map<String, Object> additionalParameters) {
        if (additionalParameters==null || additionalParameters.isEmpty()) {
            return new HashMap<>();
        }
        Map<String, String> additionalParametersValid = AdditionalParameters.ADDITIONAL_PARAMETER;
        String[] keysAvailable = additionalParametersValid.keySet().toArray(new String[0]);

        for (String key : additionalParameters.keySet()) {
            if (!Arrays.asList(keysAvailable).contains(key)) {
                throw new IllegalArgumentException("Please provide correct additional, " + key);
            }

            if (!additionalParameters.get(key).getClass().getSimpleName().equalsIgnoreCase(additionalParametersValid.get(key))) {
                throw new IllegalArgumentException("Please supply a valid " + key + ". Expected: " + additionalParametersValid.get(key));
            }
        }
        return additionalParameters;
    }

    public static Map<String, Object> addAdditionalParameters(Map<String, Object> target, Map<String, Object> additionalParameters) {
        Map<String, Object> newAdditionalParameters = checkAdditionalParameters(additionalParameters);
        Map<String, Object> mutableTarget = new HashMap<>(target);
        if (newAdditionalParameters!=null && !newAdditionalParameters.isEmpty()) {
            mutableTarget.putAll(newAdditionalParameters);
        }
        return mutableTarget;
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

}