package com.roles.usermanagement.modules.restaurant.dto;

import com.roles.usermanagement.modules.restaurant.enums.EstadoPedido;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PedidoResponse(

    Long id,
    Long mesaId,
    Integer numeroMesa,
    Long meseroId,
    String meseroNombre,
    LocalDateTime fechaCreacion,
    EstadoPedido estado,
    BigDecimal total,
    String observaciones,
    String createdBy

) {}
