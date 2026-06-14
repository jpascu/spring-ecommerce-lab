package com.lab.ecommerce.infrastructure.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Test de <em>slice</em> de persistencia con {@code @DataJpaTest} sobre una base de
 * datos <strong>PostgreSQL real</strong> levantada con Testcontainers.
 *
 * <p>Por qué PostgreSQL y no H2: H2 imita a una BBDD, pero hay diferencias de tipos,
 * SQL y comportamiento. Testcontainers arranca un PostgreSQL efímero en Docker, así
 * el test corre contra el mismo motor que producción. {@code @ServiceConnection}
 * (Spring Boot 3.1+) conecta el DataSource al contenedor automáticamente, sin
 * configurar URL/usuario a mano.</p>
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE) // no sustituyas por H2: usa el contenedor
@Testcontainers
class ProductJpaRepositoryIT {

  @Container
  @ServiceConnection
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

  @Autowired
  private ProductJpaRepository repository;

  @Test
  void guarda_y_genera_timestamps_automaticos() {
    ProductJpaEntity entity = ProductJpaEntity.builder()
        .name("SSD 1TB").description("NVMe")
        .price(new BigDecimal("89.99")).stock(15).category("Almacenamiento")
        .build();

    ProductJpaEntity saved = repository.saveAndFlush(entity);

    assertThat(saved.getId()).isNotNull();
    assertThat(saved.getCreatedAt()).isNotNull();   // @CreationTimestamp
    assertThat(saved.getUpdatedAt()).isNotNull();   // @UpdateTimestamp
  }

  @Test
  void busca_por_id_recupera_lo_guardado() {
    ProductJpaEntity saved = repository.save(ProductJpaEntity.builder()
        .name("RAM 16GB").price(new BigDecimal("59.50")).stock(40).category("Memoria")
        .build());

    assertThat(repository.findById(saved.getId()))
        .isPresent()
        .get()
        .satisfies(found -> {
          assertThat(found.getName()).isEqualTo("RAM 16GB");
          assertThat(found.getPrice()).isEqualByComparingTo("59.50");
        });
  }
}
