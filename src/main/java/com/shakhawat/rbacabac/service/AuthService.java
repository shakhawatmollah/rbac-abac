package com.shakhawat.rbacabac.service;

import com.shakhawat.rbacabac.dto.AuthResponse;
import com.shakhawat.rbacabac.dto.LoginRequest;
import com.shakhawat.rbacabac.exception.UnauthorizedException;
import com.shakhawat.rbacabac.security.JwtTokenProvider;
import com.shakhawat.rbacabac.security.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    public AuthResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        log.info("Login attempt for email: {}", request.getEmail());

        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        var accessToken = tokenProvider.generateToken(authentication);
        var userPrincipal = (UserPrincipal) authentication.getPrincipal();

        // Create refresh token
        assert userPrincipal != null;
        var refreshToken = refreshTokenService.createRefreshToken(
                userPrincipal.getId(),
                httpRequest
        );

        var roles = userPrincipal.getAuthorities().stream()
                .map(Object::toString)
                .collect(Collectors.toSet());

        log.info("User logged in successfully: {}", request.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .expiresIn(jwtExpiration / 1000) // Convert to seconds
                .id(userPrincipal.getId())
                .email(userPrincipal.getEmail())
                .roles(roles)
                .build();
    }

    public AuthResponse refreshToken(String refreshTokenStr, HttpServletRequest request) {
        log.info("Refresh token request");

        var refreshToken = refreshTokenService.verifyRefreshToken(refreshTokenStr);
        var employee = refreshToken.getEmployee();

        var userDetails = UserPrincipal.create(employee);
        var authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );

        var newAccessToken = tokenProvider.generateToken(authentication);

        // Optionally rotate refresh token (create new one and revoke old)
        refreshTokenService.revokeRefreshToken(refreshTokenStr);
        var newRefreshToken = refreshTokenService.createRefreshToken(employee.getId(), request);

        var roles = userDetails.getAuthorities().stream()
                .map(Object::toString)
                .collect(Collectors.toSet());

        log.info("Tokens refreshed successfully for user: {}", employee.getEmail());

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken.getToken())
                .expiresIn(jwtExpiration / 1000)
                .id(employee.getId())
                .email(employee.getEmail())
                .roles(roles)
                .build();
    }

    public void logout(String refreshToken) {
        log.info("Logout request");
        if (refreshToken != null && !refreshToken.isBlank()) {
            refreshTokenService.revokeRefreshToken(refreshToken);
        }
        SecurityContextHolder.clearContext();
        log.info("User logged out successfully");
    }

    public UserPrincipal getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            throw new UnauthorizedException("User is not authenticated");
        }

        var principal = authentication.getPrincipal();

        if (principal instanceof UserPrincipal userPrincipal) {
            return userPrincipal;
        }

        throw new UnauthorizedException("Access token expired or user not authenticated");
    }

}
