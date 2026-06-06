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

## Estructura del repositorio

```
spring-ecommerce-lab/
├── backend/        # API REST Spring Boot
│   ├── pom.xml
│   └── src/
└── frontend/       # SPA Angular (se añade en la Fase 7)
```

## Hoja de ruta

- [x] **Fase 0 — Setup**: estructura, Maven, Spring Boot arrancable, Actuator.
- [ ] **Fase 1 — CRUD**: entidades JPA, H2, repositorios, DTOs + MapStruct, validación, controllers REST.
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
mvn test
```
