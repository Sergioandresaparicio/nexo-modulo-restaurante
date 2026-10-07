package com.roles.usermanagement.domain.service;

import com.roles.usermanagement.domain.dto.UserDto;
import com.roles.usermanagement.domain.dto.UserPermissionDto;
import com.roles.usermanagement.domain.dto.UserPermissionsDto;
import com.roles.usermanagement.domain.dto.UserRoleDto;
import com.roles.usermanagement.persistance.repository.UserRepository;
import com.roles.usermanagement.persistance.repository.UserRoleRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final UserRoleRepository userRoleRepository;

  @Autowired
  public UserService(UserRepository userRepository, UserRoleRepository userRoleRepository) {
    this.userRepository = userRepository;
    this.userRoleRepository = userRoleRepository;
  }

  public UserDto saveUser(UserDto userDto) {
    return userRepository.save(userDto);
  }

  public UserDto updateUser(UserDto userDto) {
    return userRepository.update(userDto);
  }

  public List<UserDto> getAllUsers() {
    return userRepository.getAllUsers();
  }

  public void deleteUser(String name) {
    userRepository.deleteUser(name);
  }

  public boolean exists(String name) {
    return userRepository.existsByUsername(name);
  }

  public UserDto grantPermission(UserPermissionDto dto) { return userRepository.grantPermission(dto); }
  public UserDto revokePermission(String username, String permission) {return userRepository.revokePermission(username,permission);}
  public UserPermissionsDto permissionDetails(String username) {return userRepository.permissionDetails(username);}
  public List<String> permissionCatalog() {return userRepository.permissionCatalog();}

  public UserRoleDto assignRoleToUser(UserRoleDto userRoleDto) {
    return userRoleRepository.save(userRoleDto);
  }
}
