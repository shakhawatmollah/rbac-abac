package com.shakhawat.rbacabac.util;

import org.springframework.stereotype.Component;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class TokenGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();

    public String generateRandomToken(int length) {
        var bytes = new byte[length];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public String generateRefreshToken() {
        return generateRandomToken(32);
    }

    public String generatePasswordResetToken() {
        return generateRandomToken(24);
    }
}
