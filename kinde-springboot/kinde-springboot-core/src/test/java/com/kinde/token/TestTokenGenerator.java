package com.kinde.token;

public interface TestTokenGenerator {
    String generateAccessToken();
    String generateIDToken();
    String refreshToken();
}
