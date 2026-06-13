package com.lab.ecommerce.domain.pricing;

/**
 * Datos de entrada para calcular un precio: a quién y en qué condiciones.
 *
 * <p>Objeto de dominio puro. Las estrategias de descuento deciden en función de
 * este contexto (tier del cliente, cantidad, cupón).</p>
 *
 * @param tier       segmento del cliente (nunca null; por defecto STANDARD)
 * @param quantity   unidades solicitadas (&gt;= 1)
 * @param couponCode código de cupón opcional (puede ser null/vacío)
 */
public record PricingContext(CustomerTier tier, int quantity, String couponCode) {

  public PricingContext {
    if (tier == null) {
      tier = CustomerTier.STANDARD;
    }
    if (quantity < 1) {
      throw new IllegalArgumentException("quantity debe ser >= 1");
    }
  }

  public boolean hasCoupon() {
    return couponCode != null && !couponCode.isBlank();
  }
}
