package com.shakhawat.rbacabac.service;

import com.shakhawat.rbacabac.entity.RefreshToken;
import com.shakhawat.rbacabac.exception.ResourceNotFoundException;
import com.shakhawat.rbacabac.exception.UnauthorizedException;
import com.shakhawat.rbacabac.repository.EmployeeRepository;
import com.shakhawat.rbacabac.repository.RefreshTokenRepository;
import com.shakhawat.rbacabac.util.TokenGenerator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final EmployeeRepository employeeRepository;
    private final TokenGenerator tokenGenerator;

    @Value("${jwt.refresh-expiration:604800000}") // 7 days in milliseconds
    private long refreshTokenExpiration;

    public RefreshToken createRefreshToken(Long employeeId, HttpServletRequest request) {
        var employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        var token = tokenGenerator.generateRefreshToken();
        var expiryDate = LocalDateTime.now()
                .plusSeconds(refreshTokenExpiration / 1000);

        var refreshToken = RefreshToken.builder()
                .token(token)
                .employee(employee)
                .expiryDate(expiryDate)
                .revoked(false)
                .ipAddress(getClientIP(request))
                .userAgent(request.getHeader("User-Agent"))
                .build();

        var savedToken = refreshTokenRepository.save(refreshToken);
        log.info("Refresh token created for employee: {}", employeeId);

        return savedToken;
    }

    public RefreshToken verifyRefreshToken(String token) {
        var refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (refreshToken.getRevoked()) {
            log.warn("Attempted to use revoked refresh token");
            throw new UnauthorizedException("Refresh token has been revoked");
        }

        if (refreshToken.isExpired()) {
            refreshTokenRepository.delete(refreshToken);
            log.warn("Attempted to use expired refresh token");
            throw new UnauthorizedException("Refresh token has expired");
        }

        // Update last used timestamp
        refreshToken.setLastUsedAt(LocalDateTime.now());
        refreshTokenRepository.save(refreshToken);

        return refreshToken;
    }

    public void revokeRefreshToken(String token) {
        var refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Refresh token not found"));

        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
        log.info("Refresh token revoked");
    }

    public void revokeAllUserTokens(Long employeeId) {
        refreshTokenRepository.revokeAllByEmployeeId(employeeId);
        log.info("All refresh tokens revoked for employee: {}", employeeId);
    }

    @Scheduled(cron = "0 0 2 * * ?") // Run daily at 2 AM
    public void cleanupExpiredTokens() {
        log.info("Running cleanup of expired refresh tokens");
        refreshTokenRepository.deleteExpiredTokens(LocalDateTime.now());
        log.info("Expired refresh tokens cleaned up");
    }

    private String getClientIP(HttpServletRequest request) {
        var xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
