# 🔍 Search Service - Microservice de Recherche

> Service de recherche de la plateforme **Travel**. Fournit une recherche full-text ultra-rapide via Elasticsearch avec un pattern **CQRS** — les données sont indexées via des événements RabbitMQ.

---

## 📋 Table des Matières

- [Vue d'Ensemble](#-vue-densemble)
- [Stack Technique](#-stack-technique)
- [Architecture & Design](#-architecture--design)
- [Flux Métier (CQRS Pattern)](#-flux-métier-cqrs-pattern)
- [Modèle de Données (TravelDocument)](#-modèle-de-données-traveldocument)
- [Communication Inter-Services (RabbitMQ)](#-communication-inter-services-rabbitmq)
- [Endpoints REST (Recherche)](#-endpoints-rest-recherche)
- [Fonctionnalités de Recherche](#-fonctionnalités-de-recherche)
- [Gestion des Exceptions](#-gestion-des-exceptions)
- [Installation & Démarrage](#-installation--démarrage)
- [Configuration](#-configuration)
- [Tests](#-tests)

---

## 📖 Vue d'Ensemble

Le `search-service` implémente le **côté lecture du pattern CQRS** (Command Query Responsibility Segregation) :

- **Écriture** : Le service n'écrit **jamais** en base SQL. Il écoute des événements RabbitMQ pour indexer les données dans Elasticsearch.
- **Lecture** : Il expose des APIs de recherche ultra-rapides avec fuzzy matching, filtres par prix/date, et pagination.

### Rôle dans l'architecture
```
┌──────────────┐  TravelCreatedEvent    ┌──────────────────┐
│ travel-      │ ─────────────────────► │                  │
│ service      │  TravelUpdatedEvent    │  search-         │ ──── GET /api/v1/search
│ (writes)     │ ─────────────────────► │  service         │       Fuzzy search
│              │  TravelDeletedEvent    │  (Elasticsearch) │       Price filter
│              │ ─────────────────────► │                  │       Date filter
└──────────────┘       RabbitMQ         └──────────────────┘       Pagination
```

### Caractéristiques Clés

| Feature | Description |
|---------|-------------|
| **Fuzzy Search** | Gestion des fautes de frappe via `fuzziness: AUTO` |
| **Multi-field** | Recherche sur titre, description, destinations, activités |
| **Weighted** | Le titre a un poids 3x, la description 2x |
| **Filtres** | Prix min/max, date de départ minimum |
| **CQRS** | Écriture via events, lecture via REST |
| **Temps réel** | Indexation quasi-instantanée via RabbitMQ |

---

## 🛠 Stack Technique

| Composant | Technologie | Version | Description |
|-----------|-------------|---------|-------------|
| **Langage** | Java | **21 (LTS)** | Records, Pattern Matching. |
| **Framework** | Spring Boot | **4.0.2** | Core Application Framework. |
| **Build Tool** | Maven | 3.9+ | Gestion des dépendances. |
| **Search Engine** | Elasticsearch | 8.x | Moteur de recherche full-text. |
| **Data Access** | Spring Data Elasticsearch | Latest | Mapping Document ↔ Index. |
| **Messaging** | RabbitMQ | 3.12+ | Event-driven indexing (consumer). |
| **Security** | Spring Security | 7+ | OAuth2 Resource Server (JWT). |
| **Mapping** | MapStruct | 1.6.3 | Event → Document → Response. |
| **Secrets** | HashiCorp Vault | Latest | Gestion sécurisée des secrets. |
| **Docs** | SpringDoc OpenAPI | 2.8.4 | Swagger UI automatique. |
| **Monitoring** | Micrometer + Prometheus | Latest | Métriques applicatives. |

---

## 🏛 Architecture & Design

### Structure des Dossiers

```
src/main/java/sn/travel/search_service/
├── config/                                  # Configuration Beans
│   ├── SecurityConfig.java                  # JWT-based security (OAuth2 Resource Server)
│   ├── RabbitMQConfig.java                  # Exchanges, queues, bindings
│   ├── OpenApiConfig.java                   # Swagger documentation
│   └── TravelEventListener.java             # @RabbitListener — event consumer
├── data/                                    # Couche Données
│   ├── documents/
│   │   └── TravelDocument.java              # @Document Elasticsearch (index: travels)
│   ├── records/
│   │   ├── TravelCreatedEvent.java          # Event IN (travel publié)
│   │   ├── TravelUpdatedEvent.java          # Event IN (travel mis à jour)
│   │   └── TravelDeletedEvent.java          # Event IN (travel supprimé/annulé)
│   └── repositories/
│       └── TravelSearchRepository.java      # ElasticsearchRepository
├── exceptions/                              # Gestion centralisée des erreurs
│   ├── SearchServiceException.java          # Base abstraite
│   ├── DocumentNotFoundException.java       # SEARCH_001
│   ├── SearchQueryException.java            # SEARCH_002
│   └── GlobalExceptionHandler.java          # @ControllerAdvice (RFC 7807)
├── services/                                # Logique Métier
│   ├── SearchService.java                   # Interface
│   └── implementation/
│       └── SearchServiceImpl.java           # Fuzzy search + filters
├── web/                                     # Couche REST
│   ├── controllers/
│   │   ├── SearchController.java            # Interface Swagger-annotée
│   │   └── implementation/
│   │       └── SearchControllerImpl.java    # Endpoints REST
│   ├── dto/
│   │   └── responses/
│   │       ├── SearchResultResponse.java    # Résultat de recherche
│   │       ├── PageResponse.java            # Wrapper paginé
│   │       └── MessageResponse.java         # Messages simples
│   └── mappers/
│       └── SearchMapper.java                # MapStruct (Event → Doc → Response)
└── SearchServiceApplication.java            # Entrypoint
```

### Principes Clés

1. **CQRS** : Séparation stricte lecture (REST) / écriture (RabbitMQ). Aucun endpoint de création.
2. **Event-Driven** : Le `TravelEventListener` consomme les événements et délègue au `SearchService`.
3. **Interface-based** : Services et Controllers définis par interface pour faciliter les tests/mocks.
4. **RFC 7807** : Toutes les erreurs sont formatées en `ProblemDetail`.

---

## 🔄 Flux Métier (CQRS Pattern)

### Flux d'Indexation

```
Manager publie un voyage (travel-service)
        │
        ▼
TravelCreatedEvent → RabbitMQ (travel.exchange / travel.created)
        │
        ▼
TravelEventListener (search-service) → SearchService.indexTravel()
        │
        ▼
TravelDocument sauvegardé dans Elasticsearch (index: "travels")
        │
        ▼
Disponible pour recherche via GET /api/v1/search
```

### Événements Consommés

| Événement | Routing Key | Action |
|-----------|-------------|--------|
| `TravelCreatedEvent` | `travel.created` | Index new document |
| `TravelUpdatedEvent` | `travel.updated` | Re-index (upsert) |
| `TravelDeletedEvent` | `travel.deleted` | Remove from index |

### Quand les événements sont-ils publiés ?

| Action (travel-service) | Événement publié |
|--------------------------|------------------|
| `publishTravel()` | `TravelCreatedEvent` |
| `updateTravel()` (si PUBLISHED) | `TravelUpdatedEvent` |
| `deleteTravel()` | `TravelDeletedEvent` |
| `cancelTravel()` | `TravelDeletedEvent` |

---

## 📄 Modèle de Données (TravelDocument)

```java
@Document(indexName = "travels")
public class TravelDocument {
    String id;            // UUID du voyage (clé primaire ES)
    String managerId;     // UUID du manager
    String title;         // Text (analysé, pondéré x3)
    String description;   // Text (analysé, pondéré x2)
    LocalDate startDate;
    LocalDate endDate;
    Integer duration;
    Double price;
    Integer maxCapacity;
    Integer currentBookings;
    String status;        // Keyword (PUBLISHED, CANCELLED...)
    String accommodationType;
    String accommodationName;
    String transportationType;
    String transportationDetails;
    List<DestinationDoc> destinations;  // Object (name, country, city, description)
    List<ActivityDoc> activities;       // Object (name, description, location)
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
```

---

## 📡 Communication Inter-Services (RabbitMQ)

### Configuration

| Ressource | Nom | Description |
|-----------|-----|-------------|
| **Exchange** | `travel.exchange` | TopicExchange (partagé avec travel-service) |
| **Queue** | `search.travel.created.queue` | Indexation des nouveaux voyages |
| **Queue** | `search.travel.updated.queue` | Mise à jour des voyages modifiés |
| **Queue** | `search.travel.deleted.queue` | Suppression des voyages annulés/supprimés |

---

## 🌐 Endpoints REST (Recherche)

> **Tous les endpoints de recherche sont publics** (pas d'authentification requise).

### `GET /api/v1/search`

Recherche full-text avec filtres optionnels.

| Paramètre | Type | Requis | Description |
|-----------|------|--------|-------------|
| `q` | String | Non | Texte de recherche (fuzzy matching) |
| `minPrice` | Double | Non | Prix minimum (inclusif) |
| `maxPrice` | Double | Non | Prix maximum (inclusif) |
| `fromDate` | LocalDate | Non | Date de départ minimum (yyyy-MM-dd) |
| `page` | int | Non | Numéro de page (défaut: 0) |
| `size` | int | Non | Taille de page (défaut: 20) |

**Exemples :**

```bash
# Recherche simple
GET /api/v1/search?q=Saint-Louis

# Avec filtres
GET /api/v1/search?q=plage&minPrice=200000&maxPrice=500000&fromDate=2026-04-01

# Recherche avec typo (fuzzy matching !)
GET /api/v1/search?q=Sain-Loui

# Tous les voyages publiés (pas de query)
GET /api/v1/search?page=0&size=10
```

### `GET /api/v1/search/{travelId}`

Récupère un document spécifique par son ID.

---

## 🔍 Fonctionnalités de Recherche

### Fuzzy Search (Tolérance aux fautes de frappe)

Le service utilise `fuzziness: AUTO` d'Elasticsearch, qui ajuste automatiquement la distance d'édition :
- Mots de 1-2 caractères : correspondance exacte
- Mots de 3-5 caractères : 1 édition autorisée
- Mots de 6+ caractères : 2 éditions autorisées

**Exemples :**
| Requête | Résultat attendu |
|---------|------------------|
| `Saint-Louis` | ✅ Match exact |
| `Sain-Loui` | ✅ Fuzzy match |
| `aventurr` | ✅ → Aventure |
| `Casamanc` | ✅ → Casamance |

### Pondération des Champs (Boosting)

| Champ | Poids | Raison |
|-------|-------|--------|
| `title` | x3 | Le titre est le plus pertinent |
| `description` | x2 | La description ajoute du contexte |
| `destinations.name` | x1 | Recherche par destination |
| `destinations.country` | x1 | Recherche par pays |
| `activities.name` | x1 | Recherche par activité |

### Filtres Elasticsearch

| Filtre | Type | Champ ES |
|--------|------|----------|
| Prix minimum | `range.gte` | `price` |
| Prix maximum | `range.lte` | `price` |
| Date départ | `range.gte` | `startDate` |
| Statut publié | `term` | `status = PUBLISHED` |

---

## ⚠️ Gestion des Exceptions

| Code | Exception | HTTP | Description |
|------|-----------|------|-------------|
| `SEARCH_001` | `DocumentNotFoundException` | 404 | Document non trouvé dans l'index |
| `SEARCH_002` | `SearchQueryException` | 500 | Erreur d'exécution de la requête ES |
| `SEARCH_400` | Validation Error | 400 | Erreur de validation des paramètres |
| `SEARCH_403` | Access Denied | 403 | Accès non autorisé |
| `SEARCH_500` | Internal Error | 500 | Erreur générique |

---

## 🚀 Installation & Démarrage

### Prérequis

- Java 21+
- Maven 3.9+
- Elasticsearch 8.x accessible sur `localhost:9200`
- RabbitMQ accessible sur `localhost:5672`
- HashiCorp Vault accessible sur `localhost:8200` (optionnel)

### Port-forwards (Kubernetes)

```bash
kubectl port-forward svc/elasticsearch-master 9200:9200 -n travel
kubectl port-forward svc/rabbitmq 5672:5672 15672:15672 -n travel
kubectl port-forward svc/vault 8200:8200 -n travel
```

### Démarrage

```bash
cd search-service
./mvnw spring-boot:run
```

Le service démarre sur le port **8085**.

### Vérification

```bash
# Health check
curl http://localhost:8085/actuator/health

# Swagger UI
open http://localhost:8085/swagger-ui.html
```

---

## ⚙️ Configuration

### application.properties

| Propriété | Description | Défaut |
|-----------|-------------|--------|
| `server.port` | Port du service | `8085` |
| `spring.elasticsearch.uris` | URL Elasticsearch | `http://localhost:9200` |
| `spring.elasticsearch.username` | Username ES | `elastic` |
| `spring.elasticsearch.password` | Password ES | Via Vault |
| `spring.rabbitmq.host` | Hôte RabbitMQ | `localhost` |
| `jwt.secret` | Clé JWT partagée | Via Vault |

---

## 🧪 Tests

### Test Manuel (HTTP)

Utilisez le fichier `search-service-test.http` pour tester le flux complet :

1. **Authentification** → Login manager via auth-service
2. **Création** → Créer et publier un voyage via travel-service
3. **Recherche** → Vérifier l'indexation via search-service
4. **Modification** → Mettre à jour et chercher de nouveau
5. **Suppression** → Annuler et vérifier la suppression de l'index

### Test Elasticsearch Direct

```bash
# Vérifier l'index
curl -u elastic:lbB07FlWk4MDeGYx http://localhost:9200/travels/_search?pretty

# Compter les documents
curl -u elastic:lbB07FlWk4MDeGYx http://localhost:9200/travels/_count

# Supprimer l'index (reset)
curl -u elastic:lbB07FlWk4MDeGYx -X DELETE http://localhost:9200/travels
```
