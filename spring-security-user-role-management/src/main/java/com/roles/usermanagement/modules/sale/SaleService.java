package com.roles.usermanagement.modules.sale;

import com.roles.usermanagement.modules.customer.Customer;
import com.roles.usermanagement.modules.customer.CustomerRepository;
import com.roles.usermanagement.modules.product.Product;
import com.roles.usermanagement.modules.product.ProductRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class SaleService {

 private final SaleRepository sales;
 private final CustomerRepository customers;
 private final ProductRepository products;

 public SaleService(
         SaleRepository sales,
         CustomerRepository customers,
         ProductRepository products
 ) {
  this.sales = sales;
  this.customers = customers;
  this.products = products;
 }

 private ResponseStatusException conflict(String text) {
  return new ResponseStatusException(HttpStatus.CONFLICT, text);
 }

 private SaleResponse dto(Sale s) {
  return new SaleResponse(
          s.getId(),
          s.getCustomer().getId(),
          s.getCustomerName(),
          s.getCreatedAt(),
          s.getCreatedBy(),
          s.getTotal(),
          s.isCancelled(),
          s.getCancelledAt(),
          s.getCancelledBy(),
          s.getItems().stream()
                  .map(i -> new SaleResponse.Item(
                          i.getProduct().getId(),
                          i.getProductName(),
                          i.getQuantity(),
                          i.getUnitPrice(),
                          i.getSubtotal()
                  ))
                  .toList()
  );
 }

 @Transactional(readOnly = true)
 public SaleResponse get(Long id) {
  return dto(sales.findById(id)
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Venta no encontrada")));
 }

 @Transactional(readOnly = true)
 public Page<SaleResponse> all(int page, int size) {
  if (page < 0 || size < 1 || size > 100) {
   throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "page >= 0; size entre 1 y 100");
  }
  return sales.findAll(PageRequest.of(page, size, Sort.by("id").descending())).map(this::dto);
 }

 public SaleResponse create(SaleRequest request, String actor) {
  Customer customer = customers.findForUpdate(request.customerId())
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado"));

  if (!customer.isActive()) {
   throw conflict("Cliente inactivo");
  }

  Map<Long, Integer> quantities = new TreeMap<>();
  for (var item : request.items()) {
   if (quantities.putIfAbsent(item.productId(), item.quantity()) != null) {
    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No repitas productos; consolida la cantidad");
   }
  }

  Sale sale = new Sale();
  sale.setCustomer(customer);
  sale.setCustomerName(customer.getName());
  sale.setCreatedAt(LocalDateTime.now());
  sale.setCreatedBy(actor);
  sale.setTotal(BigDecimal.ZERO);

  for (var entry : quantities.entrySet()) {
   Product product = products.findForUpdate(entry.getKey())
           .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));

   if (!product.isActive()) {
    throw conflict("Producto inactivo");
   }

   if (product.getStock() < entry.getValue()) {
    throw conflict("Existencias insuficientes para " + product.getSku());
   }

   SaleItem item = new SaleItem();
   item.setSale(sale);
   item.setProduct(product);
   item.setProductName(product.getName());
   item.setQuantity(entry.getValue());
   item.setUnitPrice(product.getPrice());
   item.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(entry.getValue())));

   if (item.getSubtotal().precision() - item.getSubtotal().scale() > 17) {
    throw conflict("Importe fuera de rango");
   }

   product.setStock(product.getStock() - entry.getValue());
   sale.getItems().add(item);
   sale.setTotal(sale.getTotal().add(item.getSubtotal()));
  }

  if (sale.getTotal().precision() - sale.getTotal().scale() > 17) {
   throw conflict("Total fuera de rango");
  }

  return dto(sales.saveAndFlush(sale));
 }

 public SaleResponse cancel(Long id, String actor) {
  Sale sale = sales.findForUpdate(id)
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Venta no encontrada"));

  if (sale.isCancelled()) {
   return dto(sale);
  }

  for (var item : sale.getItems().stream().sorted(Comparator.comparing(i -> i.getProduct().getId())).toList()) {
   Product product = products.findForUpdate(item.getProduct().getId()).orElseThrow();

   if ((long) product.getStock() + item.getQuantity() > Integer.MAX_VALUE) {
    throw conflict("Existencias fuera de rango");
   }

   product.setStock(product.getStock() + item.getQuantity());
  }

  sale.setCancelled(true);
  sale.setCancelledAt(LocalDateTime.now());
  sale.setCancelledBy(actor);

  return dto(sale);
 }
}