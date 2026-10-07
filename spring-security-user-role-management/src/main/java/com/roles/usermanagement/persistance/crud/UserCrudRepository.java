package com.roles.usermanagement.persistance.crud;

import com.roles.usermanagement.persistance.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;

/**
 * Interfaz que define operaciones CRUD (Crear, Leer, Actualizar, Borrar) para la entidad UserEntity.
 * Extiende CrudRepository proporcionado por Spring Data JPA.
 *
 * Permite realizar operaciones básicas de persistencia para objetos UserEntity en la base de datos.
 */
public interface UserCrudRepository extends CrudRepository<UserEntity, String> {
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select u from UserEntity u where u.username = :username")
    java.util.Optional<UserEntity> findForUpdate(@org.springframework.data.repository.query.Param("username") String username);

    boolean existsByEmailAndUsernameNot(String email, String username);

}
