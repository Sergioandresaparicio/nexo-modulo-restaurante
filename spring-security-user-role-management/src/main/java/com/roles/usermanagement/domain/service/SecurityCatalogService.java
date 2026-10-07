package com.roles.usermanagement.domain.service;

import com.roles.usermanagement.domain.dto.*;
import com.roles.usermanagement.persistance.crud.*;
import com.roles.usermanagement.persistance.entity.*;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class SecurityCatalogService {
    private final RoleCrudRepository roles;
    private final PermissionCrudRepository permissions;
    public SecurityCatalogService(RoleCrudRepository roles, PermissionCrudRepository permissions) {
        this.roles=roles; this.permissions=permissions;
    }
    private String valid(String name) {
        if(name==null || !name.matches("[A-Za-z][A-Za-z0-9_]{0,49}"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"El nombre debe tener entre 1 y 50 caracteres: letras, números y guion bajo; debe comenzar con una letra");
        return name;
    }
    private RoleCatalogDto dto(RoleEntity role) {
        return new RoleCatalogDto(role.getName(),role.getPermissions().stream().map(PermissionEntity::getName).sorted().toList());
    }
    private PermissionEntity permission(String name) {
        return permissions.findById(valid(name)).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"El permiso no existe"));
    }
    @Transactional(readOnly=true)
    public List<RoleCatalogDto> roles() {
        List<RoleCatalogDto> result=new ArrayList<>(); roles.findAll().forEach(role -> result.add(dto(role)));
        return result.stream().sorted(Comparator.comparing(RoleCatalogDto::name)).toList();
    }
    @Transactional(readOnly=true)
    public List<String> permissions() {
        List<String> result=new ArrayList<>(); permissions.findAll().forEach(p -> result.add(p.getName()));
        return result.stream().sorted().toList();
    }
    @Transactional
    public RoleCatalogDto createRole(RoleCatalogDto request) {
        String name=valid(request.name());
        if(roles.existsById(name)) throw new ResponseStatusException(HttpStatus.CONFLICT,"El rol ya existe");
        RoleEntity role=new RoleEntity(); role.setName(name);
        if(request.permissions()!=null) for(String permission:new LinkedHashSet<>(request.permissions())) role.getPermissions().add(permission(permission));
        return dto(roles.save(role));
    }
    @Transactional
    public PermissionCatalogDto createPermission(PermissionCatalogDto request) {
        String name=valid(request.name());
        if(permissions.existsById(name)) throw new ResponseStatusException(HttpStatus.CONFLICT,"El permiso ya existe");
        PermissionEntity permission=new PermissionEntity(); permission.setName(name); permissions.save(permission);
        return new PermissionCatalogDto(name);
    }
    @Transactional
    public RoleCatalogDto link(String name,String permissionName,boolean add) {
        RoleEntity role=roles.findForUpdate(valid(name)).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"El rol no existe"));
        PermissionEntity selected=permission(permissionName);
        if(add) {
            if(role.getPermissions().stream().noneMatch(p -> p.getName().equals(permissionName))) role.getPermissions().add(selected);
        } else {
            if((name.equals("ADMIN") && Arrays.stream(UserRoles.Authority.values()).anyMatch(a -> a.value().equals(permissionName)))
                || (name.equals("CUSTOMER") && permissionName.equals("random_order")))
                throw new ResponseStatusException(HttpStatus.CONFLICT,"El permiso base de este rol está reservado por la inicialización");
            role.getPermissions().removeIf(p -> p.getName().equals(permissionName));
        }
        return dto(role);
    }
}
