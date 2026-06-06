package com.lab.ecommerce.infrastructure.adapter.in.web.advice;

import com.lab.ecommerce.domain.exception.ProductNotFoundException;
import java.time.Instant;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Traduce excepciones de dominio a respuestas HTTP en el borde web.
 *
 * <p>Mantener esta traduccion en el adaptador (y no en el dominio) es lo que
 * permite que la excepcion de dominio sea agnostica al framework. En la Fase 2
 * ampliaremos este manejador (validacion, formato de error estandar, etc.).</p>
 */
@RestControllerAdvice
public class WebExceptionHandler {

  @ExceptionHandler(ProductNotFoundException.class)
  public ResponseEntity<Map<String, Object>> handleNotFound(ProductNotFoundException ex) {
    Map<String, Object> body = Map.of(
        "timestamp", Instant.now().toString(),
        "status", HttpStatus.NOT_FOUND.value(),
        "error", "Not Found",
        "message", ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
  }
}
