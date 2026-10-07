package com.roles.usermanagement.modules.restaurant.dto;

import com.roles.usermanagement.modules.restaurant.enums.EstadoPedido;

import jakarta.validation.constraints.NotNull;

public record CambioEstadoPedidoRequest(

    @NotNull(message = "El nuevo estado es obligatorio")
    EstadoPedido nuevoEstado

) {}
