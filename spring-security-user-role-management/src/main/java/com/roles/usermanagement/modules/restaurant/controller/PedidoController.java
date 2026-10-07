package com.roles.usermanagement.modules.restaurant.controller;

import com.roles.usermanagement.modules.restaurant.dto.CambioEstadoPedidoRequest;
import com.roles.usermanagement.modules.restaurant.dto.PedidoRequest;
import com.roles.usermanagement.modules.restaurant.dto.PedidoResponse;
import com.roles.usermanagement.modules.restaurant.service.PedidoService;
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
@RequestMapping("/api/pedidos")
@Tag(name = "Pedidos", description = "Gestión y ciclo de vida de pedidos en restaurante")
@SecurityRequirement(name = "bearerAuth")
public class PedidoController {

    private final PedidoService service;

    public PedidoController(PedidoService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PEDIDO_READ')")
    @Operation(summary = "Listar pedidos paginados")
    public Page<PedidoResponse> all(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return service.all(page, size);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PEDIDO_READ')")
    @Operation(summary = "Consultar pedido por ID")
    public PedidoResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PEDIDO_CREATE')")
    @Operation(summary = "Crear nuevo pedido (valida que el mesero esté ACTIVO y mesa disponible)")
    public ResponseEntity<PedidoResponse> create(@Valid @RequestBody PedidoRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAuthority('PEDIDO_CHANGE_STATUS')")
    @Operation(summary = "Actualizar estado del pedido (PENDIENTE, EN_COCINA, PAGADO)")
    public PedidoResponse changeStatus(@PathVariable Long id, @Valid @RequestBody CambioEstadoPedidoRequest request) {
        return service.changeStatus(id, request);
    }
}
