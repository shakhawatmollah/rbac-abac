package com.shakhawat.rbacabac.util;

import org.springframework.stereotype.Component;
import java.util.regex.Pattern;

@Component
public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$"
    );

    public boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public boolean isStrongPassword(String password) {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }

    public String getPasswordStrengthMessage() {
        return """
            Password must contain:
            - At least 8 characters
            - One uppercase letter
            - One lowercase letter
            - One digit
            - One special character (@#$%^&+=)
            - No whitespace
            """;
    }
}
