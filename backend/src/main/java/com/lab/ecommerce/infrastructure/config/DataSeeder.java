package com.lab.ecommerce.infrastructure.config;

import com.lab.ecommerce.application.port.in.ProductService;
import com.lab.ecommerce.domain.model.Product;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Carga datos de ejemplo al arrancar (solo perfil "dev"), a traves del puerto de
 * entrada {@link ProductService} para respetar la arquitectura hexagonal.
 */
@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

  private final ProductService productService;

  @Override
  public void run(String... args) {
    if (!productService.findAll().isEmpty()) {
      return;
    }
    List.of(
        Product.builder().name("Teclado mecanico").description("Switches rojos, RGB")
            .price(new BigDecimal("79.90")).stock(40).category("Perifericos").build(),
        Product.builder().name("Raton inalambrico").description("6 botones, 16000 DPI")
            .price(new BigDecimal("49.50")).stock(75).category("Perifericos").build(),
        Product.builder().name("Monitor 27 4K").description("IPS, 144Hz")
            .price(new BigDecimal("389.00")).stock(15).category("Monitores").build()
    ).forEach(productService::create);
    log.info("DataSeeder: cargados {} productos de ejemplo", productService.findAll().size());
  }
}
