package com.shakhawat.rbacabac.dto;

import lombok.*;
import java.util.Set;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String type = "Bearer";
    private Long expiresIn;
    private Long id;
    private String email;
    private Set<String> roles;
}
