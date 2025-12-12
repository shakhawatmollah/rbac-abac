package com.shakhawat.rbacabac.repository;

import com.shakhawat.rbacabac.entity.Employee;
import com.shakhawat.rbacabac.entity.RoleType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByEmail(String email);
    boolean existsByEmail(String email);

    Page<Employee> findByDepartment(String department, Pageable pageable);
    List<Employee> findByDepartment(String department);
    List<Employee> findByActive(Boolean active);

    @Query("SELECT e FROM Employee e JOIN e.roles r WHERE r.name = :roleName")
    List<Employee> findByRoleName(RoleType roleName);

    @Query("SELECT e FROM Employee e WHERE LOWER(e.firstName) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(e.lastName) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(e.email) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Employee> searchEmployees(String search, Pageable pageable);
}
