package com.lab.ecommerce.infrastructure.adapter.out.persistence;

import com.lab.ecommerce.application.common.PageQuery;
import com.lab.ecommerce.application.common.PageResult;
import com.lab.ecommerce.application.port.out.ProductRepositoryPort;
import com.lab.ecommerce.domain.model.Product;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

/**
 * Adaptador de salida que implementa el puerto {@link ProductRepositoryPort}
 * usando Spring Data JPA. Traduce entre dominio y entidad JPA mediante el mapper.
 */
@Component
@RequiredArgsConstructor
public class ProductPersistenceAdapter implements ProductRepositoryPort {

  private final ProductJpaRepository jpaRepository;
  private final ProductPersistenceMapper mapper;

  @Override
  public List<Product> findAll() {
    return jpaRepository.findAll().stream()
        .map(mapper::toDomain)
        .toList();
  }

  @Override
  public PageResult<Product> findAll(PageQuery query) {
    Sort.Direction direction = query.direction() == PageQuery.Direction.DESC
        ? Sort.Direction.DESC
        : Sort.Direction.ASC;
    PageRequest pageRequest = PageRequest.of(
        query.page(), query.size(), Sort.by(direction, query.sortBy()));
    Page<ProductJpaEntity> page = jpaRepository.findAll(pageRequest);
    return new PageResult<>(
        page.getContent().stream().map(mapper::toDomain).toList(),
        page.getNumber(),
        page.getSize(),
        page.getTotalElements(),
        page.getTotalPages());
  }

  @Override
  public Optional<Product> findById(Long id) {
    return jpaRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public Product save(Product product) {
    ProductJpaEntity saved = jpaRepository.save(mapper.toEntity(product));
    return mapper.toDomain(saved);
  }

  @Override
  public boolean existsById(Long id) {
    return jpaRepository.existsById(id);
  }

  @Override
  public void deleteById(Long id) {
    jpaRepository.deleteById(id);
  }
}
