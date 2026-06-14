package com.lab.ecommerce.infrastructure.adapter.in.web;

import com.lab.ecommerce.application.port.in.GetExchangeRateUseCase;
import com.lab.ecommerce.infrastructure.adapter.in.web.dto.ExchangeRateResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Adaptador de entrada que expone los tipos de cambio. Demuestra la resiliencia: la
 * divisa "BOOM" siempre falla en el proveedor, pero el endpoint sigue respondiendo
 * (gracias al fallback de Resilience4j) en lugar de devolver un error.
 */
@RestController
@RequestMapping("/api/exchange-rates")
@RequiredArgsConstructor
@Tag(name = "Tipos de cambio", description = "Proveedor externo protegido con Resilience4j")
public class ExchangeRateController {

  private final GetExchangeRateUseCase exchangeRate;

  @Operation(summary = "Obtener el tipo de cambio EUR -> divisa",
      description = "Usa un proveedor externo simulado con retry + circuit breaker + fallback.")
  @GetMapping("/{currency}")
  public ExchangeRateResponse getRate(@PathVariable String currency) {
    return new ExchangeRateResponse(currency.toUpperCase(), exchangeRate.rateFor(currency));
  }
}
