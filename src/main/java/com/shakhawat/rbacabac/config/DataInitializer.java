package com.shakhawat.rbacabac.config;

import com.shakhawat.rbacabac.entity.Employee;
import com.shakhawat.rbacabac.entity.Permission;
import com.shakhawat.rbacabac.entity.Role;
import com.shakhawat.rbacabac.entity.RoleType;
import com.shakhawat.rbacabac.repository.EmployeeRepository;
import com.shakhawat.rbacabac.repository.PermissionRepository;
import com.shakhawat.rbacabac.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        log.info("Initializing database with default data...");

        initializePermissions();
        initializeRoles();
        initializeDefaultUsers();

        log.info("Database initialization completed successfully");
    }

    private void initializePermissions() {
        if (permissionRepository.count() > 0) {
            log.info("Permissions already exist, skipping initialization");
            return;
        }

        var permissions = new Permission[]{
                createPermission("READ_EMPLOYEE", "EMPLOYEE", "READ", "Read employee information"),
                createPermission("CREATE_EMPLOYEE", "EMPLOYEE", "CREATE", "Create new employee"),
                createPermission("UPDATE_EMPLOYEE", "EMPLOYEE", "UPDATE", "Update employee information"),
                createPermission("DELETE_EMPLOYEE", "EMPLOYEE", "DELETE", "Delete employee"),
                createPermission("READ_ROLE", "ROLE", "READ", "Read role information"),
                createPermission("MANAGE_ROLE", "ROLE", "MANAGE", "Manage roles and permissions")
        };

        permissionRepository.saveAll(Set.of(permissions));
        log.info("Permissions initialized: {}", permissions.length);
    }

    private void initializeRoles() {
        if (roleRepository.count() > 0) {
            log.info("Roles already exist, skipping initialization");
            return;
        }

        // Admin role - full access
        var adminPerms = permissionRepository.findAll();
        var adminRole = Role.builder()
                .name(RoleType.ROLE_ADMIN)
                .description("Administrator with full system access")
                .permissions(Set.copyOf(adminPerms))
                .build();

        // Manager role - read and update
        var managerPerms = Set.of(
                permissionRepository.findByName("READ_EMPLOYEE").orElseThrow(),
                permissionRepository.findByName("CREATE_EMPLOYEE").orElseThrow(),
                permissionRepository.findByName("UPDATE_EMPLOYEE").orElseThrow(),
                permissionRepository.findByName("READ_ROLE").orElseThrow()
        );
        var managerRole = Role.builder()
                .name(RoleType.ROLE_MANAGER)
                .description("Manager with employee management access")
                .permissions(managerPerms)
                .build();

        // HR role - employee CRUD but not delete
        var hrPerms = Set.of(
                permissionRepository.findByName("READ_EMPLOYEE").orElseThrow(),
                permissionRepository.findByName("CREATE_EMPLOYEE").orElseThrow(),
                permissionRepository.findByName("UPDATE_EMPLOYEE").orElseThrow()
        );
        var hrRole = Role.builder()
                .name(RoleType.ROLE_HR)
                .description("HR personnel with employee data access")
                .permissions(hrPerms)
                .build();

        // Employee role - read only
        var employeePerms = Set.of(
                permissionRepository.findByName("READ_EMPLOYEE").orElseThrow()
        );
        var employeeRole = Role.builder()
                .name(RoleType.ROLE_EMPLOYEE)
                .description("Regular employee with limited access")
                .permissions(employeePerms)
                .build();

        roleRepository.saveAll(Set.of(adminRole, managerRole, hrRole, employeeRole));
        log.info("Roles initialized: 4 roles created");
    }

    private void initializeDefaultUsers() {
        if (employeeRepository.count() > 0) {
            log.info("Users already exist, skipping initialization");
            return;
        }

        // Create admin user
        var adminRole = roleRepository.findByName(RoleType.ROLE_ADMIN).orElseThrow();
        var admin = Employee.builder()
                .firstName("System")
                .lastName("Administrator")
                .email("admin@company.com")
                .password(passwordEncoder.encode("Admin@123"))
                .department("IT")
                .position("System Administrator")
                .salary(100000.0)
                .active(true)
                .roles(Set.of(adminRole))
                .build();

        // Create manager user
        var managerRole = roleRepository.findByName(RoleType.ROLE_MANAGER).orElseThrow();
        var manager = Employee.builder()
                .firstName("John")
                .lastName("Manager")
                .email("manager@company.com")
                .password(passwordEncoder.encode("Manager@123"))
                .department("Operations")
                .position("Operations Manager")
                .salary(80000.0)
                .active(true)
                .roles(Set.of(managerRole))
                .build();

        // Create HR user
        var hrRole = roleRepository.findByName(RoleType.ROLE_HR).orElseThrow();
        var hr = Employee.builder()
                .firstName("Jane")
                .lastName("HR")
                .email("hr@company.com")
                .password(passwordEncoder.encode("Hr@123"))
                .department("Human Resources")
                .position("HR Manager")
                .salary(70000.0)
                .active(true)
                .roles(Set.of(hrRole))
                .build();

        // Create regular employee
        var employeeRole = roleRepository.findByName(RoleType.ROLE_EMPLOYEE).orElseThrow();
        var employee = Employee.builder()
                .firstName("Bob")
                .lastName("Employee")
                .email("employee@company.com")
                .password(passwordEncoder.encode("Employee@123"))
                .department("Engineering")
                .position("Software Engineer")
                .salary(60000.0)
                .active(true)
                .roles(Set.of(employeeRole))
                .build();

        employeeRepository.saveAll(Set.of(admin, manager, hr, employee));
        log.info("Default users initialized: 4 users created");
        log.info("Default credentials - admin@company.com / Admin@123");
    }

    private Permission createPermission(String name, String resource, String action, String description) {
        return Permission.builder()
                .name(name)
                .resource(resource)
                .action(action)
                .description(description)
                .build();
    }
}
