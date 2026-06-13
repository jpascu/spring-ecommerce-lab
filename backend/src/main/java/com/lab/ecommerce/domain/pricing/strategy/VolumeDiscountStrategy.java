package com.lab.ecommerce.domain.pricing.strategy;

import com.lab.ecommerce.domain.pricing.PricingContext;
import java.math.BigDecimal;

/**
 * Descuento por volumen: a partir de 10 unidades 5%, a partir de 50 unidades 12%.
 */
public class VolumeDiscountStrategy extends AbstractDiscountStrategy {

  private static final int TIER_1 = 10;
  private static final int TIER_2 = 50;

  @Override
  public boolean supports(PricingContext context) {
    return context.quantity() >= TIER_1;
  }

  @Override
  protected BigDecimal doCalculate(BigDecimal subtotal, PricingContext context) {
    String percent = context.quantity() >= TIER_2 ? "0.12" : "0.05";
    return percentage(subtotal, percent);
  }

  @Override
  public String code() {
    return "VOLUME";
  }

  @Override
  public int priority() {
    return 20;
  }
}
