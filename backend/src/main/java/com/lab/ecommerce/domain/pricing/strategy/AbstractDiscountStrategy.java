package com.lab.ecommerce.domain.pricing.strategy;

import com.lab.ecommerce.domain.pricing.DiscountStrategy;
import com.lab.ecommerce.domain.pricing.PricingContext;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Patrón <strong>Template Method</strong>: define el esqueleto del cálculo de
 * descuento y deja que las subclases rellenen solo el paso variable.
 *
 * <p>El método {@link #calculateDiscount} es {@code final}: fija los pasos comunes
 * (saneado, tope máximo, redondeo) para TODAS las estrategias y evita que cada una
 * los reimplemente (y olvide alguno). Las subclases solo implementan
 * {@link #doCalculate}.</p>
 */
public abstract class AbstractDiscountStrategy implements DiscountStrategy {

  /** Ningún descuento puede superar el 70% del subtotal (regla transversal). */
  private static final BigDecimal MAX_RATIO = new BigDecimal("0.70");

  @Override
  public final BigDecimal calculateDiscount(BigDecimal subtotal, PricingContext context) {
    if (subtotal == null || subtotal.signum() <= 0) {
      return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }
    BigDecimal raw = doCalculate(subtotal, context);          // paso variable (subclase)
    BigDecimal cap = subtotal.multiply(MAX_RATIO);            // tope común
    BigDecimal capped = raw.min(cap).max(BigDecimal.ZERO);    // nunca negativo ni > tope
    return capped.setScale(2, RoundingMode.HALF_UP);
  }

  /**
   * Paso variable que implementa cada estrategia: cuánto descuento bruto aplicar.
   */
  protected abstract BigDecimal doCalculate(BigDecimal subtotal, PricingContext context);

  /** Utilidad para subclases: porcentaje sobre el subtotal. */
  protected BigDecimal percentage(BigDecimal subtotal, String percent) {
    return subtotal.multiply(new BigDecimal(percent));
  }
}
