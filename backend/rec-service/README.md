# rec-service

Service de recommandations pour la plateforme Travel. Utilise Neo4j (base graphe) pour generer des recommandations personnalisees, gerer les feedbacks (notations) et les signalements d'utilisateurs.

## Port

`8086`

## Base de donnees

**Neo4j 5 Community** — graphe de relations entre voyageurs, voyages, destinations et activites.

- Browser UI : http://localhost:7474
- Bolt : `bolt://localhost:7687`
- Credentials : `neo4j / neo4jpassword`

## Endpoints

### Recommandations (`/api/v1/recommendations`)

| Methode | Endpoint | Auth | Description |
|---------|----------|------|-------------|
| GET | `/personalized?limit=10` | Authentifie | Recommandations personnalisees |
| GET | `/popular?limit=10` | Public | Voyages populaires |
| GET | `/similar/{travelId}?limit=10` | Public | Voyages similaires |

### Feedbacks (`/api/v1/feedbacks`)

| Methode | Endpoint | Auth | Description |
|---------|----------|------|-------------|
| POST | `/` | TRAVELER | Creer un feedback |
| GET | `/travel/{travelId}` | Public | Feedbacks d'un voyage |
| GET | `/me` | Authentifie | Mes feedbacks |
| PUT | `/{feedbackId}` | TRAVELER | Modifier un feedback |
| DELETE | `/{feedbackId}` | TRAVELER/ADMIN | Supprimer un feedback |

### Signalements (`/api/v1/reports`)

| Methode | Endpoint | Auth | Description |
|---------|----------|------|-------------|
| POST | `/` | Authentifie | Creer un signalement |
| GET | `/` | ADMIN | Lister les signalements |
| GET | `/{reportId}` | ADMIN | Detail d'un signalement |
| PUT | `/{reportId}/resolve` | ADMIN | Resoudre un signalement |

## Evenements RabbitMQ consommes

| Evenement | Exchange | Queue |
|-----------|----------|-------|
| TravelCreatedEvent | travel.exchange | rec.travel.created.queue |
| TravelUpdatedEvent | travel.exchange | rec.travel.updated.queue |
| TravelDeletedEvent | travel.exchange | rec.travel.deleted.queue |
| SubscriptionCreatedEvent | subscription.exchange | rec.subscription.created.queue |

## Build & Run

```bash
# Demarrer les dependances (incluant Neo4j)
docker compose up -d

# Compiler
./mvnw clean compile

# Lancer
./mvnw spring-boot:run

# Tests
./mvnw test
```

## Swagger UI

http://localhost:8086/swagger-ui.html
