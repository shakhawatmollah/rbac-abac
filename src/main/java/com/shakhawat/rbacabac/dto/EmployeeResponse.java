package com.shakhawat.rbacabac.dto;

import lombok.*;
import java.util.Set;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class EmployeeResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String department;
    private String position;
    private Double salary;
    private Boolean active;
    private Set<String> roles;
    private String createdAt;
    private String updatedAt;
}
