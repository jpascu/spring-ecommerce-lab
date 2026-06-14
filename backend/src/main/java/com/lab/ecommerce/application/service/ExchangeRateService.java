package com.lab.ecommerce.application.service;

import com.lab.ecommerce.application.port.in.GetExchangeRateUseCase;
import com.lab.ecommerce.application.port.out.ExchangeRateProviderPort;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Caso de uso de tipos de cambio. Delega en el puerto de salida; no sabe que detrás
 * hay un proveedor externo protegido con Resilience4j (eso vive en el adaptador).
 */
@Service
@RequiredArgsConstructor
public class ExchangeRateService implements GetExchangeRateUseCase {

  private final ExchangeRateProviderPort exchangeRateProvider;

  @Override
  public BigDecimal rateFor(String currency) {
    return exchangeRateProvider.getRate(currency);
  }
}
