package com.shakhawat.rbacabac.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("employeePermissionEvaluator")
@RequiredArgsConstructor
public class EmployeePermissionEvaluator {

    public boolean canModify(Long employeeId) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
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
        return userPrincipal.getId().equals(employeeId);
    }

    public boolean canView(Long employeeId) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var userPrincipal = (UserPrincipal) auth.getPrincipal();

        // Manager and above can view anyone
        if (hasRole(auth, "ROLE_ADMIN") || hasRole(auth, "ROLE_MANAGER") || hasRole(auth, "ROLE_HR")) {
            return true;
        }

        // Users can view themselves
        return userPrincipal.getId().equals(employeeId);
    }

    private boolean hasRole(org.springframework.security.core.Authentication auth, String role) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(role));
    }
}
