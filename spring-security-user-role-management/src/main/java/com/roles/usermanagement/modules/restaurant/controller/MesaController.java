package com.roles.usermanagement.modules.restaurant.controller;

import com.roles.usermanagement.modules.restaurant.dto.MesaRequest;
import com.roles.usermanagement.modules.restaurant.dto.MesaResponse;
import com.roles.usermanagement.modules.restaurant.enums.EstadoMesa;
import com.roles.usermanagement.modules.restaurant.service.MesaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mesas")
@Tag(name = "Mesas", description = "Gestión de mesas y asignación de meseros")
@SecurityRequirement(name = "bearerAuth")
public class MesaController {

    private final MesaService service;

    public MesaController(MesaService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('MESA_READ')")
    @Operation(summary = "Listar mesas paginadas")
    public Page<MesaResponse> all(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return service.all(page, size);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('MESA_READ')")
    @Operation(summary = "Consultar mesa por ID")
    public MesaResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('MESA_CREATE')")
    @Operation(summary = "Crear nueva mesa (valida que el mesero esté ACTIVO)")
    public ResponseEntity<MesaResponse> create(@Valid @RequestBody MesaRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('MESA_UPDATE')")
    @Operation(summary = "Actualizar mesa o reasignar mesero")
    public MesaResponse update(@PathVariable Long id, @Valid @RequestBody MesaRequest dto) {
        return service.update(id, dto);
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAuthority('MESA_CHANGE_STATUS')")
    @Operation(summary = "Cambiar estado de la mesa (LIBRE, OCUPADA, SUCIA)")
    public MesaResponse changeStatus(@PathVariable Long id, @RequestParam EstadoMesa estado) {
        return service.changeStatus(id, estado);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('MESA_DELETE')")
    @Operation(summary = "Desactivar mesa")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        service.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}