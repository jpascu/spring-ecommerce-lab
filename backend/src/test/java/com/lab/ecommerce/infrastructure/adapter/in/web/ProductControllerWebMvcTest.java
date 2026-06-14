package com.lab.ecommerce.infrastructure.adapter.in.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.lab.ecommerce.application.port.in.PricingUseCase;
import com.lab.ecommerce.application.port.in.ProductService;
import com.lab.ecommerce.domain.exception.ProductNotFoundException;
import com.lab.ecommerce.domain.model.Product;
import com.lab.ecommerce.infrastructure.adapter.in.web.dto.ProductResponse;
import com.lab.ecommerce.infrastructure.adapter.in.web.mapper.ProductWebMapper;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Test de <em>slice</em> web con {@code @WebMvcTest}: arranca SOLO la capa MVC
 * (controllers, validación, serialización, advices), no el contexto completo. Las
 * dependencias del controller se sustituyen por {@code @MockBean}, de modo que el
 * test es rápido y se centra en el comportamiento HTTP.
 */
@WebMvcTest(ProductController.class)
class ProductControllerWebMvcTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private ProductService service;

  @MockBean
  private PricingUseCase pricing;

  @MockBean
  private ProductWebMapper mapper;

  @Test
  void getById_devuelve_json_del_producto() throws Exception {
    Product product = Product.builder().id(1L).name("Ratón").build();
    when(service.findById(1L)).thenReturn(product);
    when(mapper.toResponse(product)).thenReturn(new ProductResponse(
        1L, "Ratón", "óptico", new BigDecimal("19.99"), 5, "Periféricos", null, null));

    mockMvc.perform(get("/api/products/{id}", 1L))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("Ratón"));
  }

  @Test
  void getById_inexistente_devuelve_404() throws Exception {
    when(service.findById(eq(99L))).thenThrow(new ProductNotFoundException(99L));

    mockMvc.perform(get("/api/products/{id}", 99L))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404));
  }

  @Test
  void crear_con_body_invalido_devuelve_400_sin_llamar_al_servicio() throws Exception {
    String invalido = """
        {"name":"","price":-1,"stock":-1,"category":""}
        """;

    mockMvc.perform(post("/api/products")
            .contentType(MediaType.APPLICATION_JSON).content(invalido))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.fieldErrors").isArray());
  }
}
