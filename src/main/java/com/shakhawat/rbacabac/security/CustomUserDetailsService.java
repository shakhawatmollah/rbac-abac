package com.shakhawat.rbacabac.security;

import com.shakhawat.rbacabac.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
        return UserPrincipal.create(employee);
    }

    @Transactional
    public UserDetails loadUserById(Long id) {
        var employee = employeeRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + id));
        return UserPrincipal.create(employee);
    }
}
