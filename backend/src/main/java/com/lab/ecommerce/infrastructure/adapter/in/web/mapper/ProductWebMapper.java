package com.lab.ecommerce.infrastructure.adapter.in.web.mapper;

import com.lab.ecommerce.domain.model.Product;
import com.lab.ecommerce.infrastructure.adapter.in.web.dto.ProductRequest;
import com.lab.ecommerce.infrastructure.adapter.in.web.dto.ProductResponse;
import org.mapstruct.Mapper;

/**
 * Mapper MapStruct entre los DTOs web y el modelo de dominio {@link Product}.
 */
@Mapper(componentModel = "spring")
public interface ProductWebMapper {

  Product toDomain(ProductRequest request);

  ProductResponse toResponse(Product product);
}
