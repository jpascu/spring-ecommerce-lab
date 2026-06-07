package com.lab.ecommerce.application.port.in;

import com.lab.ecommerce.domain.model.Product;
import java.util.List;

/**
 * Puerto de entrada (driving): caso de uso de monitorizacion de stock.
 *
 * <p>Separado del CRUD para mantener interfaces pequenas y cohesionadas (ISP).
 * Lo invocan adaptadores de entrada como una tarea programada o un endpoint.</p>
 */
public interface StockMonitorUseCase {

  /**
   * Devuelve los productos cuyo stock esta por debajo del umbral indicado.
   */
  List<Product> findLowStock(int threshold);
}
