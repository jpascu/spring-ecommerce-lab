package com.lab.ecommerce.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio Spring Data sobre la entidad JPA. Es un detalle de infraestructura
 * que usa el adaptador de persistencia; el dominio no lo conoce.
 */
public interface ProductJpaRepository extends JpaRepository<ProductJpaEntity, Long> {
}
