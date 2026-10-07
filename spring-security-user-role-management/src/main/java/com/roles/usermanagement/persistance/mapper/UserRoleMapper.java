package com.roles.usermanagement.persistance.mapper;

import com.roles.usermanagement.domain.dto.UserRoleDto;
import com.roles.usermanagement.persistance.entity.UserRoleEntity;
import java.util.List;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface UserRoleMapper {
    @Mappings({

    })
    UserRoleDto toUserRoleDto(UserRoleEntity userRoleEntity);

    List<UserRoleDto> toUserRolesDto(List<UserRoleEntity> userRoleEntities);

    @InheritInverseConfiguration
    //@Mapping(target = "grantedDate", ignore = true)
    @Mapping(target = "user", ignore = true)
    UserRoleEntity toUserRoleEntity(UserRoleDto userRoleDto);
}
