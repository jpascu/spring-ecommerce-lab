package com.lab.ecommerce.infrastructure.adapter.in.web.advice;

import com.lab.ecommerce.domain.exception.ProductNotFoundException;
import com.lab.ecommerce.infrastructure.adapter.in.web.advice.ApiError.FieldErrorItem;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Traduce excepciones a respuestas HTTP con un formato de error estandar ({@link ApiError}).
 *
 * <p>Mantener esta traduccion en el adaptador (y no en el dominio) permite que las
 * excepciones de dominio sean agnosticas al framework. Centralizar aqui el manejo
 * evita repetir try/catch en cada controller.</p>
 */
@Slf4j
@RestControllerAdvice
public class WebExceptionHandler {

  @ExceptionHandler(ProductNotFoundException.class)
  public ResponseEntity<ApiError> handleNotFound(ProductNotFoundException ex,
      HttpServletRequest request) {
    return build(HttpStatus.NOT_FOUND, ex.getMessage(), request, List.of());
  }

  /**
   * Errores de validacion de @Valid: devuelve 400 con el detalle por campo.
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex,
      HttpServletRequest request) {
    List<FieldErrorItem> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
        .map(fe -> new FieldErrorItem(fe.getField(), fe.getDefaultMessage()))
        .toList();
    return build(HttpStatus.BAD_REQUEST, "Error de validacion", request, fieldErrors);
  }

  /**
   * Red de seguridad: cualquier error no controlado se traduce a 500 sin filtrar
   * detalles internos al cliente (pero se registra en el log con el traceId).
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiError> handleUnexpected(Exception ex, HttpServletRequest request) {
    log.error("Error no controlado en {}", request.getRequestURI(), ex);
    return build(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno", request, List.of());
  }

  private ResponseEntity<ApiError> build(HttpStatus status, String message,
      HttpServletRequest request, List<FieldErrorItem> fieldErrors) {
    ApiError body = new ApiError(
        Instant.now(),
        status.value(),
        status.getReasonPhrase(),
        message,
        request.getRequestURI(),
        MDC.get("traceId"),
        fieldErrors);
    return ResponseEntity.status(status).body(body);
  }
}
