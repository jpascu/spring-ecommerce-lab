package com.lab.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Punto de entrada de la aplicacion Spring Boot.
 *
 * <p>{@code @EnableScheduling} habilita las tareas programadas (@Scheduled), que
 * usamos como ejemplo de adaptador de entrada distinto a HTTP.</p>
 */
@SpringBootApplication
@EnableScheduling
public class EcommerceApplication {

  public static void main(String[] args) {
    SpringApplication.run(EcommerceApplication.class, args);
  }
}
