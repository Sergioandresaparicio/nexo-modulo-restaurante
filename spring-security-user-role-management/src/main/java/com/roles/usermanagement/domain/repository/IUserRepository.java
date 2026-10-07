package com.roles.usermanagement.domain.repository;

import com.roles.usermanagement.domain.dto.UserDto;
import com.roles.usermanagement.domain.dto.UserRoleDto;
import com.roles.usermanagement.persistance.entity.UserEntity;
import java.util.List;

/**
 * Interfaz que define las operaciones para acceder a usuarios en el dominio.
 */
public interface IUserRepository {

  /**
   * Carga un usuario por su nombre de usuario.
   *
   * @param username El nombre de usuario del usuario que se busca.
   * @return La entidad UserEntity correspondiente al nombre de usuario proporcionado.
   */
  UserDto loadUserByUsername(String username);

  /**
   * Crear o actualizar un usuario en la base de datos.
   *
   * @param userDto La entidad UserEntity que se desea crear o actualizar.
   *                   Si el nombre de usuario ya existe, se actualiza.
   *                   Si el nombre de usuario no existe, se crea.
   */
  UserDto save(UserDto userDto);

  /**
   * Obtiene todos los usuarios de la base de datos.
   * @return Lista de usuarios.
   */
  List<UserDto> getAllUsers();

  /**
   * Elimina un usuario de la base de datos.
   * @param username El nombre de usuario del usuario que se desea eliminar.
   */
  void deleteUser(String username);

  /**
   * Si existe un usuario con el nombre de usuario proporcionado.
   * @param username El nombre de usuario del usuario que se desea buscar.
   */
  boolean existsByUsername(String username);
}