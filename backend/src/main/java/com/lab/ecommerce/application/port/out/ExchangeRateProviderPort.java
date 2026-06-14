package com.lab.ecommerce.application.port.out;

import java.math.BigDecimal;

/**
 * Puerto de salida (driven) hacia un proveedor externo de tipos de cambio.
 *
 * <p>El dominio/aplicación solo conoce esta abstracción. El adaptador concreto
 * (HTTP a un proveedor real, o el simulado de este lab) la implementa, y es ahí donde
 * se aplica la resiliencia.</p>
 */
public interface ExchangeRateProviderPort {

  /**
   * Devuelve el tipo de cambio EUR -&gt; divisa indicada.
   *
   * @param currency código ISO de la divisa (p.ej. "USD")
   * @return factor de conversión
   */
  BigDecimal getRate(String currency);
}
