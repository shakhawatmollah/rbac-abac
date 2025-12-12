package com.shakhawat.rbacabac.exception;

import com.shakhawat.rbacabac.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {
        log.error("Resource not found: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponse.builder()
                        .success(false)
                        .message(ex.getMessage())
                        .timestamp(LocalDateTime.now().toString())
                        .build()
        );
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceAlreadyExistsException(
            ResourceAlreadyExistsException ex, WebRequest request) {
        log.error("Resource already exists: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ApiResponse.builder()
                        .success(false)
                        .message(ex.getMessage())
                        .timestamp(LocalDateTime.now().toString())
                        .build()
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDeniedException(
            AccessDeniedException ex, WebRequest request) {
        log.error("Access denied: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                ApiResponse.builder()
                        .success(false)
                        .message("Access denied: " + ex.getMessage())
                        .timestamp(LocalDateTime.now().toString())
                        .build()
        );
    }

    @ExceptionHandler({AuthenticationException.class, BadCredentialsException.class})
    public ResponseEntity<ApiResponse<Object>> handleAuthenticationException(
            Exception ex, WebRequest request) {
        log.error("Authentication failed: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ApiResponse.builder()
                        .success(false)
                        .message("Invalid email or password")
                        .timestamp(LocalDateTime.now().toString())
                        .build()
        );
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<Object>> handleUnauthorizedException(
            UnauthorizedException ex, WebRequest request) {
        log.error("Unauthorized: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ApiResponse.builder()
                        .success(false)
                        .message(ex.getMessage())
                        .timestamp(LocalDateTime.now().toString())
                        .build()
        );
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        log.error("Validation failed: {}", ex.getMessage());

        var errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse.builder()
                        .success(false)
                        .message("Validation failed")
                        .data(errors)
                        .timestamp(LocalDateTime.now().toString())
                        .build()
        );
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidRequestException(
            InvalidRequestException ex, WebRequest request) {
        log.error("Invalid request: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse.builder()
                        .success(false)
                        .message(ex.getMessage())
                        .timestamp(LocalDateTime.now().toString())
                        .build()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGlobalException(
            Exception ex, WebRequest request) {
        log.error("Unexpected error occurred", ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponse.builder()
                        .success(false)
                        .message("An unexpected error occurred: " + ex.getMessage())
                        .timestamp(LocalDateTime.now().toString())
                        .build()
        );
    }
}
