package com.lab.ecommerce.infrastructure.adapter.in.web.advice;

import java.time.Instant;
import java.util.List;

/**
 * Cuerpo de error estandar devuelto por la API.
 *
 * <p>Tener un formato unico de error facilita el consumo desde el frontend y la
 * observabilidad. Incluye un {@code traceId} para correlacionar con los logs.</p>
 */
public record ApiError(
    Instant timestamp,
    int status,
    String error,
    String message,
    String path,
    String traceId,
    List<FieldErrorItem> fieldErrors) {

  /**
   * Detalle de un error de validacion sobre un campo concreto.
   */
  public record FieldErrorItem(String field, String message) {
  }
}
