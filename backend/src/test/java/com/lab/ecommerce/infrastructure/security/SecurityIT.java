package com.lab.ecommerce.infrastructure.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Test de integración de la seguridad JWT de extremo a extremo: login, acceso sin
 * token, y autorización por rol (USER vs ADMIN). No usa {@code @WithMockUser}: emite
 * y envía tokens reales para ejercitar toda la cadena de filtros.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityIT {

  private static final String NEW_PRODUCT = """
      {"name":"SSD","description":"NVMe","price":"79.00","stock":5,"category":"Almacenamiento"}
      """;

  @Autowired
  private MockMvc mockMvc;

  private String login(String username, String password) throws Exception {
    String body = """
        {"username":"%s","password":"%s"}
        """.formatted(username, password);
    String json = mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();
    return JsonPath.parse(json).read("$.accessToken", String.class);
  }

  @Test
  void login_con_credenciales_validas_devuelve_token() throws Exception {
    mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"username":"admin","password":"password"}
                """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").isNotEmpty())
        .andExpect(jsonPath("$.tokenType").value("Bearer"));
  }

  @Test
  void login_con_credenciales_invalidas_devuelve_401() throws Exception {
    mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"username":"admin","password":"mala"}
                """))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void sin_token_el_endpoint_protegido_devuelve_401() throws Exception {
    mockMvc.perform(get("/api/products"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void usuario_autenticado_puede_leer_el_catalogo() throws Exception {
    String token = login("user", "password");
    mockMvc.perform(get("/api/products").header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
        .andExpect(status().isOk());
  }

  @Test
  void usuario_sin_rol_admin_no_puede_crear_productos() throws Exception {
    String token = login("user", "password");
    mockMvc.perform(post("/api/products")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON).content(NEW_PRODUCT))
        .andExpect(status().isForbidden());
  }

  @Test
  void admin_puede_crear_productos() throws Exception {
    String token = login("admin", "password");
    mockMvc.perform(post("/api/products")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON).content(NEW_PRODUCT))
        .andExpect(status().isCreated());
  }
}
