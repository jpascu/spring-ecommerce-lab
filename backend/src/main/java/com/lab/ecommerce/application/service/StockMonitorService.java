package com.lab.ecommerce.application.service;

import com.lab.ecommerce.application.port.in.StockMonitorUseCase;
import com.lab.ecommerce.application.port.out.ProductRepositoryPort;
import com.lab.ecommerce.domain.model.Product;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementacion del caso de uso de monitorizacion de stock.
 *
 * <p>La regla de negocio (que significa "stock bajo") vive en la aplicacion, no en
 * el adaptador. El adaptador solo decide CUANDO preguntar y QUE hacer con el resultado.</p>
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockMonitorService implements StockMonitorUseCase {

  private final ProductRepositoryPort repository;

  @Override
  public List<Product> findLowStock(int threshold) {
    return repository.findAll().stream()
        .filter(p -> p.getStock() != null && p.getStock() < threshold)
        .toList();
  }
}
