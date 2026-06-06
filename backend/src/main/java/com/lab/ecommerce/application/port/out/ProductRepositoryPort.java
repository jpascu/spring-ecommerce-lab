package com.lab.ecommerce.application.port.out;

import com.lab.ecommerce.domain.model.Product;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida (driven port): persistencia de productos.
 *
 * <p>La aplicacion depende de esta abstraccion, no de JPA. El adaptador de
 * persistencia la implementa. Asi podriamos cambiar de JPA a otra tecnologia
 * sin tocar la logica de negocio.</p>
 */
public interface ProductRepositoryPort {

  List<Product> findAll();

  Optional<Product> findById(Long id);

  Product save(Product product);

  boolean existsById(Long id);

  void deleteById(Long id);
}
