package com.roles.usermanagement.modules.customer;
import jakarta.validation.constraints.*;
public record CustomerRequest(@NotBlank @Size(max=150) String name,
                              @NotBlank @Email @Size(max=200) String email,
                              @Size(max=30) String phone) {}
