package com.roles.usermanagement.web.controller;

import com.roles.usermanagement.domain.dto.LoginDto;
import com.roles.usermanagement.web.config.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Emisión de tokens JWT")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final com.roles.usermanagement.persistance.repository.UserRepository users;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, com.roles.usermanagement.persistance.repository.UserRepository users) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.users = users;
    }

    @PostMapping("/login")
    @Operation(summary = "Obtener un token JWT", description = "Valida usuario y contraseÃ±a y devuelve el token como texto.")
    public ResponseEntity<String> login(@RequestBody LoginDto loginDto) {
        if (loginDto.getUsername() == null || loginDto.getUsername().isBlank()
                || loginDto.getPassword() == null || loginDto.getPassword().isBlank()) {
            return ResponseEntity.badRequest().body("Usuario y contraseña son obligatorios");
        }
        try {
            var authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));
            return ResponseEntity.ok(jwtUtil.create(authentication.getName()));
        } catch (AuthenticationException exception) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales invalidas o cuenta no disponible");
        }
    }
    @GetMapping("/me")
    @org.springframework.security.access.prepost.PreAuthorize("isAuthenticated()")
    @io.swagger.v3.oas.annotations.security.SecurityRequirement(name="bearerAuth")
    @Operation(summary="Consultar rol y permisos de la sesión actual")
    public com.roles.usermanagement.domain.dto.UserPermissionsDto me(java.security.Principal principal) {
        return users.permissionDetails(principal.getName());
    }
}
