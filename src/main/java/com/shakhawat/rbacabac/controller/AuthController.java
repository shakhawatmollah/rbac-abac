package com.shakhawat.rbacabac.controller;

import com.shakhawat.rbacabac.dto.ApiResponse;
import com.shakhawat.rbacabac.dto.AuthResponse;
import com.shakhawat.rbacabac.dto.LoginRequest;
import com.shakhawat.rbacabac.dto.RefreshTokenRequest;
import com.shakhawat.rbacabac.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {
        log.info("POST /api/auth/login - Login attempt");

        var authResponse = authService.login(request, httpRequest);

        return ResponseEntity.ok(
                ApiResponse.<AuthResponse>builder()
                        .success(true)
                        .message("Login successful")
                        .data(authResponse)
                        .timestamp(LocalDateTime.now().toString())
                        .build()
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request,
            HttpServletRequest httpRequest) {
        log.info("POST /api/auth/refresh - Refresh token request");

        var authResponse = authService.refreshToken(request.getRefreshToken(), httpRequest);

        return ResponseEntity.ok(
                ApiResponse.<AuthResponse>builder()
                        .success(true)
                        .message("Token refreshed successfully")
                        .data(authResponse)
                        .timestamp(LocalDateTime.now().toString())
                        .build()
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestBody(required = false) RefreshTokenRequest request) {
        log.info("POST /api/auth/logout - Logout request");

        var refreshToken = request != null ? request.getRefreshToken() : null;
        authService.logout(refreshToken);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Logout successful")
                        .timestamp(LocalDateTime.now().toString())
                        .build()
        );
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<?>> getCurrentUser() {
        log.info("GET /api/auth/me - Get current user");

        var user = authService.getCurrentUser();

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Current user retrieved successfully")
                        .data(user)
                        .timestamp(LocalDateTime.now().toString())
                        .build()
        );
    }
}
