package com.lab.ecommerce.infrastructure.adapter.out.lake;

import com.lab.ecommerce.domain.event.ProductChangedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Adaptador de salida que "envia" los eventos de producto a un data lake, ahora de
 * forma <strong>asincrona y desacoplada por eventos</strong>.
 *
 * <p>Escucha el {@link ProductChangedEvent} del bus de Spring con dos garantias:</p>
 * <ul>
 *   <li>{@code @TransactionalEventListener(AFTER_COMMIT)}: solo vuelca al lago si la
 *   transaccion que origino el cambio <em>confirmo</em> (si hay rollback, no se
 *   publica nada incoherente).</li>
 *   <li>{@code @Async}: el volcado corre en un hilo del pool {@code lakeTaskExecutor},
 *   por lo que NO bloquea la peticion HTTP que creo/actualizo el producto.</li>
 * </ul>
 *
 * <p>Aqui se simula con un log. En un caso real usaria el SDK correspondiente (p.ej.
 * Kinesis Firehose o un PutObject a S3 en JSON/Parquet). Lo importante hexagonalmente
 * es que TODO ese detalle vive aqui, fuera del dominio y de la aplicacion.</p>
 */
@Slf4j
@Component
public class LakeProductEventPublisher {

  @Async("lakeTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onProductChanged(ProductChangedEvent event) {
    // En produccion: serializar a JSON/Parquet y volcar al lake (S3/Kinesis...)
    log.info("[LAKE] (async hilo={}) {} producto id={} name='{}' price={} stock={} @ {}",
        Thread.currentThread().getName(),
        event.type(),
        event.product().getId(),
        event.product().getName(),
        event.product().getPrice(),
        event.product().getStock(),
        event.occurredAt());
  }
}
