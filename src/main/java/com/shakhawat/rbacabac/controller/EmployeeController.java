package com.shakhawat.rbacabac.controller;

import com.shakhawat.rbacabac.dto.ApiResponse;
import com.shakhawat.rbacabac.dto.EmployeeRequest;
import com.shakhawat.rbacabac.dto.EmployeeResponse;
import com.shakhawat.rbacabac.dto.PageMetadata;
import com.shakhawat.rbacabac.security.*;
import com.shakhawat.rbacabac.service.EmployeeService;
import com.shakhawat.rbacabac.util.PaginationUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    @CanCreateEmployee
    public ResponseEntity<ApiResponse<EmployeeResponse>> createEmployee(
            @Valid @RequestBody EmployeeRequest request) {
        log.info("POST /api/employees - Create employee");

        var employee = employeeService.createEmployee(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<EmployeeResponse>builder()
                        .success(true)
                        .message("Employee created successfully")
                        .data(employee)
                        .timestamp(LocalDateTime.now().toString())
                        .build()
        );
    }

    @GetMapping
    @CanReadEmployee
    public ResponseEntity<ApiResponse<List<EmployeeResponse>>> getAllEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        log.info("GET /api/employees - Get all employees (page={}, size={})", page, size);

        var pageable = PaginationUtil.createPageRequest(page, size, sortBy, direction);

        var employeesPage = employeeService.getAllEmployees(pageable);

        return ResponseEntity.ok(
                ApiResponse.<List<EmployeeResponse>>builder()
                        .success(true)
                        .message("Employees retrieved successfully")
                        .data(employeesPage.getContent())
                        .pagination(createPageMetadata(employeesPage))
                        .timestamp(LocalDateTime.now().toString())
                        .build()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("@employeePermissionEvaluator.canView(#id)")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getEmployeeById(@PathVariable Long id) {
        log.info("GET /api/employees/{} - Get employee by id", id);

        var employee = employeeService.getEmployeeById(id);

        return ResponseEntity.ok(
                ApiResponse.<EmployeeResponse>builder()
                        .success(true)
                        .message("Employee retrieved successfully")
                        .data(employee)
                        .timestamp(LocalDateTime.now().toString())
                        .build()
        );
    }

    @GetMapping("/department/{department}")
    @IsManagerOrAbove
    public ResponseEntity<ApiResponse<List<EmployeeResponse>>> getEmployeesByDepartment(
            @PathVariable String department,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        log.info("GET /api/employees/department/{} - Get employees by department", department);

        var pageable = PaginationUtil.createPageRequest(page, size, sortBy, direction);

        var employeesPage = employeeService.getEmployeesByDepartment(department, pageable);

        return ResponseEntity.ok(
                ApiResponse.<List<EmployeeResponse>>builder()
                        .success(true)
                        .message("Employees retrieved successfully")
                        .data(employeesPage.getContent())
                        .pagination(createPageMetadata(employeesPage))
                        .timestamp(LocalDateTime.now().toString())
                        .build()
        );
    }

    @GetMapping("/search")
    @CanReadEmployee
    public ResponseEntity<ApiResponse<List<EmployeeResponse>>> searchEmployees(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        log.info("GET /api/employees/search?query={} - Search employees", query);

        var sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        var pageable = PageRequest.of(page, Math.min(size, 100), sort);

        var employeesPage = employeeService.searchEmployees(query, pageable);

        return ResponseEntity.ok(
                ApiResponse.<List<EmployeeResponse>>builder()
                        .success(true)
                        .message("Search completed successfully")
                        .data(employeesPage.getContent())
                        .pagination(createPageMetadata(employeesPage))
                        .timestamp(LocalDateTime.now().toString())
                        .build()
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("@employeePermissionEvaluator.canModify(#id)")
    public ResponseEntity<ApiResponse<EmployeeResponse>> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeRequest request) {
        log.info("PUT /api/employees/{} - Update employee", id);

        var employee = employeeService.updateEmployee(id, request);

        return ResponseEntity.ok(
                ApiResponse.<EmployeeResponse>builder()
                        .success(true)
                        .message("Employee updated successfully")
                        .data(employee)
                        .timestamp(LocalDateTime.now().toString())
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @CanDeleteEmployee
    public ResponseEntity<ApiResponse<Void>> deleteEmployee(@PathVariable Long id) {
        log.info("DELETE /api/employees/{} - Delete employee", id);

        employeeService.deleteEmployee(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Employee deleted successfully")
                        .timestamp(LocalDateTime.now().toString())
                        .build()
        );
    }

    @PatchMapping("/{id}/activate")
    @IsManagerOrAbove
    public ResponseEntity<ApiResponse<EmployeeResponse>> activateEmployee(@PathVariable Long id) {
        log.info("PATCH /api/employees/{}/activate - Activate employee", id);

        var employee = employeeService.activateEmployee(id);

        return ResponseEntity.ok(
                ApiResponse.<EmployeeResponse>builder()
                        .success(true)
                        .message("Employee activated successfully")
                        .data(employee)
                        .timestamp(LocalDateTime.now().toString())
                        .build()
        );
    }

    @PatchMapping("/{id}/deactivate")
    @IsManagerOrAbove
    public ResponseEntity<ApiResponse<EmployeeResponse>> deactivateEmployee(@PathVariable Long id) {
        log.info("PATCH /api/employees/{}/deactivate - Deactivate employee", id);

        var employee = employeeService.deactivateEmployee(id);

        return ResponseEntity.ok(
                ApiResponse.<EmployeeResponse>builder()
                        .success(true)
                        .message("Employee deactivated successfully")
                        .data(employee)
                        .timestamp(LocalDateTime.now().toString())
                        .build()
        );
    }

    private PageMetadata createPageMetadata(Page<?> page) {
        return PageMetadata.builder()
                .currentPage(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }
}
