package com.lab.ecommerce.infrastructure.adapter.in.web.dto;

import java.math.BigDecimal;

/**
 * Respuesta con el presupuesto calculado.
 */
public record PriceQuoteResponse(
    Long productId,
    BigDecimal unitPrice,
    int quantity,
    BigDecimal subtotal,
    BigDecimal discount,
    BigDecimal total,
    String appliedStrategy) {
}
