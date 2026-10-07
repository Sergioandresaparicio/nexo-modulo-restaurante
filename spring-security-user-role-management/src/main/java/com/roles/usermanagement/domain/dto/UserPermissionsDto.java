package com.roles.usermanagement.domain.dto;
import java.util.List;
public record UserPermissionsDto(String username, String role, List<String> rolePermissions,
        List<String> additionalPermissions, List<String> effectivePermissions) { }
