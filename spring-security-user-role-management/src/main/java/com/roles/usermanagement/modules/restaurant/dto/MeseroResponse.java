package com.roles.usermanagement.modules.restaurant.dto;
import com.roles.usermanagement.modules.restaurant.enums.EstadoMesero;

public record MeseroResponse(

    Long id,
    String nombre,
    String documentoIdentidad,
    String telefono,
    EstadoMesero estado,
    boolean active

) {}
