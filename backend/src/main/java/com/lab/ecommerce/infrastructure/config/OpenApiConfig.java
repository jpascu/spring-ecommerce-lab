package com.lab.ecommerce.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Metadatos globales de la documentación OpenAPI 3.
 *
 * <p>springdoc escanea los {@code @RestController} y genera la especificación en
 * {@code /v3/api-docs}; Swagger UI la muestra de forma interactiva en
 * {@code /swagger-ui.html}. Este bean solo personaliza título, versión y contacto.</p>
 */
@Configuration
public class OpenApiConfig {

  private static final String BEARER = "bearerAuth";

  @Bean
  public OpenAPI ecommerceOpenApi() {
    return new OpenAPI()
        .info(new Info()
            .title("spring-ecommerce-lab API")
            .description("API REST de aprendizaje (arquitectura hexagonal). "
                + "Gestión de productos y motor de presupuestos con descuentos.")
            .version("v1")
            .contact(new Contact().name("spring-ecommerce-lab"))
            .license(new License().name("MIT")))
        // Define el esquema JWT y el botón "Authorize" en Swagger UI
        .components(new Components().addSecuritySchemes(BEARER, new SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")))
        .addSecurityItem(new SecurityRequirement().addList(BEARER));
  }
}
