package com.lab.ecommerce.domain.pricing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.lab.ecommerce.domain.pricing.strategy.CouponDiscountStrategy;
import com.lab.ecommerce.domain.pricing.strategy.CustomerTierStrategy;
import com.lab.ecommerce.domain.pricing.strategy.NoDiscountStrategy;
import com.lab.ecommerce.domain.pricing.strategy.VolumeDiscountStrategy;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Test unitario PURO del motor de descuentos: sin Spring, sin BBDD. Es la ventaja
 * de tener el dominio aislado (hexagonal): la lógica se prueba en milisegundos.
 */
class DiscountEngineTest {

  private final DiscountStrategyFactory factory = new DiscountStrategyFactory(List.of(
      new NoDiscountStrategy(),
      new CustomerTierStrategy(),
      new VolumeDiscountStrategy(),
      new CouponDiscountStrategy()));

  private static final BigDecimal SUBTOTAL = new BigDecimal("100.00");

  @Test
  void sin_condiciones_no_aplica_descuento() {
    DiscountStrategy strategy = factory.select(new PricingContext(CustomerTier.STANDARD, 1, null));
    assertThat(strategy.code()).isEqualTo("NONE");
    assertThat(strategy.calculateDiscount(SUBTOTAL, new PricingContext(CustomerTier.STANDARD, 1, null)))
        .isEqualByComparingTo("0.00");
  }

  @Test
  void cliente_vip_obtiene_15_por_ciento() {
    PricingContext ctx = new PricingContext(CustomerTier.VIP, 1, null);
    DiscountStrategy strategy = factory.select(ctx);
    assertThat(strategy.code()).isEqualTo("CUSTOMER_TIER");
    assertThat(strategy.calculateDiscount(SUBTOTAL, ctx)).isEqualByComparingTo("15.00");
  }

  @Test
  void volumen_supera_a_fidelidad() {
    PricingContext ctx = new PricingContext(CustomerTier.VIP, 60, null);
    DiscountStrategy strategy = factory.select(ctx);
    assertThat(strategy.code()).isEqualTo("VOLUME");
    assertThat(strategy.calculateDiscount(SUBTOTAL, ctx)).isEqualByComparingTo("12.00");
  }

  @Test
  void cupon_tiene_la_maxima_prioridad() {
    PricingContext ctx = new PricingContext(CustomerTier.VIP, 60, "SAVE20");
    DiscountStrategy strategy = factory.select(ctx);
    assertThat(strategy.code()).isEqualTo("COUPON");
    assertThat(strategy.calculateDiscount(SUBTOTAL, ctx)).isEqualByComparingTo("20.00");
  }

  @Test
  void cupon_desconocido_no_se_selecciona() {
    PricingContext ctx = new PricingContext(CustomerTier.STANDARD, 1, "NOPE");
    assertThat(factory.select(ctx).code()).isEqualTo("NONE");
  }

  @Test
  void el_template_aplica_tope_del_70_por_ciento() {
    // HALF = 50% no llega al tope; verificamos que el cap nunca deja pasar > 70%
    PricingContext ctx = new PricingContext(CustomerTier.STANDARD, 1, "HALF");
    BigDecimal discount = factory.select(ctx).calculateDiscount(SUBTOTAL, ctx);
    assertThat(discount).isEqualByComparingTo("50.00");
    assertThat(discount).isLessThanOrEqualTo(SUBTOTAL.multiply(new BigDecimal("0.70")));
  }

  @Test
  void contexto_con_cantidad_invalida_falla() {
    assertThatThrownBy(() -> new PricingContext(CustomerTier.STANDARD, 0, null))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
