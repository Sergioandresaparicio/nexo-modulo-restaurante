package com.roles.usermanagement.modules.product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "business_product")
@Getter
@Setter
public class Product {

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 @Column(nullable = false, length = 150)
 private String name;

 @Column(nullable = false, unique = true, length = 50)
 private String sku;

 @Column(nullable = false, precision = 19, scale = 2)
 private BigDecimal price;

 @Column(nullable = false)
 private Integer stock;

 @Column(nullable = false)
 private boolean active = true;
}