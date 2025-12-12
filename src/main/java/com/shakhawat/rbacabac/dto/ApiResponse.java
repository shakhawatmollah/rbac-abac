package com.shakhawat.rbacabac.dto;

import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private PageMetadata pagination;
    private String timestamp;
}
