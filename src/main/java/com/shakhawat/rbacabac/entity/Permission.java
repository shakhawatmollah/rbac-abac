package com.shakhawat.rbacabac.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "permissions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // READ_EMPLOYEE, CREATE_EMPLOYEE, UPDATE_EMPLOYEE, DELETE_EMPLOYEE

    private String resource; // EMPLOYEE
    private String action; // READ, CREATE, UPDATE, DELETE
    private String description;
}
