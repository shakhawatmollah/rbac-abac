package com.shakhawat.rbacabac.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component("employeePermissionEvaluator")
@RequiredArgsConstructor
public class EmployeePermissionEvaluator {

    public boolean canModify(Long employeeId) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        assert auth != null;
        var userPrincipal = (UserPrincipal) auth.getPrincipal();

        // Admin can modify anyone
        if (hasRole(auth, "ROLE_ADMIN")) {
            return true;
        }

        // HR can modify non-admin employees
        if (hasRole(auth, "ROLE_HR")) {
            return true;
        }

        // Users can only modify themselves
        assert userPrincipal != null;
        return userPrincipal.getId().equals(employeeId);
    }

    public boolean canView(Long employeeId) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        assert auth != null;
        var userPrincipal = (UserPrincipal) auth.getPrincipal();

        // Manager and above can view anyone
        if (hasRole(auth, "ROLE_ADMIN") || hasRole(auth, "ROLE_MANAGER") || hasRole(auth, "ROLE_HR")) {
            return true;
        }

        // Users can view themselves
        assert userPrincipal != null;
        return userPrincipal.getId().equals(employeeId);
    }

    private boolean hasRole(org.springframework.security.core.Authentication auth, String role) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> Objects.equals(a.getAuthority(), role));
    }
}
