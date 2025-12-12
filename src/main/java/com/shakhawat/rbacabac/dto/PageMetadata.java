package com.shakhawat.rbacabac.dto;

import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PageMetadata {
    private int currentPage;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;
}
