package com.roles.usermanagement.modules.restaurant.dto;

import com.roles.usermanagement.modules.restaurant.enums.EstadoMesero;
import jakarta.validation.constraints.NotNull;

public record CambioEstadoMeseroRequest(

    @NotNull(message = "El nuevo estado del mesero es obligatorio")
    EstadoMesero nuevoEstado

) {}