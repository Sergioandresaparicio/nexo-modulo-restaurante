package com.roles.usermanagement.modules.product;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Productos")
@SecurityRequirement(name = "bearerAuth")
public class ProductController {

 private final ProductService service;

 public ProductController(ProductService service) {
  this.service = service;
 }

 @Operation(
         summary = "Listar productos",
         description = "Paginación: page desde 0; size entre 1 y 100. Incluye registros inactivos."
 )
 @GetMapping
 @PreAuthorize("hasAuthority('PRODUCT_READ')")
 public Page<ProductResponse> all(
         @RequestParam(defaultValue = "0") int page,
         @RequestParam(defaultValue = "20") int size
 ) {
  return service.all(page, size);
 }

 @Operation(summary = "Consultar detalle de productos")
 @GetMapping("/{id}")
 @PreAuthorize("hasAuthority('PRODUCT_READ')")
 public ProductResponse get(@PathVariable Long id) {
  return service.get(id);
 }

 @Operation(summary = "Crear registro de productos")
 @PostMapping
 @PreAuthorize("hasAuthority('PRODUCT_CREATE')")
 public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest dto) {
  return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
 }

 @Operation(
         summary = "Actualizar registro de productos",
         description = "Reemplaza los campos editables; requiere registro activo."
 )
 @PutMapping("/{id}")
 @PreAuthorize("hasAuthority('PRODUCT_UPDATE')")
 public ProductResponse update(
         @PathVariable Long id,
         @Valid @RequestBody ProductRequest dto
 ) {
  return service.update(id, dto);
 }

 @Operation(
         summary = "Desactivar registro de productos",
         description = "Conserva el historial de ventas; no elimina físicamente el registro."
 )
 @DeleteMapping("/{id}")
 @PreAuthorize("hasAuthority('PRODUCT_DELETE')")
 public ResponseEntity<Void> deactivate(@PathVariable Long id) {
  service.deactivate(id);
  return ResponseEntity.noContent().build();
 }
}