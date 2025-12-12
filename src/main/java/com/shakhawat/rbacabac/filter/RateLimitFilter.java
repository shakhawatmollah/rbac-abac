package com.shakhawat.rbacabac.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shakhawat.rbacabac.config.RateLimitingService;
import com.shakhawat.rbacabac.config.RateLimitingService.RateLimitType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitingService rateLimitingService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String clientId = getClientIdentifier(request);
        String path = request.getRequestURI();
        String method = request.getMethod();

        RateLimitType limitType = determineLimitType(path, method);

        if (!rateLimitingService.tryConsume(clientId, limitType)) {
            log.warn("Rate limit exceeded for client: {} on path: {} [{}]", clientId, path, method);
            sendRateLimitError(response, clientId, limitType);
            return;
        }

        // Add rate limit headers
        long remainingTokens = rateLimitingService.getAvailableTokens(clientId, limitType);
        response.addHeader("X-RateLimit-Limit", String.valueOf(limitType.getCapacity()));
        response.addHeader("X-RateLimit-Remaining", String.valueOf(remainingTokens));

        filterChain.doFilter(request, response);
    }

    private String getClientIdentifier(HttpServletRequest request) {
        var principal = request.getUserPrincipal();
        if (principal != null && principal.getName() != null) {
            return "user:" + principal.getName();
        }

        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null && !xfHeader.isBlank()) {
            return "ip:" + xfHeader.split(",")[0].trim();
        }

        return "ip:" + request.getRemoteAddr();
    }

    private RateLimitType determineLimitType(String path, String method) {
        if (path != null && path.contains("/auth/login")) {
            return RateLimitType.LOGIN;
        }

        return switch (method) {
            case "POST", "PUT", "DELETE", "PATCH" -> RateLimitType.API_WRITE;
            case "GET" -> RateLimitType.API_READ;
            default -> RateLimitType.API_GENERAL;
        };
    }

    private void sendRateLimitError(HttpServletResponse response,
                                    String clientId,
                                    RateLimitType limitType) throws IOException {

        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> errorResponse = Map.of(
                "success", false,
                "message", "Rate limit exceeded. Please try again later.",
                "details", String.format("Limit: %d requests per %s",
                        limitType.getCapacity(),
                        formatDuration(limitType.getRefillDuration())),
                "timestamp", LocalDateTime.now().toString()
        );

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

    private String formatDuration(Duration duration) {
        if (duration == null) return "unknown";

        long seconds = duration.getSeconds();

        if (seconds < 60) {
            return seconds + " second" + (seconds != 1 ? "s" : "");
        } else if (seconds < 3600) {
            long minutes = seconds / 60;
            return minutes + " minute" + (minutes != 1 ? "s" : "");
        } else {
            long hours = seconds / 3600;
            return hours + " hour" + (hours != 1 ? "s" : "");
        }
    }
}
