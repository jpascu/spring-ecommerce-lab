package com.lab.ecommerce.infrastructure.adapter.out.lake;

import com.lab.ecommerce.application.port.out.ProductEventPublisherPort;
import com.lab.ecommerce.domain.event.ProductChangedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Adaptador de salida que "envia" los eventos de producto a un data lake.
 *
 * <p>Aqui se simula con un log para no depender de infraestructura externa. En un
 * caso real, este adaptador usaria el SDK correspondiente (p.ej. Kinesis Firehose
 * o un PutObject a S3 en formato JSON/Parquet). Lo importante hexagonalmente es
 * que TODO ese detalle vive aqui, fuera del dominio y de la aplicacion.</p>
 */
@Slf4j
@Component
public class LakeProductEventPublisher implements ProductEventPublisherPort {

  @Override
  public void publish(ProductChangedEvent event) {
    // En produccion: serializar a JSON/Parquet y volcar al lake (S3/Kinesis...)
    log.info("[LAKE] {} producto id={} name='{}' price={} stock={} @ {}",
        event.type(),
        event.product().getId(),
        event.product().getName(),
        event.product().getPrice(),
        event.product().getStock(),
        event.occurredAt());
  }
}
