package com.lab.ecommerce.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.Min;

/**
 * Petición de presupuesto. {@code tier} y {@code couponCode} son opcionales.
 *
 * @param tier       segmento de cliente: STANDARD | PREMIUM | VIP (por defecto STANDARD)
 * @param quantity   unidades (&gt;= 1)
 * @param couponCode cupón opcional (p.ej. SAVE10, SAVE20, HALF)
 */
public record QuoteRequest(
    String tier,
    @Min(value = 1, message = "quantity debe ser >= 1") int quantity,
    String couponCode) {
}
