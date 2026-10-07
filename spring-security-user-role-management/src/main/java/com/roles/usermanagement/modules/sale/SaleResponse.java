package com.roles.usermanagement.modules.sale;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record SaleResponse(
        Long id,
        Long customerId,
        String customerName,
        LocalDateTime createdAt,
        String createdBy,
        BigDecimal total,
        boolean cancelled,
        LocalDateTime cancelledAt,
        String cancelledBy,
        List<Item> items
) {
 public record Item(
         Long productId,
         String productName,
         Integer quantity,
         BigDecimal unitPrice,
         BigDecimal subtotal
 ) {}
}