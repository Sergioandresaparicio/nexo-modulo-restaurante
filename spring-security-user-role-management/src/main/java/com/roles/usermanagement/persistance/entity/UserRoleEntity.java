package com.roles.usermanagement.persistance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * Representa la relación entre usuarios y roles en la base de datos.
 */
@Getter
@Setter
@Entity
@Table(name = "user_role", uniqueConstraints = @jakarta.persistence.UniqueConstraint(name = "uk_user_role_username", columnNames = "username"))
@IdClass(UserRoleId.class)
public class UserRoleEntity {

  @Id
  @Column(nullable = false, length = 50)
  private String username;

  @Id
  @Column(nullable = false, length = 50)
  private String role;

  @Column(nullable = false, name = "granted_date")
  private LocalDateTime grantedDate;

  @PrePersist
  protected void onCreate() {
    grantedDate = LocalDateTime.now(); // Establecer la fecha actual al crear el rol
  }

  @ManyToOne
  @JoinColumn(name = "username", referencedColumnName = "username", insertable = false, updatable = false)
  private UserEntity user;
  @ManyToOne
  @JoinColumn(name = "role", referencedColumnName = "name", insertable = false, updatable = false)
  private RoleEntity roleDefinition;
}
