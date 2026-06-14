package com.lab.ecommerce.infrastructure.adapter.in.web;

import com.lab.ecommerce.application.common.PageQuery;
import com.lab.ecommerce.application.common.PageResult;
import com.lab.ecommerce.application.port.in.PricingUseCase;
import com.lab.ecommerce.application.port.in.ProductService;
import com.lab.ecommerce.domain.pricing.CustomerTier;
import com.lab.ecommerce.domain.pricing.PriceQuote;
import com.lab.ecommerce.domain.pricing.PricingContext;
import com.lab.ecommerce.infrastructure.adapter.in.web.dto.PriceQuoteResponse;
import com.lab.ecommerce.infrastructure.adapter.in.web.dto.ProductRequest;
import com.lab.ecommerce.infrastructure.adapter.in.web.dto.ProductResponse;
import com.lab.ecommerce.infrastructure.adapter.in.web.dto.QuoteRequest;
import com.lab.ecommerce.infrastructure.adapter.in.web.mapper.ProductWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;
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
@Tag(name = "Productos", description = "CRUD de productos y cálculo de presupuestos")
public class ProductController {

  private final ProductService service;
  private final PricingUseCase pricing;
  private final ProductWebMapper mapper;

  @GetMapping
  public PageResult<ProductResponse> findAll(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(defaultValue = "id") String sort,
      @RequestParam(defaultValue = "asc") String direction) {
    PageQuery query = PageQuery.of(page, size, sort, direction);
    return service.findAll(query).map(mapper::toResponse);
  }

  @Operation(summary = "Obtener un producto por id")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Producto encontrado"),
      @ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @io.swagger.v3.oas.annotations.media.Content)
  })
  @GetMapping("/{id}")
  public ProductResponse findById(@PathVariable Long id) {
    return mapper.toResponse(service.findById(id));
  }

  @Operation(summary = "Crear un producto")
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

  @Operation(summary = "Calcular presupuesto",
      description = "Aplica el motor de descuentos (cupón > volumen > fidelidad) y devuelve el total.")
  @PostMapping("/{id}/quote")
  public PriceQuoteResponse quote(@PathVariable Long id, @Valid @RequestBody QuoteRequest request) {
    PricingContext context = new PricingContext(
        parseTier(request.tier()), request.quantity(), request.couponCode());
    PriceQuote quote = pricing.quote(id, context);
    return new PriceQuoteResponse(
        quote.getProductId(), quote.getUnitPrice(), quote.getQuantity(),
        quote.getSubtotal(), quote.getDiscount(), quote.getTotal(), quote.getAppliedStrategy());
  }

  private CustomerTier parseTier(String tier) {
    if (tier == null || tier.isBlank()) {
      return CustomerTier.STANDARD;
    }
    try {
      return CustomerTier.valueOf(tier.trim().toUpperCase());
    } catch (IllegalArgumentException ex) {
      throw new IllegalArgumentException("tier invalido: " + tier + " (use STANDARD, PREMIUM o VIP)");
    }
  }
}
