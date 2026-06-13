package com.lab.ecommerce.domain.pricing;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

/**
 * Resultado del cálculo de precio (presupuesto).
 *
 * <p>Patrón <strong>Builder</strong>: con tantos campos relacionados, un constructor
 * posicional sería ilegible y propenso a errores. Lombok {@code @Builder} genera un
 * builder fluido ({@code PriceQuote.builder().unitPrice(..).build()}), que es la
 * implementación idiomática del patrón en Java.</p>
 */
@Getter
@Builder
public class PriceQuote {

  private final Long productId;
  private final BigDecimal unitPrice;
  private final int quantity;
  private final BigDecimal subtotal;
  private final BigDecimal discount;
  private final BigDecimal total;
  private final String appliedStrategy;
}
