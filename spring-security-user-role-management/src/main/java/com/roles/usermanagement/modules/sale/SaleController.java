package com.roles.usermanagement.modules.sale;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.security.Principal;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sales")
@Tag(name = "Ventas")
@SecurityRequirement(name = "bearerAuth")
public class SaleController {

 private final SaleService service;

 public SaleController(SaleService service) {
  this.service = service;
 }

 @Operation(
         summary = "Listar ventas",
         description = "Incluye ventas anuladas. page desde 0; size entre 1 y 100."
 )
 @GetMapping
 @PreAuthorize("hasAuthority('SALE_READ')")
 public Page<SaleResponse> all(
         @RequestParam(defaultValue = "0") int page,
         @RequestParam(defaultValue = "20") int size
 ) {
  return service.all(page, size);
 }

 @Operation(summary = "Consultar una venta y su detalle histórico")
 @GetMapping("/{id}")
 @PreAuthorize("hasAuthority('SALE_READ')")
 public SaleResponse get(@PathVariable Long id) {
  return service.get(id);
 }

 @Operation(
         summary = "Registrar una venta",
         description = "Precio y total calculados en el servidor. Descuenta inventario; cliente y productos deben estar activos. No acepta productos repetidos."
 )
 @PostMapping
 @PreAuthorize("hasAuthority('SALE_CREATE')")
 public ResponseEntity<SaleResponse> create(
         @Valid @RequestBody SaleRequest dto,
         Principal principal
 ) {
  return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto, principal.getName()));
 }

 @Operation(
         summary = "Anular una venta",
         description = "Repone existencias una sola vez. Conserva fecha, actor y detalle original."
 )
 @PostMapping("/{id}/cancel")
 @PreAuthorize("hasAuthority('SALE_CANCEL')")
 public SaleResponse cancel(@PathVariable Long id, Principal principal) {
  return service.cancel(id, principal.getName());
 }
}