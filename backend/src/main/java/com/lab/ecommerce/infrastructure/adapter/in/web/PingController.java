package com.lab.ecommerce.infrastructure.adapter.in.web;

import java.time.Instant;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Adaptador web de prueba para verificar que la aplicacion responde.
 */
@RestController
@RequestMapping("/api/ping")
public class PingController {

  @GetMapping
  public Map<String, Object> ping() {
    return Map.of(
        "status", "ok",
        "message", "ecommerce-backend funcionando",
        "timestamp", Instant.now().toString());
  }
}
