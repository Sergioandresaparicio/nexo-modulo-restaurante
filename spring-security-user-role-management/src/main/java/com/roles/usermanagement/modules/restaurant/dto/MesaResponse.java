package com.roles.usermanagement.modules.restaurant.dto;

import com.roles.usermanagement.modules.restaurant.enums.EstadoMesa;



public record MesaResponse(
    Long id,
    Integer numeroMesa,
    Integer capacidad,
    EstadoMesa estado,
    boolean active,
    Long meseroId,
    String meseroNombre

) {}