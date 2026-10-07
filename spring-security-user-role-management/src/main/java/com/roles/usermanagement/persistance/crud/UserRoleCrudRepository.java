package com.roles.usermanagement.persistance.crud;

import com.roles.usermanagement.persistance.entity.UserRoleEntity;
import com.roles.usermanagement.persistance.entity.UserRoleId;
import org.springframework.data.repository.CrudRepository;

public interface UserRoleCrudRepository extends CrudRepository<UserRoleEntity, UserRoleId> {

}
