package com.lab.ecommerce.application.port.in;

import com.lab.ecommerce.domain.pricing.PriceQuote;
import com.lab.ecommerce.domain.pricing.PricingContext;

/**
 * Puerto de entrada (driving): caso de uso de cálculo de precio de un producto.
 */
public interface PricingUseCase {

  /**
   * Calcula el presupuesto para un producto dado un contexto (cliente, cantidad, cupón).
   */
  PriceQuote quote(Long productId, PricingContext context);
}
