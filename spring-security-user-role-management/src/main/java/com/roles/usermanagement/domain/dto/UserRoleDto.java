package com.roles.usermanagement.domain.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class UserRoleDto {
  public String username;
  public String role;
  //public LocalDateTime grantedDate;
}
