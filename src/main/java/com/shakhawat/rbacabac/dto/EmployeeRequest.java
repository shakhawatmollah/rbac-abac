package com.shakhawat.rbacabac.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.util.Set;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class EmployeeRequest {
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50)
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    private String department;
    private String position;

    @DecimalMin(value = "0.0", inclusive = false)
    private Double salary;

    private Boolean active;
    private Set<String> roles;
}
