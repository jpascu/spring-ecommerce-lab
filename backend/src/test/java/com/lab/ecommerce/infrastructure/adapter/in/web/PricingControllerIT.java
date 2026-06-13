package com.lab.ecommerce.infrastructure.adapter.in.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Test de integración del endpoint de presupuesto, ejercitando el motor de
 * descuentos a través de todas las capas hexagonales.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PricingControllerIT {

  @Autowired
  private MockMvc mockMvc;

  private Long crearProducto(String precio) throws Exception {
    String body = """
        {"name":"Monitor","description":"27 pulgadas","price":%s,"stock":100,"category":"Pantallas"}
        """.formatted(precio);
    String creado = mockMvc.perform(post("/api/products")
            .contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isCreated())
        .andReturn().getResponse().getContentAsString();
    return JsonPath.parse(creado).read("$.id", Long.class);
  }

  @Test
  void presupuesto_con_cupon_calcula_total_correcto() throws Exception {
    Long id = crearProducto("100.00");
    String quote = """
        {"tier":"VIP","quantity":2,"couponCode":"SAVE10"}
        """;
    mockMvc.perform(post("/api/products/{id}/quote", id)
            .contentType(MediaType.APPLICATION_JSON).content(quote))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.subtotal").value(200.00))
        .andExpect(jsonPath("$.discount").value(20.00))
        .andExpect(jsonPath("$.total").value(180.00))
        .andExpect(jsonPath("$.appliedStrategy").value("COUPON"));
  }

  @Test
  void presupuesto_estandar_sin_descuento() throws Exception {
    Long id = crearProducto("50.00");
    String quote = """
        {"tier":"STANDARD","quantity":1}
        """;
    mockMvc.perform(post("/api/products/{id}/quote", id)
            .contentType(MediaType.APPLICATION_JSON).content(quote))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.total").value(50.00))
        .andExpect(jsonPath("$.appliedStrategy").value("NONE"));
  }

  @Test
  void tier_invalido_devuelve_400() throws Exception {
    Long id = crearProducto("10.00");
    String quote = """
        {"tier":"GOLD","quantity":1}
        """;
    mockMvc.perform(post("/api/products/{id}/quote", id)
            .contentType(MediaType.APPLICATION_JSON).content(quote))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400));
  }
}
