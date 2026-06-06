package com.lab.ecommerce.infrastructure.adapter.in.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Test de integracion del CRUD que ejercita todas las capas hexagonales
 * (controller -> puerto in -> servicio -> puerto out -> adaptador JPA -> H2).
 * Usa el perfil "test" para que NO se ejecute el DataSeeder de "dev".
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductControllerIT {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void crud_completo() throws Exception {
    String nuevo = """
        {"name":"Webcam HD","description":"1080p","price":59.99,"stock":20,"category":"Perifericos"}
        """;

    String creado = mockMvc.perform(post("/api/products")
            .contentType(MediaType.APPLICATION_JSON).content(nuevo))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").isNumber())
        .andExpect(jsonPath("$.name").value("Webcam HD"))
        .andReturn().getResponse().getContentAsString();

    Long id = com.jayway.jsonpath.JsonPath.parse(creado).read("$.id", Long.class);

    mockMvc.perform(get("/api/products/{id}", id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.category").value("Perifericos"));

    mockMvc.perform(delete("/api/products/{id}", id))
        .andExpect(status().isNoContent());

    mockMvc.perform(get("/api/products/{id}", id))
        .andExpect(status().isNotFound());
  }

  @Test
  void crear_invalido_devuelve_400() throws Exception {
    String invalido = """
        {"name":"","price":-5,"stock":-1,"category":""}
        """;
    mockMvc.perform(post("/api/products")
            .contentType(MediaType.APPLICATION_JSON).content(invalido))
        .andExpect(status().isBadRequest());
  }
}
