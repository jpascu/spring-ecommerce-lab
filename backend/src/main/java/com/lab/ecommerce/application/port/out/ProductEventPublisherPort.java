package com.lab.ecommerce.application.port.out;

import com.lab.ecommerce.domain.event.ProductChangedEvent;

/**
 * Puerto de salida (driven) para publicar eventos de cambio de producto.
 *
 * <p>La aplicacion no sabe si detras hay un data lake, un topic de Kafka, una
 * cola SQS o un simple log: solo conoce esta abstraccion. Cambiar el destino es
 * cambiar de adaptador, sin tocar la logica de negocio.</p>
 */
public interface ProductEventPublisherPort {

  void publish(ProductChangedEvent event);
}
