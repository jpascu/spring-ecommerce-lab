package com.lab.ecommerce.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Modelo de dominio puro de un producto.
 *
 * <p>No contiene anotaciones de JPA ni de ningun framework: es el corazon de la
 * aplicacion. Los adaptadores (web, persistencia) se mapean hacia y desde este
 * modelo, de modo que el dominio permanece aislado de detalles tecnicos.</p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

  private Long id;
  private String name;
  private String description;
  private BigDecimal price;
  private Integer stock;
  private String category;
  private Instant createdAt;
  private Instant updatedAt;
}
