package com.shakhawat.rbacabac.security;

import com.shakhawat.rbacabac.entity.Employee;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class UserPrincipal implements UserDetails {
    private Long id;
    private String email;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    public static UserPrincipal create(Employee employee) {
        var authorities = employee.getRoles().stream()
                .flatMap(role -> {
                    var roleAuth = new SimpleGrantedAuthority(role.getName().name());
                    var permissionAuths = role.getPermissions().stream()
                            .map(permission -> new SimpleGrantedAuthority(permission.getName()))
                            .collect(Collectors.toSet());
                    permissionAuths.add(roleAuth);
                    return permissionAuths.stream();
                })
                .collect(Collectors.toSet());

        return new UserPrincipal(
                employee.getId(),
                employee.getEmail(),
                employee.getPassword(),
                authorities
        );
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
