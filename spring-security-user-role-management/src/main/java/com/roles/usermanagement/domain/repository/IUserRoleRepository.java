package com.roles.usermanagement.domain.repository;

import com.roles.usermanagement.domain.dto.UserRoleDto;

public interface IUserRoleRepository {

  UserRoleDto save(UserRoleDto userRoleDto);

}
