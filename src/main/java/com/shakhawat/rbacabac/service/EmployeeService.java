package com.shakhawat.rbacabac.service;

import com.shakhawat.rbacabac.dto.EmployeeRequest;
import com.shakhawat.rbacabac.dto.EmployeeResponse;
import com.shakhawat.rbacabac.entity.Employee;
import com.shakhawat.rbacabac.entity.RoleType;
import com.shakhawat.rbacabac.exception.ResourceAlreadyExistsException;
import com.shakhawat.rbacabac.exception.ResourceNotFoundException;
import com.shakhawat.rbacabac.repository.EmployeeRepository;
import com.shakhawat.rbacabac.repository.RoleRepository;
import com.shakhawat.rbacabac.util.EmployeeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmployeeMapper employeeMapper;

    public EmployeeResponse createEmployee(EmployeeRequest request) {
        log.info("Creating employee with email: {}", request.getEmail());

        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("Employee with email already exists: " + request.getEmail());
        }

        var employee = Employee.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .department(request.getDepartment())
                .position(request.getPosition())
                .salary(request.getSalary())
                .active(request.getActive() != null ? request.getActive() : true)
                .build();

        // Assign roles
        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            var roles = request.getRoles().stream()
                    .map(roleName -> roleRepository.findByName(RoleType.valueOf(roleName))
                            .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName)))
                    .collect(Collectors.toSet());
            employee.setRoles(roles);
        } else {
            // Default role
            var defaultRole = roleRepository.findByName(RoleType.ROLE_EMPLOYEE)
                    .orElseThrow(() -> new ResourceNotFoundException("Default role not found"));
            employee.setRoles(Set.of(defaultRole));
        }

        var savedEmployee = employeeRepository.save(employee);
        log.info("Employee created successfully with id: {}", savedEmployee.getId());

        return employeeMapper.toResponse(savedEmployee);
    }

    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeById(Long id) {
        log.info("Fetching employee with id: {}", id);

        var employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

        return employeeMapper.toResponse(employee);
    }

    @Transactional(readOnly = true)
    public Page<EmployeeResponse> getAllEmployees(Pageable pageable) {
        log.info("Fetching employees with pagination: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());

        return employeeRepository.findAll(pageable)
                .map(employeeMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<EmployeeResponse> getEmployeesByDepartment(String department, Pageable pageable) {
        log.info("Fetching employees by department: {} with pagination", department);

        return employeeRepository.findByDepartment(department, pageable)
                .map(employeeMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<EmployeeResponse> searchEmployees(String searchTerm, Pageable pageable) {
        log.info("Searching employees with term: {} with pagination", searchTerm);

        return employeeRepository.searchEmployees(searchTerm, pageable)
                .map(employeeMapper::toResponse);
    }

    public EmployeeResponse updateEmployee(Long id, EmployeeRequest request) {
        log.info("Updating employee with id: {}", id);

        var employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

        // Check email uniqueness if changed
        if (!employee.getEmail().equals(request.getEmail())) {
            if (employeeRepository.existsByEmail(request.getEmail())) {
                throw new ResourceAlreadyExistsException("Email already in use: " + request.getEmail());
            }
            employee.setEmail(request.getEmail());
        }

        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setDepartment(request.getDepartment());
        employee.setPosition(request.getPosition());
        employee.setSalary(request.getSalary());

        if (request.getActive() != null) {
            employee.setActive(request.getActive());
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            employee.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        // Update roles if provided
        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            var roles = request.getRoles().stream()
                    .map(roleName -> roleRepository.findByName(RoleType.valueOf(roleName))
                            .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName)))
                    .collect(Collectors.toSet());
            employee.setRoles(roles);
        }

        var updatedEmployee = employeeRepository.save(employee);
        log.info("Employee updated successfully with id: {}", updatedEmployee.getId());

        return employeeMapper.toResponse(updatedEmployee);
    }

    public void deleteEmployee(Long id) {
        log.info("Deleting employee with id: {}", id);

        var employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

        employeeRepository.delete(employee);
        log.info("Employee deleted successfully with id: {}", id);
    }

    public EmployeeResponse activateEmployee(Long id) {
        log.info("Activating employee with id: {}", id);

        var employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

        employee.setActive(true);
        var updatedEmployee = employeeRepository.save(employee);

        return employeeMapper.toResponse(updatedEmployee);
    }

    public EmployeeResponse deactivateEmployee(Long id) {
        log.info("Deactivating employee with id: {}", id);

        var employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

        employee.setActive(false);
        var updatedEmployee = employeeRepository.save(employee);

        return employeeMapper.toResponse(updatedEmployee);
    }
}
