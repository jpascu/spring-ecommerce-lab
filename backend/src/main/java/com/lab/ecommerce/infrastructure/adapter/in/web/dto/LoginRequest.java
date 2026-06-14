package com.lab.ecommerce.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Credenciales de login.
 */
public record LoginRequest(
    @NotBlank(message = "el usuario es obligatorio") String username,
    @NotBlank(message = "la contraseña es obligatoria") String password) {
}
