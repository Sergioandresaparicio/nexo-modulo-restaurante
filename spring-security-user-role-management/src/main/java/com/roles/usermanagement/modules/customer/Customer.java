package com.roles.usermanagement.modules.customer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="business_customer")
@Getter
@Setter
public class Customer {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
 private Long id;

 @Column(nullable=false,length=150)
 private String name;

 @Column(nullable=false,length=200)
 private String email;

 @Column(length=30)
 private String phone;

 @Column(nullable=false)
 private boolean active=true;
}
