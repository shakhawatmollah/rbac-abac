package com.shakhawat.rbacabac.util;

import com.shakhawat.rbacabac.dto.EmployeeResponse;
import com.shakhawat.rbacabac.entity.Employee;
import org.springframework.stereotype.Component;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@Component
public class EmployeeMapper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public EmployeeResponse toResponse(Employee employee) {
        if (employee == null) {
            return null;
        }

        var roles = employee.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());

        return EmployeeResponse.builder()
                .id(employee.getId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .department(employee.getDepartment())
                .position(employee.getPosition())
                .salary(employee.getSalary())
                .active(employee.getActive())
                .roles(roles)
                .createdAt(employee.getCreatedAt().format(FORMATTER))
                .updatedAt(employee.getUpdatedAt().format(FORMATTER))
                .build();
    }
}
