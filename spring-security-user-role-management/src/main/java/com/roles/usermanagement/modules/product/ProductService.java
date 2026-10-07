package com.roles.usermanagement.modules.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class ProductService {

 private final ProductRepository repository;

 public ProductService(ProductRepository repository) {
  this.repository = repository;
 }

 private Product existing(Long id, boolean lock) {
  return (lock ? repository.findForUpdate(id) : repository.findById(id))
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Registro no encontrado"));
 }

 private ProductResponse dto(Product e) {
  return new ProductResponse(
          e.getId(),
          e.getName(),
          e.getSku(),
          e.getPrice(),
          e.getStock(),
          e.isActive()
  );
 }

 @Transactional(readOnly = true)
 public Page<ProductResponse> all(int page, int size) {
  if (page < 0 || size < 1 || size > 100) {
   throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "page >= 0; size entre 1 y 100");
  }
  return repository.findAll(PageRequest.of(page, size, Sort.by("id"))).map(this::dto);
 }

 @Transactional(readOnly = true)
 public ProductResponse get(Long id) {
  return dto(existing(id, false));
 }

 public ProductResponse create(ProductRequest d) {
  Product e = new Product();
  e.setName(d.name().trim());
  e.setSku(d.sku().trim());
  e.setPrice(d.price());
  e.setStock(d.stock());
  return dto(repository.save(e));
 }

 public ProductResponse update(Long id, ProductRequest d) {
  Product e = existing(id, true);
  if (!e.isActive()) {
   throw new ResponseStatusException(HttpStatus.CONFLICT, "Registro inactivo");
  }
  e.setName(d.name().trim());
  e.setSku(d.sku().trim());
  e.setPrice(d.price());
  e.setStock(d.stock());
  return dto(e);
 }

 public void deactivate(Long id) {
  existing(id, true).setActive(false);
 }
}