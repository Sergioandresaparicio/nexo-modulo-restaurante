package com.roles.usermanagement.persistance.repository;
import com.roles.usermanagement.domain.dto.UserRoleDto;
import com.roles.usermanagement.domain.repository.IUserRoleRepository;
import org.springframework.stereotype.Repository;
@Repository
public class UserRoleRepository implements IUserRoleRepository {
    private final UserRepository users;
    public UserRoleRepository(UserRepository users) {this.users=users;}
    @Override public UserRoleDto save(UserRoleDto dto) {return users.assignRole(dto);}
}
