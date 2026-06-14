package com.lab.ecommerce.infrastructure.adapter.out.event;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import com.lab.ecommerce.application.port.in.ProductService;
import com.lab.ecommerce.domain.model.Product;
import com.lab.ecommerce.infrastructure.adapter.out.lake.LakeProductEventPublisher;
import java.math.BigDecimal;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;

/**
 * Verifica el flujo de eventos asíncrono: al crear un producto, el listener del data
 * lake se ejecuta tras el commit y en un hilo distinto (no bloquea la operación).
 *
 * <p>El test NO es {@code @Transactional}: deja que la transacción de
 * {@code create()} confirme, para que dispare el {@code @TransactionalEventListener}
 * (AFTER_COMMIT). Awaitility espera la ejecución asíncrona con un timeout.</p>
 */
@SpringBootTest
@ActiveProfiles("test")
class ProductEventAsyncIT {

  @Autowired
  private ProductService productService;

  @SpyBean
  private LakeProductEventPublisher lakeListener;

  @Test
  void al_crear_producto_el_listener_del_lake_se_ejecuta_tras_commit() {
    Product nuevo = Product.builder()
        .name("Teclado mecánico")
        .description("switches marrones")
        .price(new BigDecimal("89.90"))
        .stock(15)
        .category("Periféricos")
        .build();

    productService.create(nuevo);

    // El listener corre de forma asíncrona tras el commit: esperamos a que ocurra.
    await().atMost(Duration.ofSeconds(3))
        .untilAsserted(() -> verify(lakeListener).onProductChanged(any()));
  }
}
