package com.lab.ecommerce.application.common;

/**
 * Petición de paginación independiente del framework.
 *
 * <p>No usamos {@code Pageable} de Spring en los puertos para no acoplar el
 * dominio/aplicación a una tecnología. El adaptador de persistencia traduce este
 * objeto a {@code PageRequest}.</p>
 */
public record PageQuery(int page, int size, String sortBy, Direction direction) {

  public enum Direction { ASC, DESC }

  private static final int DEFAULT_SIZE = 20;
  private static final int MAX_SIZE = 100;

  /**
   * Factoría que normaliza/saneca los valores de entrada (defensa frente a
   * parámetros inválidos provenientes del exterior).
   */
  public static PageQuery of(int page, int size, String sortBy, String direction) {
    int safePage = Math.max(page, 0);
    int safeSize = size <= 0 ? DEFAULT_SIZE : Math.min(size, MAX_SIZE);
    String safeSort = (sortBy == null || sortBy.isBlank()) ? "id" : sortBy;
    Direction dir = "desc".equalsIgnoreCase(direction) ? Direction.DESC : Direction.ASC;
    return new PageQuery(safePage, safeSize, safeSort, dir);
  }
}
