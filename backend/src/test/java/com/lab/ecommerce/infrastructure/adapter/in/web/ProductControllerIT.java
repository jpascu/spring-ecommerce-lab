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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Test de integracion del CRUD que ejercita todas las capas hexagonales
 * (controller -> puerto in -> servicio -> puerto out -> adaptador JPA -> H2).
 * Usa el perfil "test" para que NO se ejecute el DataSeeder de "dev".
 *
 * <p>Se autentica como ADMIN (operaciones de escritura del catálogo lo requieren).</p>
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser(roles = "ADMIN")
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
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.path").value("/api/products/" + id));
  }

  @Test
  void listar_paginado_devuelve_estructura_de_pagina() throws Exception {
    mockMvc.perform(get("/api/products?page=0&size=5&sort=name&direction=asc"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.page").value(0))
        .andExpect(jsonPath("$.size").value(5))
        .andExpect(jsonPath("$.totalElements").exists())
        .andExpect(jsonPath("$.totalPages").exists());
  }

  @Test
  void crear_invalido_devuelve_400() throws Exception {
    String invalido = """
        {"name":"","price":-5,"stock":-1,"category":""}
        """;
    mockMvc.perform(post("/api/products")
            .contentType(MediaType.APPLICATION_JSON).content(invalido))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.fieldErrors").isArray())
        .andExpect(jsonPath("$.fieldErrors.length()").value(org.hamcrest.Matchers.greaterThanOrEqualTo(3)));
  }
}
