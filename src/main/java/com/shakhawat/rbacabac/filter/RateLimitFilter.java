package com.shakhawat.rbacabac.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shakhawat.rbacabac.config.RateLimitingService;
import com.shakhawat.rbacabac.config.RateLimitingService.RateLimitType;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
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

        var clientId = getClientIdentifier(request);
        var path = request.getRequestURI();
        var method = request.getMethod();

        var limitType = determineLimitType(path, method);

        if (!rateLimitingService.tryConsume(clientId, limitType)) {
            log.warn("Rate limit exceeded for client: {} on path: {}", clientId, path);
            sendRateLimitError(response, clientId, limitType);
            return;
        }

        // Add rate limit headers
        var availableTokens = rateLimitingService.getAvailableTokens(clientId, limitType);
        response.addHeader("X-RateLimit-Limit", String.valueOf(limitType.getCapacity()));
        response.addHeader("X-RateLimit-Remaining", String.valueOf(availableTokens));

        filterChain.doFilter(request, response);
    }

    private String getClientIdentifier(HttpServletRequest request) {
        // Try to get authenticated user email first
        var principal = request.getUserPrincipal();
        if (principal != null) {
            return "user:" + principal.getName();
        }

        // Fall back to IP address
        var xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null) {
            return "ip:" + xfHeader.split(",")[0];
        }
        return "ip:" + request.getRemoteAddr();
    }

    private RateLimitType determineLimitType(String path, String method) {
        // Login endpoint has stricter limits
        if (path.contains("/auth/login")) {
            return RateLimitType.LOGIN;
        }

        // Write operations (POST, PUT, DELETE, PATCH)
        if (method.equals("POST") || method.equals("PUT") ||
                method.equals("DELETE") || method.equals("PATCH")) {
            return RateLimitType.API_WRITE;
        }

        // Read operations (GET)
        if (method.equals("GET")) {
            return RateLimitType.API_READ;
        }

        return RateLimitType.API_GENERAL;
    }

    private void sendRateLimitError(HttpServletResponse response,
                                    String clientId,
                                    RateLimitType limitType) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        var errorResponse = Map.of(
                "success", false,
                "message", "Rate limit exceeded. Please try again later.",
                "details", String.format("Limit: %d requests per %s",
                        limitType.getCapacity(),
                        formatDuration(limitType.getRefillDuration())),
                "timestamp", LocalDateTime.now().toString()
        );

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

    private String formatDuration(java.time.Duration duration) {
        var minutes = duration.toMinutes();
        if (minutes < 60) {
            return minutes + " minute" + (minutes != 1 ? "s" : "");
        }
        var hours = duration.toHours();
        return hours + " hour" + (hours != 1 ? "s" : "");
    }
}
