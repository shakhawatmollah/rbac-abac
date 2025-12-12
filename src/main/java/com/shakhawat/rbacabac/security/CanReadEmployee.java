package com.shakhawat.rbacabac.security;

import org.springframework.security.access.prepost.PreAuthorize;
import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAuthority('READ_EMPLOYEE')")
public @interface CanReadEmployee {}
