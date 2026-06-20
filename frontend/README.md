# Frontend — spring-ecommerce-lab

SPA en **Angular 20** (componentes *standalone*) que consume la API REST del backend:
login con **JWT**, listado y **CRUD** de productos y cálculo de **presupuestos**.

## Requisitos

- Node.js 20.19+ / 22.12+ / 24+
- npm 10+
- El backend corriendo en `http://localhost:8080` (ver `../backend`).

## Puesta en marcha

```bash
cd frontend
npm install        # instala dependencias (requiere red; puede tardar)
npm start          # ng serve con proxy a :8080  ->  http://localhost:4200
```

El script `start` usa `proxy.conf.json` para redirigir `/api/*` al backend
(`http://localhost:8080`), evitando problemas de CORS en desarrollo.

Usuarios de ejemplo (definidos en el backend):

- `admin / password` — rol ADMIN (puede crear/editar/borrar productos)
- `user / password` — rol USER (solo lectura y presupuestos)

## Build de producción

```bash
npm run build      # genera dist/frontend
```

## Estructura

```
src/app/
├── core/
│   ├── models.ts            # tipos que reflejan los DTOs del backend
│   ├── auth.service.ts      # login, almacenamiento del JWT, roles (signals)
│   ├── product.service.ts   # llamadas a /api/products y /quote
│   ├── auth.interceptor.ts  # añade Authorization: Bearer y maneja 401
│   └── auth.guard.ts        # protege rutas que requieren sesión
├── features/
│   ├── login/               # pantalla de acceso
│   └── products/            # listado, formulario crear/editar y presupuesto
├── app.component.ts         # layout + barra de navegación
├── app.config.ts            # providers (router, httpClient + interceptor)
└── app.routes.ts            # rutas con lazy loading y guard
```

## Notas de arquitectura

- **Autenticación**: el JWT se guarda en `localStorage`; `AuthService` lo decodifica
  para exponer `username`, `roles` e `isAdmin` como *signals*.
- **Autorización en UI**: los botones de escritura solo se muestran a `ROLE_ADMIN`
  (el backend vuelve a comprobarlo: la UI es comodidad, no seguridad).
- **Manejo de errores**: el interceptor cierra sesión y redirige al login ante un 401;
  los formularios muestran los `fieldErrors` que devuelve `ApiError`.
