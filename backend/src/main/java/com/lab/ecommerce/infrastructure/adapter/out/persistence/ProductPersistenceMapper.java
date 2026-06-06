package com.lab.ecommerce.infrastructure.adapter.out.persistence;

import com.lab.ecommerce.domain.model.Product;
import org.mapstruct.Mapper;

/**
 * Mapper MapStruct entre el modelo de dominio {@link Product} y la entidad JPA.
 */
@Mapper(componentModel = "spring")
public interface ProductPersistenceMapper {

  ProductJpaEntity toEntity(Product product);

  Product toDomain(ProductJpaEntity entity);
}
