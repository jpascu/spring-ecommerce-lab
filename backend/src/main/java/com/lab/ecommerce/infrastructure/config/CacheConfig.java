package com.lab.ecommerce.infrastructure.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/**
 * Activa la abstracción de cache de Spring ({@code @Cacheable}, {@code @CacheEvict}...).
 *
 * <p>El proveedor concreto es <strong>Caffeine</strong>, configurado de forma
 * declarativa en {@code application.yml} ({@code spring.cache.caffeine.spec}) con TTL
 * y tamaño máximo. Spring detecta Caffeine en el classpath y crea el CacheManager.</p>
 */
@Configuration
@EnableCaching
public class CacheConfig {
}
