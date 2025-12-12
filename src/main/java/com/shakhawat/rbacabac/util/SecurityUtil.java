package com.shakhawat.rbacabac.util;

import lombok.experimental.UtilityClass;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;
import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class SecurityUtil {

    public String getCurrentUsername() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : null;
    }

    public Set<String> getCurrentUserRoles() {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth.getAuthorities() == null) {
            return Set.of();
        }

        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(role -> role.startsWith("ROLE_"))
                .collect(Collectors.toSet());
    }

    public boolean hasRole(String role) {
        var roles = getCurrentUserRoles();
        return roles.contains(role) || roles.contains("ROLE_" + role);
    }

    public boolean hasAnyRole(String... roles) {
        var userRoles = getCurrentUserRoles();

        for (var role : roles) {
            if (userRoles.contains(role) || userRoles.contains("ROLE_" + role)) {
                return true;
            }
        }

        return false;
    }
}
