# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Travel Management System (TMS) — a microservices-based travel platform written in French business context (Senegal-focused). The project uses event-driven architecture with RabbitMQ for inter-service communication. External dependencies run locally via Docker Compose.

## Tech Stack

- **Java 21** with Spring Boot **4.0.2**, Spring Cloud **2025.1.0**
- **Maven** (no parent POM — each service has its own independent `pom.xml`)
- **PostgreSQL** (relational), **Elasticsearch** (search), **Neo4j** (recommendations)
- **RabbitMQ** for async event-driven messaging between services
- **HashiCorp Vault** for secrets management (Spring Cloud Vault)
- **Flyway** for database migrations
- **Lombok** (mandatory), **MapStruct** (DTO mappers in search/notification services)
- **SpringDoc OpenAPI** for Swagger documentation on each service

## Build & Run Commands

Each service is built independently from its own directory:

```bash
# Build a single service (skip tests)
cd <service-name> && ./mvnw clean install -DskipTests

# Run a single service
cd <service-name> && ./mvnw spring-boot:run

# Run tests for a service
cd <service-name> && ./mvnw test

# Start all external dependencies (PostgreSQL, RabbitMQ, Elasticsearch, Vault, MailDev)
docker compose up -d
```

There is no root `pom.xml` — you cannot build all services at once from the root.

## Services & Ports

| Service | Port | Database | Description |
|---------|------|----------|-------------|
| **api-gateway** | 8080 | — | Spring Cloud Gateway (WebFlux/reactive), routes requests, JWT validation, Resilience4j circuit breakers |
| **auth-service** | 8081 | `travel_auth_db` | Identity & access management, JWT issuance (JJWT), OAuth2 Resource Server |
| **travel-service** | 8082 | `travel_db` | Core business: travels CRUD, subscriptions, publishes RabbitMQ events |
| **payment-service** | 8083 | `travel_payment_db` | Payment processing (simulated), consumes subscription events, publishes payment events |
| **notification-service** | 8084 | `travel_notification_db` | Event-driven email notifications via RabbitMQ, Thymeleaf HTML templates, SMTP to MailDev |
| **search-service** | 8085 | Elasticsearch | CQRS read-side, consumes travel events from RabbitMQ, indexes into Elasticsearch |
| **rec-service** | — | Neo4j | Recommendation engine (early stage) |

## Infrastructure (Docker Compose)

All external dependencies run via `docker-compose.yml` at the project root:

```bash
docker compose up -d      # Start all
docker compose down        # Stop all
docker compose down -v     # Stop and delete volumes (full reset)
```

| Service | Port(s) | Container |
|---------|---------|-----------|
| PostgreSQL 16 | 5432 | travel-postgres |
| Vault 1.17 (dev mode) | 8200 | travel-vault |
| RabbitMQ 3.13 | 5672, 15672 (management UI) | travel-rabbitmq |
| MailDev 2.1 | 1025 (SMTP), 1080 (web UI) | travel-maildev |
| Elasticsearch 8.17 | 9200 | travel-elasticsearch |

Vault secrets are auto-injected by `vault-init` container on first start (`infra/vault/init-secrets.sh`).
PostgreSQL databases are auto-created via `infra/postgres/init-databases.sql`.

## Architecture Patterns

### Package Structure (per service)
```
sn.travel.<service_name>/
├── config/          # Security, RabbitMQ, JWT, Elasticsearch configs
├── data/
│   ├── entities/    # JPA entities
│   ├── enums/
│   ├── records/     # Java records for events/DTOs
│   └── repositories/
├── exceptions/      # Custom exceptions + @ControllerAdvice (RFC 7807 ProblemDetail)
├── services/
│   ├── <Interface>.java
│   └── implementation/
├── web/
│   ├── controllers/
│   │   ├── <Interface>.java  # Swagger-annotated interface
│   │   └── implementation/
│   ├── dto/
│   │   ├── requests/
│   │   └── responses/
│   └── mappers/     # MapStruct mappers
```

### Key Design Rules
- **Never expose JPA entities** to controllers — always use DTOs with MapStruct mappers
- **Interface-based services and controllers**: define interface with OpenAPI annotations, implement in `implementation/` subdirectory
- **Exception handling**: centralized via `@ControllerAdvice` returning RFC 7807 `ProblemDetail` with custom error codes
- **Validation**: `jakarta.validation.constraints` on request DTOs
- **Security**: JWT with shared HMAC secret across services (via Vault), roles extracted from JWT claims
- **JPA**: `ddl-auto=validate` — schema managed exclusively by Flyway migrations

### Event-Driven Flows (RabbitMQ)
- **travel-service** publishes `SubscriptionCreatedEvent` → consumed by payment-service and notification-service
- **payment-service** publishes `PaymentCompletedEvent` → consumed by travel-service (subscription status update) and notification-service (email)
- Uses Topic Exchanges with dedicated queues per consumer service

### API Gateway Routing
- `/api/v1/auth/**` → auth-service (8081)
- `/api/v1/travels/**` → travel-service (8082)
- `/api/v1/payments/**` → payment-service (8083)

## Code Standards

- Use Java 21 features: Records, Pattern Matching, Sequenced Collections
- Naming: explicit business names (e.g., `cancelTravelSubscription` not `deleteSub`)
- SOLID, DRY, KISS principles
- Custom exceptions must follow the same pattern across all services (typed error codes like `TRAVEL_001`, `PAYMENT_001`, etc.)
- All code, comments, and business logic documentation is in **French** (the project language)
- Tests: JUnit 5 + Mockito, Testcontainers for integration tests

## Vault Configuration

All services use Spring Cloud Vault with KV backend `travel-backend` (except api-gateway which uses `kv`). Secrets override properties like database passwords, JWT secret, and RabbitMQ password. Vault token defaults to `root` for local dev via `VAULT_TOKEN` env var.

## Flyway Workaround

If Flyway migrations fail, schemas can be applied manually:
```bash
PGPASSWORD=<pw> psql -h localhost -p 5432 -U postgres -d <db_name> -f <service>/src/main/resources/db/migration/V1__*.sql
```
