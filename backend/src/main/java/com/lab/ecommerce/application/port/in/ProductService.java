package com.lab.ecommerce.application.port.in;

import com.lab.ecommerce.application.common.PageQuery;
import com.lab.ecommerce.application.common.PageResult;
import com.lab.ecommerce.domain.model.Product;
import java.util.List;

/**
 * Puerto de entrada (driving port): caso de uso de gestion de productos.
 *
 * <p>Define lo que la aplicacion ofrece al exterior en terminos de dominio
 * ({@link Product}), sin acoplarse a DTOs web ni a HTTP. El adaptador de entrada
 * (controller) depende de esta interfaz.</p>
 */
public interface ProductService {

  List<Product> findAll();

  PageResult<Product> findAll(PageQuery query);

  Product findById(Long id);

  Product create(Product product);

  Product update(Long id, Product product);

  void delete(Long id);
}
