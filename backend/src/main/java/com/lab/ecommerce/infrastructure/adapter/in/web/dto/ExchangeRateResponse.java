package com.lab.ecommerce.infrastructure.adapter.in.web.dto;

import java.math.BigDecimal;

/**
 * Respuesta con el tipo de cambio EUR -&gt; divisa.
 */
public record ExchangeRateResponse(String currency, BigDecimal rate) {
}
