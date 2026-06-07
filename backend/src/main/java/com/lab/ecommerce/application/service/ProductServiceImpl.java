package com.lab.ecommerce.application.service;

import com.lab.ecommerce.application.port.in.ProductService;
import com.lab.ecommerce.application.port.out.ProductEventPublisherPort;
import com.lab.ecommerce.application.port.out.ProductRepositoryPort;
import com.lab.ecommerce.domain.event.ProductChangeType;
import com.lab.ecommerce.domain.event.ProductChangedEvent;
import com.lab.ecommerce.domain.exception.ProductNotFoundException;
import com.lab.ecommerce.domain.model.Product;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementacion del caso de uso de productos.
 *
 * <p>Depende unicamente de puertos ({@link ProductRepositoryPort}) y de modelo de
 * dominio, nunca de JPA ni de DTOs web. Aqui reside la logica de negocio.</p>
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

  private final ProductRepositoryPort repository;
  private final ProductEventPublisherPort eventPublisher;

  @Override
  public List<Product> findAll() {
    return repository.findAll();
  }

  @Override
  public Product findById(Long id) {
    return repository.findById(id)
        .orElseThrow(() -> new ProductNotFoundException(id));
  }

  @Override
  @Transactional
  public Product create(Product product) {
    product.setId(null);
    Product saved = repository.save(product);
    eventPublisher.publish(ProductChangedEvent.of(saved, ProductChangeType.CREATED));
    return saved;
  }

  @Override
  @Transactional
  public Product update(Long id, Product changes) {
    Product existing = repository.findById(id)
        .orElseThrow(() -> new ProductNotFoundException(id));
    existing.setName(changes.getName());
    existing.setDescription(changes.getDescription());
    existing.setPrice(changes.getPrice());
    existing.setStock(changes.getStock());
    existing.setCategory(changes.getCategory());
    Product saved = repository.save(existing);
    eventPublisher.publish(ProductChangedEvent.of(saved, ProductChangeType.UPDATED));
    return saved;
  }

  @Override
  @Transactional
  public void delete(Long id) {
    if (!repository.existsById(id)) {
      throw new ProductNotFoundException(id);
    }
    repository.deleteById(id);
  }
}
