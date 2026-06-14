package com.lab.ecommerce.infrastructure.actuator;

import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Verifica los endpoints de Actuator: el HealthIndicator de negocio, /actuator/info
 * y la métrica personalizada exportada a Prometheus.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser(roles = "ADMIN")
class ActuatorIT {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void health_incluye_el_indicador_de_stock() throws Exception {
    mockMvc.perform(get("/actuator/health"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("UP"))
        .andExpect(jsonPath("$.components.stock.status").value("UP"))
        .andExpect(jsonPath("$.components.stock.details.totalProducts").exists());
  }

  @Test
  void info_publica_los_metadatos_de_la_app() throws Exception {
    mockMvc.perform(get("/actuator/info"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.app.name").value("spring-ecommerce-lab"));
  }

  @Test
  void la_metrica_personalizada_de_presupuestos_se_registra() throws Exception {
    // 1) creamos un producto
    String body = """
        {"name":"Webcam","description":"1080p","price":"40.00","stock":10,"category":"Video"}
        """;
    String creado = mockMvc.perform(post("/api/products")
            .contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isCreated())
        .andReturn().getResponse().getContentAsString();
    Long id = JsonPath.parse(creado).read("$.id", Long.class);

    // 2) calculamos un presupuesto (incrementa el contador Micrometer)
    mockMvc.perform(post("/api/products/{id}/quote", id)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"tier":"VIP","quantity":1}
                """))
        .andExpect(status().isOk());

    // 3) la metrica aparece en /actuator/metrics con al menos un conteo
    mockMvc.perform(get("/actuator/metrics/ecommerce.quotes.calculated"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("ecommerce.quotes.calculated"))
        .andExpect(jsonPath("$.measurements[?(@.statistic == 'COUNT')].value",
            org.hamcrest.Matchers.hasItem(greaterThan(0.0))));
  }
}
