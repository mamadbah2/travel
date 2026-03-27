# Dockerfile partagé — multi-stage build pour tous les microservices
# Usage: docker build -f ../Dockerfile . (depuis le répertoire d'un service)

# ==================== Stage 1 : Build ====================
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copier Maven wrapper et pom.xml d'abord (cache des dépendances)
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Copier le code source et builder
COPY src src

RUN ./mvnw package -DskipTests -B

# ==================== Stage 2 : Runtime ====================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

HEALTHCHECK --interval=15s --timeout=5s --start-period=30s --retries=5 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:${SERVER_PORT:-8080}/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
