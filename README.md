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
| Frontend    | Angular 20 (standalone) + TypeScript           |
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
│       └── ...
│   └── Dockerfile                   # imagen del backend (multi-stage)
├── frontend/                        # SPA Angular (Fase 7)
│   ├── Dockerfile                   # build + nginx (Fase 8)
│   └── nginx.conf                   # SPA fallback + proxy /api
├── docker-compose.yml               # PostgreSQL + backend + frontend (Fase 8)
├── .github/workflows/ci.yml         # CI: build + tests (Fase 8)
└── docs/guia.html                   # guía de conceptos por fase
```

## Hoja de ruta

- [x] **Fase 0 — Setup**: estructura, Maven, Spring Boot arrancable, Actuator.
- [x] **Fase 1 — CRUD (hexagonal)**: arquitectura puertos/adaptadores, JPA + H2, DTOs + MapStruct, validación, CRUD REST.
- [x] **Fase 2 — Buenas prácticas**: manejo global de errores (`ApiError`), paginación/ordenación, logging con `traceId`.
- [x] **Fase 3 — Patrones de diseño**: motor de descuentos con Strategy, Template Method, Factory y Builder.
- [x] **Fase 4 — Testing**: pirámide de tests — Mockito (unit), `@WebMvcTest`, `@DataJpaTest` + Testcontainers (PostgreSQL real), `@SpringBootTest`.
- [x] **Fase 5 — Librerías Spring**: OpenAPI/Swagger · Actuator + métricas (Prometheus) · Resilience4j · Cache (Caffeine) · Security + JWT.
- [x] **Fase 6 — Async / eventos**: `@Async` con pool propio, eventos de aplicación (`ApplicationEventPublisher`) y `@TransactionalEventListener` (AFTER_COMMIT) para volcar al data lake en segundo plano.
- [x] **Fase 7 — Frontend Angular**: SPA Angular 20 (standalone) con login JWT (interceptor + guard), listado/CRUD de productos y presupuestos. Ver [`frontend/`](frontend/).
- [x] **Fase 8 — Calidad / entrega**: Dockerfiles multi-stage (backend y frontend), `docker-compose.yml` (PostgreSQL + backend + frontend/nginx) y CI en GitHub Actions.

## Cómo ejecutar el backend

```bash
cd backend
mvn spring-boot:run
```

Comprobaciones rápidas:

- API ping: http://localhost:8080/api/ping
- Health: http://localhost:8080/actuator/health

## Cómo ejecutar el frontend

```bash
cd frontend
npm install        # instala dependencias (requiere red)
npm start          # ng serve con proxy a :8080  ->  http://localhost:4200
```

Necesita el backend en marcha. Login de ejemplo: `admin / password` (ADMIN) o `user / password` (USER).
Más detalles en [`frontend/README.md`](frontend/README.md).

## Cómo ejecutar todo con Docker

Levanta el stack completo (PostgreSQL + backend + frontend) con un solo comando:

```bash
docker compose up --build
```

- Frontend (nginx): http://localhost:8081
- Backend (API + Swagger): http://localhost:8080/swagger-ui.html
- PostgreSQL: `localhost:5432` (db `shopdb`, usuario `shop`)

El backend arranca con el perfil `prod` (PostgreSQL); el frontend se sirve con nginx,
que hace de proxy de `/api` al backend (sin CORS). Copia `.env.example` a `.env` para
personalizar credenciales y, sobre todo, `JWT_SECRET`. Para parar: `docker compose down`
(añade `-v` para borrar también el volumen de datos).

## Integración continua (CI)

El workflow [`.github/workflows/ci.yml`](.github/workflows/ci.yml) se ejecuta en cada push/PR a `main`:

- **backend**: `mvn verify` (unit + integración con Testcontainers).
- **frontend**: `npm ci` + `npm run build`.
- **docker**: construye ambas imágenes (sin publicarlas) tras pasar los anteriores.

## Cómo ejecutar los tests

```bash
cd backend
mvn test     # tests unitarios (*Test, Surefire)
mvn verify   # + tests de integración (*IT, Failsafe): CRUD end-to-end
```

## Endpoints principales

- `GET /api/products?page=0&size=20&sort=name&direction=asc` — listado **paginado** (`PageResult`)
- `POST /api/products`, `GET/PUT/DELETE /api/products/{id}` — CRUD de productos
- `POST /api/products/{id}/quote` — **presupuesto** con motor de descuentos (tier, cantidad, cupón)
- `GET /api/exchange-rates/{currency}` — proveedor externo protegido con **Resilience4j** (retry + circuit breaker + fallback)
- `POST /api/auth/login` — login que devuelve un **JWT** (usuarios demo: `user/password`, `admin/password`)
- La API exige **JWT** (`Authorization: Bearer ...`); escritura de productos solo para `ROLE_ADMIN`
- Errores con formato estándar `ApiError` (incluye `traceId` y `fieldErrors` en validación)
- Cabecera `X-Request-Id` (correlation id) en cada respuesta
- Consola H2: http://localhost:8080/h2-console (JDBC `jdbc:h2:mem:shopdb`, user `sa`)
- **Swagger UI**: http://localhost:8080/swagger-ui.html — spec OpenAPI en `/v3/api-docs`

## Probar con Postman

Importa `postman/spring-ecommerce-lab.postman_collection.json`. Ejecuta primero **`Auth > Login (admin)`**
para obtener el JWT (se guarda en `{{token}}` y se envía como Bearer en el resto de peticiones). Incluye
Health/Ping, CRUD de productos (guarda el `id` en `{{productId}}`) y casos de error (400/404).
Variable `{{baseUrl}}` por defecto `http://localhost:8080`. Puedes lanzarla entera con el
Collection Runner.

> Al crear/actualizar un producto, el adaptador de salida `LakeProductEventPublisher` emite un
> evento (simulado por log `[LAKE] ...`) — ejemplo de adaptador hexagonal hacia un *data lake*.
