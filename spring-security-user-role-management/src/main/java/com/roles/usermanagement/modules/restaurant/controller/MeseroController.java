package com.roles.usermanagement.modules.restaurant.controller;

import com.roles.usermanagement.modules.restaurant.dto.CambioEstadoMeseroRequest;
import com.roles.usermanagement.modules.restaurant.dto.MeseroRequest;
import com.roles.usermanagement.modules.restaurant.dto.MeseroResponse;
import com.roles.usermanagement.modules.restaurant.enums.EstadoMesero;
import com.roles.usermanagement.modules.restaurant.service.MeseroService;
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
@RequestMapping("/api/meseros")
@Tag(name = "Meseros", description = "Gestión de personal de atención de restaurante y estados de actividad")
@SecurityRequirement(name = "bearerAuth")

public class MeseroController {

    private final MeseroService service;

    public MeseroController(MeseroService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('MESERO_READ')")
    @Operation(summary = "Listar meseros paginados (con filtro opcional por estado)")
    public Page<MeseroResponse> all(
            @RequestParam(required = false) EstadoMesero estado,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        if (estado != null) {
            return service.byEstado(estado, page, size);
        }
        return service.all(page, size);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('MESERO_READ')")
    @Operation(summary = "Consultar mesero por ID")
    public MeseroResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('MESERO_CREATE')")
    @Operation(summary = "Registrar nuevo mesero (inicia en estado ACTIVO)")
    public ResponseEntity<MeseroResponse> create(@Valid @RequestBody MeseroRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('MESERO_UPDATE')")
    @Operation(summary = "Actualizar datos de mesero")
    public MeseroResponse update(@PathVariable Long id, @Valid @RequestBody MeseroRequest dto) {
        return service.update(id, dto);
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAuthority('MESERO_CHANGE_STATUS')")
    @Operation(summary = "Cambiar estado del mesero (ACTIVO, NO_ACTIVO)")
    public MeseroResponse changeStatus(@PathVariable Long id, @Valid @RequestBody CambioEstadoMeseroRequest request) {
        return service.changeStatus(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('MESERO_DELETE')")
    @Operation(summary = "Desactivar mesero (borrado lógico)")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        service.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
