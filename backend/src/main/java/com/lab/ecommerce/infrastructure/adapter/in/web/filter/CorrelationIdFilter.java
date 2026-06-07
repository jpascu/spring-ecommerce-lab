package com.lab.ecommerce.infrastructure.adapter.in.web.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filtro de entrada que asigna un identificador de correlacion (traceId) a cada
 * peticion HTTP y lo guarda en el MDC de SLF4J.
 *
 * <p>Asi todas las lineas de log de una misma peticion comparten el mismo traceId,
 * lo que facilita seguir el rastro extremo a extremo (clave en microservicios de
 * riesgos). Si el cliente envia la cabecera {@code X-Request-Id} la respetamos;
 * si no, generamos una. Tambien la devolvemos en la respuesta.</p>
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationIdFilter extends OncePerRequestFilter {

  public static final String TRACE_ID = "traceId";
  public static final String HEADER = "X-Request-Id";

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    String traceId = request.getHeader(HEADER);
    if (traceId == null || traceId.isBlank()) {
      traceId = UUID.randomUUID().toString().substring(0, 8);
    }
    MDC.put(TRACE_ID, traceId);
    response.setHeader(HEADER, traceId);
    try {
      filterChain.doFilter(request, response);
    } finally {
      MDC.remove(TRACE_ID);
    }
  }
}
