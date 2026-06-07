package com.lab.ecommerce.infrastructure.adapter.in.scheduler;

import com.lab.ecommerce.application.port.in.StockMonitorUseCase;
import com.lab.ecommerce.domain.model.Product;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Adaptador de ENTRADA basado en tareas programadas (no HTTP).
 *
 * <p>Periodicamente pregunta al caso de uso {@link StockMonitorUseCase} por los
 * productos con stock bajo y registra una alerta. Es "driving" porque inicia la
 * ejecucion de la aplicacion, igual que lo haria un controller REST, pero el
 * disparador es el reloj en lugar de una peticion HTTP.</p>
 *
 * <p>Solo activo en perfil "dev" para no interferir en los tests.</p>
 */
@Slf4j
@Component
@Profile("dev")
public class LowStockScheduler {

  private final StockMonitorUseCase stockMonitor;
  private final int threshold;

  public LowStockScheduler(StockMonitorUseCase stockMonitor,
      @Value("${app.low-stock.threshold:20}") int threshold) {
    this.stockMonitor = stockMonitor;
    this.threshold = threshold;
  }

  @Scheduled(
      initialDelayString = "${app.low-stock.initial-delay-ms:15000}",
      fixedDelayString = "${app.low-stock.interval-ms:60000}")
  public void checkLowStock() {
    List<Product> lowStock = stockMonitor.findLowStock(threshold);
    if (lowStock.isEmpty()) {
      log.info("[STOCK] revision OK: ningun producto por debajo de {} unidades", threshold);
      return;
    }
    lowStock.forEach(p -> log.warn("[STOCK] BAJO: id={} name='{}' stock={} (umbral {})",
        p.getId(), p.getName(), p.getStock(), threshold));
  }
}
