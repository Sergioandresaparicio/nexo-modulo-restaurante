package com.roles.usermanagement.persistance.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Clase que representa la clave primaria compuesta de UserRoleEntity.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleId implements Serializable {
  private String username;
  private String role;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof UserRoleId that)) return false;
    return Objects.equals(username, that.username) && Objects.equals(role, that.role);
  }

  @Override
  public int hashCode() {
    return Objects.hash(username, role);
  }
}
