package com.lab.ecommerce.infrastructure.adapter.in.web.dto;

/**
 * Respuesta del login con el token de acceso.
 */
public record TokenResponse(String accessToken, String tokenType, long expiresIn) {

  public static TokenResponse bearer(String token, long expiresIn) {
    return new TokenResponse(token, "Bearer", expiresIn);
  }
}
