# ---------- Etapa 1: build de la SPA con Node ----------
FROM node:22-alpine AS build
WORKDIR /app

# Cache de dependencias: copiamos package*.json primero.
COPY package*.json ./
RUN npm ci

# Codigo y build de produccion (genera dist/frontend/browser).
COPY . .
RUN npm run build

# ---------- Etapa 2: servir estaticos con nginx ----------
FROM nginx:1.27-alpine AS runtime

# Config propia: SPA fallback + proxy de /api al backend.
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copiamos el resultado del build (Angular 20 deja los estaticos en dist/<proyecto>/browser).
COPY --from=build /app/dist/frontend/browser /usr/share/nginx/html

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
