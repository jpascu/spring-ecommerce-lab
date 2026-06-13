package com.lab.ecommerce.domain.pricing.strategy;

import com.lab.ecommerce.domain.pricing.CustomerTier;
import com.lab.ecommerce.domain.pricing.PricingContext;
import java.math.BigDecimal;

/**
 * Descuento por fidelidad según el segmento del cliente: PREMIUM 8%, VIP 15%.
 */
public class CustomerTierStrategy extends AbstractDiscountStrategy {

  @Override
  public boolean supports(PricingContext context) {
    return context.tier() != CustomerTier.STANDARD;
  }

  @Override
  protected BigDecimal doCalculate(BigDecimal subtotal, PricingContext context) {
    String percent = context.tier() == CustomerTier.VIP ? "0.15" : "0.08";
    return percentage(subtotal, percent);
  }

  @Override
  public String code() {
    return "CUSTOMER_TIER";
  }

  @Override
  public int priority() {
    return 10;
  }
}
