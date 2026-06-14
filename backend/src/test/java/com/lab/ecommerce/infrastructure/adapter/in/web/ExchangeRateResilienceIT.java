package com.lab.ecommerce.infrastructure.adapter.in.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Verifica la resiliencia del proveedor externo de tipos de cambio.
 *
 * <p>Con una divisa válida devuelve la tasa real; cuando el proveedor falla (divisa
 * "BOOM"), Resilience4j reintenta y, al agotarse, aplica el fallback: el endpoint
 * sigue respondiendo 200 con la tasa por defecto en lugar de propagar el error.</p>
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser
class ExchangeRateResilienceIT {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void divisa_valida_devuelve_la_tasa_real() throws Exception {
    mockMvc.perform(get("/api/exchange-rates/{c}", "USD"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.currency").value("USD"))
        .andExpect(jsonPath("$.rate").value(1.08));
  }

  @Test
  void proveedor_caido_activa_el_fallback() throws Exception {
    mockMvc.perform(get("/api/exchange-rates/{c}", "BOOM"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.currency").value("BOOM"))
        .andExpect(jsonPath("$.rate").value(1)); // tasa por defecto del fallback
  }
}
