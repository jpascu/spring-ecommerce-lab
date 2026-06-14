package com.lab.ecommerce.infrastructure.config;

import java.util.concurrent.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Activa la ejecución asíncrona ({@code @Async}) y define el pool de hilos que la
 * sirve.
 *
 * <p>Tener un {@link Executor} propio (en vez del SimpleAsyncTaskExecutor por
 * defecto) permite acotar la concurrencia, dar nombre a los hilos (útil en logs y
 * trazas) y aislar este trabajo en segundo plano del resto de la aplicación.</p>
 */
@Configuration
@EnableAsync
public class AsyncConfig {

  @Bean("lakeTaskExecutor")
  public Executor lakeTaskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(2);
    executor.setMaxPoolSize(5);
    executor.setQueueCapacity(50);
    executor.setThreadNamePrefix("lake-");
    executor.initialize();
    return executor;
  }
}
