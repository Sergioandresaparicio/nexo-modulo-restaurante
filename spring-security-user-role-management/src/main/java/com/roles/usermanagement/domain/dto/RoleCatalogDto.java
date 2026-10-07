package com.roles.usermanagement.domain.dto;
import java.util.List;
public record RoleCatalogDto(String name, List<String> permissions) {}
