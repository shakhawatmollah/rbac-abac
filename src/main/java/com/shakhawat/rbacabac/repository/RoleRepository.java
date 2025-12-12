package com.shakhawat.rbacabac.repository;

import com.shakhawat.rbacabac.entity.Role;
import com.shakhawat.rbacabac.entity.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleType name);
    boolean existsByName(RoleType name);
}
