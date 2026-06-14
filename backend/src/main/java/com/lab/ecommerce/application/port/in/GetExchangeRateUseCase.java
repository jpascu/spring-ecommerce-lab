package com.lab.ecommerce.application.port.in;

import java.math.BigDecimal;

/**
 * Puerto de entrada (driving): obtener el tipo de cambio para una divisa.
 */
public interface GetExchangeRateUseCase {

  BigDecimal rateFor(String currency);
}
