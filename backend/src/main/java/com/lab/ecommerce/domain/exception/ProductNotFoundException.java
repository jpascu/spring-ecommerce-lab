package com.lab.ecommerce.domain.exception;

/**
 * Excepcion de dominio: no se encuentra un producto.
 *
 * <p>Es agnostica al framework (no usa anotaciones de Spring). La traduccion a
 * un codigo HTTP 404 se hace en el adaptador web, manteniendo el dominio limpio.</p>
 */
public class ProductNotFoundException extends RuntimeException {

  public ProductNotFoundException(Long id) {
    super("No existe el producto con id " + id);
  }
}
