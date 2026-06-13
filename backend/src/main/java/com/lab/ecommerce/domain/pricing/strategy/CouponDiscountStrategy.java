package com.lab.ecommerce.domain.pricing.strategy;

import com.lab.ecommerce.domain.pricing.PricingContext;
import java.math.BigDecimal;
import java.util.Map;

/**
 * Descuento por cupón. Es la de mayor prioridad: si el cliente trae un cupón válido,
 * gana frente a fidelidad o volumen.
 */
public class CouponDiscountStrategy extends AbstractDiscountStrategy {

  private static final Map<String, String> COUPONS = Map.of(
      "SAVE10", "0.10",
      "SAVE20", "0.20",
      "HALF", "0.50");

  @Override
  public boolean supports(PricingContext context) {
    return context.hasCoupon() && COUPONS.containsKey(normalize(context.couponCode()));
  }

  @Override
  protected BigDecimal doCalculate(BigDecimal subtotal, PricingContext context) {
    String percent = COUPONS.get(normalize(context.couponCode()));
    return percentage(subtotal, percent);
  }

  @Override
  public String code() {
    return "COUPON";
  }

  @Override
  public int priority() {
    return 30;
  }

  private String normalize(String coupon) {
    return coupon == null ? "" : coupon.trim().toUpperCase();
  }
}
