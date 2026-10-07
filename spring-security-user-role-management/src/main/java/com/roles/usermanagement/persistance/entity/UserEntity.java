package com.roles.usermanagement.persistance.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import java.util.Set;
import java.util.LinkedHashSet;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

/**
 * Representa la entidad de usuario en la base de datos.
 */
@Getter
@Setter
@Entity
@Table(name = "user")
public class UserEntity {

  @Id
  @Column(nullable = false, length = 50, unique = true)
  private String username;

  @Column(nullable = false, length = 200, unique = true)
  private String email;

  @Column(nullable = false)
  private Boolean locked;

  @Column(nullable = false)
  private Boolean disabled;

  @Column(nullable = false, length = 200)
  private String password;

  @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  private List<UserRoleEntity> roles;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "user_permission",
      joinColumns = @JoinColumn(name = "username"),
      inverseJoinColumns = @JoinColumn(name = "permission_name"),
      uniqueConstraints = @UniqueConstraint(columnNames = {"username", "permission_name"}))
  private Set<PermissionEntity> additionalPermissions = new LinkedHashSet<>();
}