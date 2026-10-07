package com.roles.usermanagement.modules.customer;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
@RestController @RequestMapping("/api/customers") @Tag(name="Clientes") @SecurityRequirement(name="bearerAuth")
public class CustomerController {
 private final CustomerService service;
 public CustomerController(CustomerService service){this.service=service;}

 @Operation(summary="Listar clientes",description="Paginación: page desde 0; size entre 1 y 100. Incluye registros inactivos.")
 @GetMapping
 @PreAuthorize("hasAuthority('CUSTOMER_READ')")
 public Page<CustomerResponse> all(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size){return service.all(page,size);}

 @Operation(summary="Consultar detalle de clientes")
 @GetMapping("/{id}") @PreAuthorize("hasAuthority('CUSTOMER_READ')")
 public CustomerResponse get(@PathVariable Long id){return service.get(id);}

 @Operation(summary="Crear registro de clientes")
 @PostMapping @PreAuthorize("hasAuthority('CUSTOMER_CREATE')")
 public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CustomerRequest dto){return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));}

 @Operation(summary="Actualizar registro de clientes",description="Reemplaza los campos editables; requiere registro activo.")
 @PutMapping("/{id}") @PreAuthorize("hasAuthority('CUSTOMER_UPDATE')")
 public CustomerResponse update(@PathVariable Long id,@Valid @RequestBody CustomerRequest dto){return service.update(id,dto);}

 @Operation(summary="Desactivar registro de clientes",description="Conserva el historial de ventas; no elimina físicamente el registro.")
 @DeleteMapping("/{id}") @PreAuthorize("hasAuthority('CUSTOMER_DELETE')")
 public ResponseEntity<Void> deactivate(@PathVariable Long id){service.deactivate(id);return ResponseEntity.noContent().build();}
}
