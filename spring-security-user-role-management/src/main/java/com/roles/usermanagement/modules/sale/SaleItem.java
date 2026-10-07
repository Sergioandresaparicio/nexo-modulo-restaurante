package com.roles.usermanagement.modules.sale;

import com.roles.usermanagement.modules.product.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "business_sale_item")
@Getter
@Setter
public class SaleItem {

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 @ManyToOne(optional = false)
 @JoinColumn(name = "sale_id", nullable = false)
 private Sale sale;

 @ManyToOne(optional = false)
 @JoinColumn(name = "product_id", nullable = false)
 private Product product;

 @Column(nullable = false, length = 150)
 private String productName;

 @Column(nullable = false)
 private Integer quantity;

 @Column(nullable = false, precision = 19, scale = 2)
 private BigDecimal unitPrice;

 @Column(nullable = false, precision = 19, scale = 2)
 private BigDecimal subtotal;
}