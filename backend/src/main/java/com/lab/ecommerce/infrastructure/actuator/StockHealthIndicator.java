package com.lab.ecommerce.infrastructure.actuator;

import com.lab.ecommerce.application.port.in.ProductService;
import com.lab.ecommerce.domain.model.Product;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Indicador de salud propio que aparece en {@code /actuator/health} bajo la clave
 * "stock".
 *
 * <p>Demuestra cómo enriquecer el health-check con una comprobación de negocio: si
 * el catálogo no se puede consultar, reporta DOWN (la app no está sana); si funciona,
 * reporta UP con detalle de cuántos productos hay sin stock.</p>
 */
@Component("stock")
@RequiredArgsConstructor
public class StockHealthIndicator implements HealthIndicator {

  private final ProductService productService;

  @Override
  public Health health() {
    try {
      List<Product> products = productService.findAll();
      long outOfStock = products.stream()
          .filter(p -> p.getStock() == null || p.getStock() <= 0)
          .count();
      return Health.up()
          .withDetail("totalProducts", products.size())
          .withDetail("outOfStock", outOfStock)
          .build();
    } catch (Exception ex) {
      return Health.down(ex).build();
    }
  }
}
