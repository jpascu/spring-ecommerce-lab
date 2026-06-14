package com.lab.ecommerce.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.lab.ecommerce.application.port.in.GetExchangeRateUseCase;
import com.lab.ecommerce.application.port.out.ExchangeRateProviderPort;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;

/**
 * Verifica el cacheo de los tipos de cambio: dos peticiones de la misma divisa solo
 * provocan UNA llamada al proveedor (la segunda es un acierto de cache).
 */
@SpringBootTest
@ActiveProfiles("test")
class ExchangeRateCacheIT {

  @Autowired
  private GetExchangeRateUseCase useCase;

  @Autowired
  private CacheManager cacheManager;

  @MockBean
  private ExchangeRateProviderPort provider;

  @BeforeEach
  void clearCache() {
    cacheManager.getCache("exchangeRates").clear();
  }

  @Test
  void la_segunda_consulta_se_sirve_de_cache() {
    when(provider.getRate("USD")).thenReturn(new BigDecimal("1.08"));

    BigDecimal first = useCase.rateFor("USD");
    BigDecimal second = useCase.rateFor("usd"); // misma clave (se normaliza a mayúsculas)

    assertThat(first).isEqualByComparingTo("1.08");
    assertThat(second).isEqualByComparingTo("1.08");
    verify(provider, times(1)).getRate("USD"); // solo una llamada real
  }
}
