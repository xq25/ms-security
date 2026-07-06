# ─────────────────────────────────────────────
# Stage 1: build
# ─────────────────────────────────────────────
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Copiamos primero solo los archivos de dependencias para aprovechar la caché de Docker.
# Si el código fuente cambia pero pom.xml no, esta capa no se reconstruye.
COPY pom.xml ./
COPY mvnw ./
COPY .mvn .mvn
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Ahora sí copiamos el código fuente y compilamos.
# NOTA: serviceAccountKey.json está en src/main/resources/ y se empaqueta dentro
# del JAR. FirebaseConfig.java lo lee vía ClassPathResource, así que no se necesita
# ningún volumen externo para ese archivo.
COPY src ./src
RUN ./mvnw package -DskipTests -B

# ─────────────────────────────────────────────
# Stage 2: runtime  (imagen final, sin JDK)
# ─────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app

# wget lo usa el healthcheck del compose para hacer GET al /actuator/health (o similar)
RUN apk add --no-cache wget

# Usuario sin privilegios — buena práctica de seguridad
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

COPY --from=builder /app/target/ms-security-0.0.1-SNAPSHOT.jar app.jar

# Creamos el directorio de fotos con los permisos correctos antes de cambiar a appuser.
# Este directorio se monta como volumen en docker-compose para persistir las fotos.
RUN mkdir -p uploads/photos && chown -R appuser:appgroup uploads

RUN chown appuser:appgroup app.jar
USER appuser

EXPOSE 8080

# Volumen para persistir las fotos de perfil entre reinicios del contenedor.
# En docker-compose se mapea a un named volume: ms_security_photos:/app/uploads/photos
VOLUME ["/app/uploads/photos"]

# Las siguientes propiedades se inyectan como variables de entorno en docker-compose
# (Spring Boot las resuelve automáticamente con el formato PROPIEDAD_CON_PUNTOS -> VAR_CON_GUIONES):
#
#   NOTIFICATIONS_URL          → notifications.url
#   CLASIFICATOR_URL           → clasificator.url
#   APP_BASE_URL               → app.base.url
#   APP_CORS_ALLOWED_ORIGINS   → app.cors.allowed-origins
#   SPRING_MONGODB_URI         → spring.mongodb.uri
#
# Así el application.properties de desarrollo NO se modifica.
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Dspring.profiles.active=docker", \
  "-jar", "app.jar"]
