package com.roles.usermanagement.modules.customer;
public record CustomerResponse(Long id,
                               String name,
                               String email,
                               String phone,
                               boolean active) {}
