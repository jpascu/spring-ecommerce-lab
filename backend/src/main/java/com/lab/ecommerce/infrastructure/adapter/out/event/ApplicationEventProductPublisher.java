package com.lab.ecommerce.infrastructure.adapter.out.event;

import com.lab.ecommerce.application.port.out.ProductEventPublisherPort;
import com.lab.ecommerce.domain.event.ProductChangedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Adaptador de salida que publica el evento de dominio en el <strong>bus de eventos
 * de Spring</strong> ({@link ApplicationEventPublisher}).
 *
 * <p>Es deliberadamente ligero y síncrono: solo "anuncia" el cambio dentro de la
 * transacción en curso. El trabajo real (volcado al data lake) lo realizan los
 * <em>listeners</em>, que pueden ejecutarse de forma asíncrona y tras el commit. Así
 * desacoplamos al productor (la aplicación) de los consumidores.</p>
 */
@Component
@RequiredArgsConstructor
public class ApplicationEventProductPublisher implements ProductEventPublisherPort {

  private final ApplicationEventPublisher publisher;

  @Override
  public void publish(ProductChangedEvent event) {
    publisher.publishEvent(event);
  }
}
