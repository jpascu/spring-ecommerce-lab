package com.lab.ecommerce.web;

import java.time.Instant;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller de prueba para verificar que la aplicacion arranca y responde
 */
@RestController
@RequestMapping("/api/ping")
public class PingController {

  /**
   * Devuelve un mensaje simple con la marca de tiempo del servidor
   *
   * @return mapa con el estado y la hora actual
   */
  @GetMapping
  public Map<String, Object> ping() {
    return Map.of(
        "status", "ok",
        "message", "ecommerce-backend funcionando",
        "timestamp", Instant.now().toString());
  }
}
