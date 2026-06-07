package com.lab.ecommerce.application.common;

import java.util.List;
import java.util.function.Function;

/**
 * Resultado paginado genérico e independiente del framework.
 *
 * @param <T> tipo de los elementos
 */
public record PageResult<T>(
    List<T> content,
    int page,
    int size,
    long totalElements,
    int totalPages) {

  /**
   * Transforma el contenido a otro tipo conservando los metadatos de paginación.
   * Útil para pasar de {@code PageResult<Product>} a {@code PageResult<ProductResponse>}.
   */
  public <R> PageResult<R> map(Function<? super T, ? extends R> mapper) {
    List<R> mapped = content.stream().<R>map(mapper).toList();
    return new PageResult<R>(mapped, page, size, totalElements, totalPages);
  }
}
