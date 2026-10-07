package com.roles.usermanagement.modules.restaurant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;



public record MeseroRequest(

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 120)
    String nombre,
    @NotBlank(message = "El documento es obligatorio")
    @Size(max = 30)
    String documentoIdentidad,
    @Size(max = 30)
    String telefono

) {}