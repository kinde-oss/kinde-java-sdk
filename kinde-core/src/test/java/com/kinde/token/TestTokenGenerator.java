package com.kinde.token;

public interface TestTokenGenerator {

    String generateAccessToken();

    String generateIdToken();

    String generateRefreshToken();

}
