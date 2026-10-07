package com.roles.usermanagement.modules.restaurant.dto;

import jakarta.validation.constraints.Min;

import jakarta.validation.constraints.NotNull;



public record MesaRequest(

    @NotNull(message = "El número de mesa es obligatorio")
    @Min(value = 1, message = "El número de mesa debe ser positivo")
    Integer numeroMesa,
    @NotNull(message = "La capacidad es obligatoria")
    @Min(value = 1, message = "La capacidad mínima es de 1 persona")
    Integer capacidad,
    Long meseroId

) {}
