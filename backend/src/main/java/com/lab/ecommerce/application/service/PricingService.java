package com.lab.ecommerce.application.service;

import com.lab.ecommerce.application.port.in.PricingUseCase;
import com.lab.ecommerce.application.port.out.ProductRepositoryPort;
import com.lab.ecommerce.domain.exception.ProductNotFoundException;
import com.lab.ecommerce.domain.model.Product;
import com.lab.ecommerce.domain.pricing.DiscountStrategy;
import com.lab.ecommerce.domain.pricing.DiscountStrategyFactory;
import com.lab.ecommerce.domain.pricing.PriceQuote;
import com.lab.ecommerce.domain.pricing.PricingContext;
import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso de pricing. Orquesta: cargar producto -> elegir estrategia (Factory)
 * -> aplicar descuento (Strategy/Template Method) -> construir el presupuesto (Builder).
 *
 * <p>No conoce las estrategias concretas: solo el {@link DiscountStrategyFactory}.</p>
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PricingService implements PricingUseCase {

  private final ProductRepositoryPort productRepository;
  private final DiscountStrategyFactory strategyFactory;

  @Override
  public PriceQuote quote(Long productId, PricingContext context) {
    Product product = productRepository.findById(productId)
        .orElseThrow(() -> new ProductNotFoundException(productId));

    BigDecimal unitPrice = product.getPrice();
    BigDecimal subtotal = unitPrice
        .multiply(BigDecimal.valueOf(context.quantity()))
        .setScale(2, RoundingMode.HALF_UP);

    DiscountStrategy strategy = strategyFactory.select(context);
    BigDecimal discount = strategy.calculateDiscount(subtotal, context);
    BigDecimal total = subtotal.subtract(discount).setScale(2, RoundingMode.HALF_UP);

    return PriceQuote.builder()
        .productId(productId)
        .unitPrice(unitPrice)
        .quantity(context.quantity())
        .subtotal(subtotal)
        .discount(discount)
        .total(total)
        .appliedStrategy(strategy.code())
        .build();
  }
}
