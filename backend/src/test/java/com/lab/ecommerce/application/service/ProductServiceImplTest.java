package com.lab.ecommerce.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.lab.ecommerce.application.port.out.ProductEventPublisherPort;
import com.lab.ecommerce.application.port.out.ProductRepositoryPort;
import com.lab.ecommerce.domain.event.ProductChangedEvent;
import com.lab.ecommerce.domain.exception.ProductNotFoundException;
import com.lab.ecommerce.domain.model.Product;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test unitario del caso de uso con <strong>Mockito</strong>. No levanta Spring ni
 * BBDD: los puertos de salida se sustituyen por <em>mocks</em>, de modo que probamos
 * solo la lógica del servicio (rápido y aislado).
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

  @Mock
  private ProductRepositoryPort repository;

  @Mock
  private ProductEventPublisherPort eventPublisher;

  @InjectMocks
  private ProductServiceImpl service;

  private Product sampleProduct(Long id) {
    return Product.builder()
        .id(id).name("Teclado").description("Mecánico")
        .price(new BigDecimal("49.90")).stock(10).category("Periféricos")
        .build();
  }

  @Test
  void create_guarda_resetea_id_y_publica_evento() {
    Product input = sampleProduct(99L); // id que debe ignorarse
    // Devolvemos una COPIA con id asignado (no mutamos el argumento, para poder
    // verificar con el captor que al servicio le llegó el id reseteado a null).
    when(repository.save(any(Product.class))).thenAnswer(inv -> {
      Product arg = inv.getArgument(0);
      return Product.builder()
          .id(1L).name(arg.getName()).description(arg.getDescription())
          .price(arg.getPrice()).stock(arg.getStock()).category(arg.getCategory())
          .build();
    });

    Product result = service.create(input);

    ArgumentCaptor<Product> savedCaptor = ArgumentCaptor.forClass(Product.class);
    verify(repository).save(savedCaptor.capture());
    assertThat(savedCaptor.getValue().getId()).isNull(); // el id se reseteó antes de guardar
    assertThat(result.getId()).isEqualTo(1L);
    verify(eventPublisher).publish(any(ProductChangedEvent.class));
  }

  @Test
  void findById_inexistente_lanza_excepcion() {
    when(repository.findById(42L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.findById(42L))
        .isInstanceOf(ProductNotFoundException.class);
  }

  @Test
  void update_inexistente_no_guarda_ni_publica() {
    when(repository.findById(7L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.update(7L, sampleProduct(null)))
        .isInstanceOf(ProductNotFoundException.class);

    verify(repository, never()).save(any());
    verify(eventPublisher, never()).publish(any());
  }

  @Test
  void delete_inexistente_lanza_y_no_borra() {
    when(repository.existsById(5L)).thenReturn(false);

    assertThatThrownBy(() -> service.delete(5L))
        .isInstanceOf(ProductNotFoundException.class);

    verify(repository, never()).deleteById(any());
  }
}
