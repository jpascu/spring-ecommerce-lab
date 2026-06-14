package com.lab.ecommerce.domain.exception;

/**
 * Error al invocar un servicio externo (p.ej. un proveedor de tipos de cambio).
 *
 * <p>Representa un fallo de integración: timeout, 5xx, indisponibilidad... Es el tipo
 * de fallo que protegemos con Resilience4j (retry + circuit breaker + fallback).</p>
 */
public class ExternalServiceException extends RuntimeException {

  public ExternalServiceException(String message) {
    super(message);
  }

  public ExternalServiceException(String message, Throwable cause) {
    super(message, cause);
  }
}
