package com.lab.ecommerce.infrastructure.adapter.in.web;

import com.lab.ecommerce.application.port.in.ProductService;
import com.lab.ecommerce.infrastructure.adapter.in.web.dto.ProductRequest;
import com.lab.ecommerce.infrastructure.adapter.in.web.dto.ProductResponse;
import com.lab.ecommerce.infrastructure.adapter.in.web.mapper.ProductWebMapper;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Adaptador de entrada (driving adapter) REST. Traduce HTTP/JSON hacia el puerto
 * de entrada {@link ProductService}, mapeando DTOs a modelo de dominio.
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

  private final ProductService service;
  private final ProductWebMapper mapper;

  @GetMapping
  public List<ProductResponse> findAll() {
    return service.findAll().stream().map(mapper::toResponse).toList();
  }

  @GetMapping("/{id}")
  public ProductResponse findById(@PathVariable Long id) {
    return mapper.toResponse(service.findById(id));
  }

  @PostMapping
  public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request,
      UriComponentsBuilder uriBuilder) {
    ProductResponse created = mapper.toResponse(service.create(mapper.toDomain(request)));
    URI location = uriBuilder.path("/api/products/{id}")
        .buildAndExpand(created.id())
        .toUri();
    return ResponseEntity.created(location).body(created);
  }

  @PutMapping("/{id}")
  public ProductResponse update(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
    return mapper.toResponse(service.update(id, mapper.toDomain(request)));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    service.delete(id);
  }
}
