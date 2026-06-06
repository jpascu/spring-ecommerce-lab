package com.lab.ecommerce.infrastructure.adapter.in.web.dto;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * DTO de salida del adaptador web.
 */
public record ProductResponse(
    Long id,
    String name,
    String description,
    BigDecimal price,
    Integer stock,
    String category,
    Instant createdAt,
    Instant updatedAt) {
}
