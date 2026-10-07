package com.roles.usermanagement.persistance.crud;

import com.roles.usermanagement.persistance.entity.RoleEntity;
import org.springframework.data.repository.CrudRepository;

public interface RoleCrudRepository extends CrudRepository<RoleEntity, String> {
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select r from RoleEntity r where r.name = :name")
    java.util.Optional<RoleEntity> findForUpdate(@org.springframework.data.repository.query.Param("name") String name);
}