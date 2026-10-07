package com.roles.usermanagement.persistance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "app_permission")
public class PermissionEntity {
    @Id
    @Column(nullable = false, length = 50)
    private String name;
}