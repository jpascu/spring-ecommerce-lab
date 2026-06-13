package com.lab.ecommerce.domain.pricing;

import java.math.BigDecimal;

/**
 * Patrón <strong>Strategy</strong>: encapsula un algoritmo de descuento intercambiable.
 *
 * <p>Cada regla de negocio (cupón, volumen, fidelidad...) es una implementación. La
 * aplicación trabaja contra esta interfaz, no contra una rama {@code if/else} gigante,
 * de modo que añadir un nuevo descuento es añadir una clase, sin tocar las demás
 * (principio abierto/cerrado).</p>
 */
public interface DiscountStrategy {

  /**
   * Indica si esta estrategia aplica al contexto dado.
   */
  boolean supports(PricingContext context);

  /**
   * Calcula el importe de descuento (no el total) para el subtotal indicado.
   */
  BigDecimal calculateDiscount(BigDecimal subtotal, PricingContext context);

  /**
   * Código identificativo de la estrategia (aparece en el presupuesto).
   */
  String code();

  /**
   * Prioridad de selección: a mayor valor, más prioritaria cuando varias aplican.
   */
  default int priority() {
    return 0;
  }
}
