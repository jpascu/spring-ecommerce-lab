package com.lab.ecommerce.domain.event;

import com.lab.ecommerce.domain.model.Product;
import java.time.Instant;

/**
 * Evento de dominio: un producto ha sido creado o modificado.
 *
 * <p>Es un objeto inmutable y puro (sin frameworks). La aplicacion lo emite a
 * traves de un puerto de salida; el "como" se entrega (lake, Kafka, etc.) es
 * responsabilidad del adaptador.</p>
 */
public record ProductChangedEvent(
    Product product,
    ProductChangeType type,
    Instant occurredAt) {

  public static ProductChangedEvent of(Product product, ProductChangeType type) {
    return new ProductChangedEvent(product, type, Instant.now());
  }
}
