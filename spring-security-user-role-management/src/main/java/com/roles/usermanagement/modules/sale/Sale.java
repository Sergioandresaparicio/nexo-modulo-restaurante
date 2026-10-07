package com.roles.usermanagement.modules.sale;

import com.roles.usermanagement.modules.customer.Customer;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "business_sale")
@Getter
@Setter
public class Sale {

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 @ManyToOne(optional = false)
 @JoinColumn(name = "customer_id", nullable = false)
 private Customer customer;

 @Column(nullable = false, length = 150)
 private String customerName;

 @Column(nullable = false)
 private LocalDateTime createdAt;

 @Column(nullable = false, length = 50)
 private String createdBy;

 @Column(nullable = false, precision = 19, scale = 2)
 private BigDecimal total;

 @Column(nullable = false)
 private boolean cancelled;

 private LocalDateTime cancelledAt;

 @Column(length = 50)
 private String cancelledBy;

 @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL)
 @OrderBy("id")
 private List<SaleItem> items = new ArrayList<>();
}