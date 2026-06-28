# ---------- Etapa 1: build con Maven ----------
# Imagen con JDK 21 + Maven para compilar y empaquetar el jar.
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

# Copiamos primero el pom para aprovechar la cache de capas de Docker:
# si el pom no cambia, las dependencias descargadas se reutilizan entre builds.
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

# Ahora el codigo fuente y el empaquetado (sin tests: se ejecutan en CI).
COPY src ./src
RUN mvn -B -q clean package -DskipTests

# ---------- Etapa 2: runtime ligero (solo JRE) ----------
# Imagen mucho mas pequena: solo necesita el JRE para ejecutar el jar.
FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app

# curl para el healthcheck; usuario sin privilegios (no correr como root).
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/* \
    && groupadd --system spring && useradd --system --gid spring spring

# Copiamos unicamente el jar resultante de la etapa de build.
COPY --from=build /workspace/target/*.jar app.jar

USER spring
EXPOSE 8080

# Perfil 'prod' por defecto (PostgreSQL); se puede sobreescribir por entorno.
ENV SPRING_PROFILES_ACTIVE=prod

# Healthcheck contra Actuator: marca el contenedor 'healthy' cuando la app responde.
HEALTHCHECK --interval=15s --timeout=5s --start-period=40s --retries=5 \
  CMD curl -fsS http://localhost:8080/actuator/health | grep -q '"status":"UP"' || exit 1

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
