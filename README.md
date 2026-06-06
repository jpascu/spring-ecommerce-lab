# spring-ecommerce-lab

Proyecto de aprendizaje **full-stack** para dominar Spring Boot (backend) y Angular (frontend),
construido de forma incremental por fases. El dominio es una pequeña tienda / catálogo de e-commerce.

## Stack

| Capa        | Tecnología                                   |
|-------------|----------------------------------------------|
| Lenguaje    | Java 21                                       |
| Backend     | Spring Boot 3.3.x, Spring Web, Spring Data JPA|
| Build       | Maven 3.9+                                     |
| BBDD (dev)  | H2 en memoria                                  |
| BBDD (test) | PostgreSQL vía Testcontainers                  |
| Frontend    | Angular + TypeScript                           |
| Testing     | JUnit 5, Mockito, AssertJ, Testcontainers      |
| Extras      | Lombok, MapStruct, OpenAPI, Actuator, Resilience4j |

## Documentación

Guía completa de conceptos por fase (con sección especial sobre **Testcontainers**):
abre [`docs/guia.html`](docs/guia.html) en el navegador.

## Estructura del repositorio

```
spring-ecommerce-lab/
├── backend/                         # API REST Spring Boot (arquitectura hexagonal)
│   ├── pom.xml
│   └── src/main/java/com/lab/ecommerce/
│       ├── domain/                  # núcleo: modelo y excepciones (sin frameworks)
│       ├── application/             # puertos (in/out) y casos de uso
│       └── infrastructure/adapter/  # adaptadores web (in) y persistencia (out)
├── frontend/                        # SPA Angular (Fase 7)
└── docs/guia.html                   # guía de conceptos por fase
```

## Hoja de ruta

- [x] **Fase 0 — Setup**: estructura, Maven, Spring Boot arrancable, Actuator.
- [x] **Fase 1 — CRUD (hexagonal)**: arquitectura puertos/adaptadores, JPA + H2, DTOs + MapStruct, validación, CRUD REST.
- [ ] **Fase 2 — Servicios y buenas prácticas**: capas, `@ControllerAdvice`, paginación, logging.
- [ ] **Fase 3 — Patrones de diseño**: Strategy, Factory, Builder, Template Method.
- [ ] **Fase 4 — Testing**: JUnit 5, Mockito, AssertJ, slices (`@WebMvcTest`, `@DataJpaTest`), Testcontainers.
- [ ] **Fase 5 — Librerías Spring**: OpenAPI/Swagger, Actuator, Resilience4j, Cache, Security + JWT.
- [ ] **Fase 6 — Async / eventos**: `@Async`, eventos de aplicación, mensajería.
- [ ] **Fase 7 — Frontend Angular**: SPA que consume la API.
- [ ] **Fase 8 — Calidad / entrega**: Docker, docker-compose, GitHub Actions CI.

## Cómo ejecutar el backend

```bash
cd backend
mvn spring-boot:run
```

Comprobaciones rápidas:

- API ping: http://localhost:8080/api/ping
- Health: http://localhost:8080/actuator/health

## Cómo ejecutar los tests

```bash
cd backend
mvn test     # tests unitarios (*Test, Surefire)
mvn verify   # + tests de integración (*IT, Failsafe): CRUD end-to-end
```

## Endpoints principales

- `GET/POST /api/products`, `GET/PUT/DELETE /api/products/{id}` — CRUD de productos
- Consola H2: http://localhost:8080/h2-console (JDBC `jdbc:h2:mem:shopdb`, user `sa`)
