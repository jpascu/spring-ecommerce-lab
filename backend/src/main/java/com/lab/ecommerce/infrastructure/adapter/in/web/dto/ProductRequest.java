package com.lab.ecommerce.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

/**
 * DTO de entrada del adaptador web. Vive en infraestructura porque es un detalle
 * del transporte HTTP, no del dominio.
 */
public record ProductRequest(
    @NotBlank(message = "el nombre es obligatorio")
    String name,

    String description,

    @NotNull(message = "el precio es obligatorio")
    @PositiveOrZero(message = "el precio no puede ser negativo")
    BigDecimal price,

    @NotNull(message = "el stock es obligatorio")
    @PositiveOrZero(message = "el stock no puede ser negativo")
    Integer stock,

    @NotBlank(message = "la categoria es obligatoria")
    String category) {
}
