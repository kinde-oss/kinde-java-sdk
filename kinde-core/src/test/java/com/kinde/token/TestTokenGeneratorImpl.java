package com.kinde.token;

import com.google.inject.Inject;

public class TestTokenGeneratorImpl implements TestTokenGenerator {

    private TestKeyGenerator testKeyGenerator;

    @Inject
    public TestTokenGeneratorImpl(TestKeyGenerator testKeyGenerator) {
        this.testKeyGenerator = testKeyGenerator;
        System.out.println("The test token generator");
    }


    @Override
    public String generateAccessToken() {
        return "";
    }

    @Override
    public String generateIdToken() {
        return "";
    }

    @Override
    public String generateRefreshToken() {
        return "";
    }
}
