package com.shakhawat.rbacabac.util;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@UtilityClass
public class PaginationUtil {

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 100;

    public Pageable createPageRequest(int page, int size, String sortBy, String direction) {
        var validatedSize = Math.min(size > 0 ? size : DEFAULT_PAGE_SIZE, MAX_PAGE_SIZE);
        var validatedPage = Math.max(page, 0);

        var sort = Sort.by(
                "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC,
                sortBy != null && !sortBy.isBlank() ? sortBy : "id"
        );

        return PageRequest.of(validatedPage, validatedSize, sort);
    }
}
