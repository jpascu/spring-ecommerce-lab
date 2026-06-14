package com.lab.ecommerce.application.service;

import com.lab.ecommerce.application.port.in.GetExchangeRateUseCase;
import com.lab.ecommerce.application.port.out.ExchangeRateProviderPort;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * Caso de uso de tipos de cambio. Delega en el puerto de salida; no sabe que detrás
 * hay un proveedor externo protegido con Resilience4j (eso vive en el adaptador).
 *
 * <p>Los tipos de cambio cambian poco y obtenerlos es "caro" (llamada externa), así
 * que cacheamos el resultado: la cache envuelve a la resiliencia, de modo que un
 * acierto de cache evita por completo la llamada al proveedor.</p>
 */
@Service
@RequiredArgsConstructor
public class ExchangeRateService implements GetExchangeRateUseCase {

  private final ExchangeRateProviderPort exchangeRateProvider;

  @Override
  @Cacheable(cacheNames = "exchangeRates", key = "#currency.toUpperCase()")
  public BigDecimal rateFor(String currency) {
    return exchangeRateProvider.getRate(currency);
  }
}
