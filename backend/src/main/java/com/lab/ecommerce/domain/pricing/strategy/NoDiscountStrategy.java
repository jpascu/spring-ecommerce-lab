package com.lab.ecommerce.domain.pricing.strategy;

import com.lab.ecommerce.domain.pricing.PricingContext;
import java.math.BigDecimal;

/**
 * Estrategia por defecto (fallback): no aplica descuento. Siempre "soporta" el
 * contexto, con la prioridad más baja, de modo que se usa cuando ninguna otra aplica.
 */
public class NoDiscountStrategy extends AbstractDiscountStrategy {

  @Override
  public boolean supports(PricingContext context) {
    return true;
  }

  @Override
  protected BigDecimal doCalculate(BigDecimal subtotal, PricingContext context) {
    return BigDecimal.ZERO;
  }

  @Override
  public String code() {
    return "NONE";
  }

  @Override
  public int priority() {
    return Integer.MIN_VALUE;
  }
}
