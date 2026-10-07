package com.roles.usermanagement.persistance.crud;

import com.roles.usermanagement.persistance.entity.PermissionEntity;
import org.springframework.data.repository.CrudRepository;

public interface PermissionCrudRepository extends CrudRepository<PermissionEntity, String> {
}