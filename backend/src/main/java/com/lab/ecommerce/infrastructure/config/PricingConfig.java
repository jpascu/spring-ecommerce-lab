package com.lab.ecommerce.infrastructure.config;

import com.lab.ecommerce.domain.pricing.DiscountStrategy;
import com.lab.ecommerce.domain.pricing.DiscountStrategyFactory;
import com.lab.ecommerce.domain.pricing.strategy.CouponDiscountStrategy;
import com.lab.ecommerce.domain.pricing.strategy.CustomerTierStrategy;
import com.lab.ecommerce.domain.pricing.strategy.NoDiscountStrategy;
import com.lab.ecommerce.domain.pricing.strategy.VolumeDiscountStrategy;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Wiring del motor de descuentos.
 *
 * <p>Las estrategias y la factoría son dominio PURO (sin anotaciones de Spring). Es
 * aquí, en la infraestructura (el "borde"), donde se registran como beans y se
 * inyectan en la factoría. Así el dominio no depende del framework: se respeta la
 * regla de dependencia hexagonal.</p>
 */
@Configuration
public class PricingConfig {

  @Bean
  public NoDiscountStrategy noDiscountStrategy() {
    return new NoDiscountStrategy();
  }

  @Bean
  public CustomerTierStrategy customerTierStrategy() {
    return new CustomerTierStrategy();
  }

  @Bean
  public VolumeDiscountStrategy volumeDiscountStrategy() {
    return new VolumeDiscountStrategy();
  }

  @Bean
  public CouponDiscountStrategy couponDiscountStrategy() {
    return new CouponDiscountStrategy();
  }

  /**
   * Spring inyecta automáticamente TODOS los beans de tipo {@link DiscountStrategy}.
   */
  @Bean
  public DiscountStrategyFactory discountStrategyFactory(List<DiscountStrategy> strategies) {
    return new DiscountStrategyFactory(strategies);
  }
}
