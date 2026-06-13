package com.lab.ecommerce.domain.pricing;

import java.util.Comparator;
import java.util.List;

/**
 * Patrón <strong>Factory</strong>: a partir del contexto decide QUÉ estrategia usar,
 * aislando al caso de uso de esa decisión.
 *
 * <p>Recibe todas las estrategias disponibles y selecciona la de mayor prioridad
 * entre las que soportan el contexto. Añadir una estrategia nueva no obliga a tocar
 * esta clase: basta con registrarla en la lista (lo hace el wiring de Spring).</p>
 */
public class DiscountStrategyFactory {

  private final List<DiscountStrategy> strategies;

  public DiscountStrategyFactory(List<DiscountStrategy> strategies) {
    this.strategies = strategies.stream()
        .sorted(Comparator.comparingInt(DiscountStrategy::priority).reversed())
        .toList();
  }

  /**
   * Devuelve la estrategia aplicable de mayor prioridad. Siempre habrá al menos una
   * ({@code NoDiscountStrategy} es el fallback).
   */
  public DiscountStrategy select(PricingContext context) {
    return strategies.stream()
        .filter(strategy -> strategy.supports(context))
        .findFirst()
        .orElseThrow(() -> new IllegalStateException(
            "No hay ninguna estrategia de descuento aplicable (falta el fallback?)"));
  }
}
