package com.lab.ecommerce.infrastructure.adapter.out.external;

import com.lab.ecommerce.application.port.out.ExchangeRateProviderPort;
import com.lab.ecommerce.domain.exception.ExternalServiceException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import java.math.BigDecimal;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Adaptador de salida que SIMULA un proveedor externo de tipos de cambio poco fiable.
 *
 * <p>Aplica resiliencia con Resilience4j:
 * <ul>
 *   <li>{@code @Retry}: reintenta los fallos transitorios.</li>
 *   <li>{@code @CircuitBreaker}: si el proveedor falla repetidamente, "abre el
 *   circuito" y deja de llamarlo, invocando directamente el {@code fallbackRate}.</li>
 * </ul>
 * La divisa especial {@code "BOOM"} falla siempre (para demostrar el fallback de forma
 * determinista en los tests).</p>
 */
@Slf4j
@Component
public class FlakyExchangeRateAdapter implements ExchangeRateProviderPort {

  private static final Map<String, BigDecimal> RATES = Map.of(
      "USD", new BigDecimal("1.08"),
      "GBP", new BigDecimal("0.85"),
      "JPY", new BigDecimal("168.50"));

  /** Tipo de cambio por defecto cuando el proveedor no está disponible. */
  private static final BigDecimal FALLBACK_RATE = BigDecimal.ONE;

  @Override
  @Retry(name = "exchangeRate")
  @CircuitBreaker(name = "exchangeRate", fallbackMethod = "fallbackRate")
  public BigDecimal getRate(String currency) {
    String code = currency == null ? "" : currency.trim().toUpperCase();
    log.info("[FX] Llamando al proveedor externo de tipos de cambio para {}", code);
    if ("BOOM".equals(code)) {
      throw new ExternalServiceException("Proveedor de tipos de cambio no disponible");
    }
    BigDecimal rate = RATES.get(code);
    if (rate == null) {
      throw new ExternalServiceException("Divisa no soportada por el proveedor: " + code);
    }
    return rate;
  }

  /**
   * Fallback: se invoca cuando se agotan los reintentos o el circuito está abierto.
   * Debe tener la misma firma que el método protegido más el {@link Throwable}.
   */
  @SuppressWarnings("unused")
  private BigDecimal fallbackRate(String currency, Throwable t) {
    log.warn("[FX] Fallback activado para {} ({}). Devolviendo tasa por defecto {}",
        currency, t.toString(), FALLBACK_RATE);
    return FALLBACK_RATE;
  }
}
