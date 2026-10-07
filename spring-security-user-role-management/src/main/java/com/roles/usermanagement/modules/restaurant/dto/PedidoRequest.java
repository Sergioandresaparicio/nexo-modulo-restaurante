package com.roles.usermanagement.modules.restaurant.dto;

import jakarta.validation.constraints.DecimalMin;

import jakarta.validation.constraints.Digits;

import jakarta.validation.constraints.NotNull;

import jakarta.validation.constraints.Size;

import java.math.BigDecimal;



public record PedidoRequest(

    @NotNull(message = "El ID de la mesa es obligatorio")
    Long mesaId,

    @NotNull(message = "El ID del mesero es obligatorio")
    Long meseroId,

    @NotNull(message = "El total es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto total debe ser mayor a 0")
    @Digits(integer = 10, fraction = 2)
    BigDecimal total,

    @Size(max = 255)
    String observaciones

) {}
