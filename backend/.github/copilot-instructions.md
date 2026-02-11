# Instructions de Développement - Projet Travel

## 1. Contexte Global
Tu es un expert Java Senior travaillant sur le projet "Travel", un système de gestion de voyages en microservices.
- Architecture : Microservices découplés.
- Communication : Event-driven (RabbitMQ) et REST.
- Environnement : Kubernetes (K3s) sur VPS OVH.

## 2. Stack Technique (Strict)
- Langage : Java 21 (Utilise les Records, Pattern Matching, et Sequenced Collections).
- Framework : Spring Boot 4.0.2
- Gestion de dépendances : Maven.
- Sécurité : Spring Security 7+ (OAuth2 Resource Server & Authorization Server). Utilisation de Vault pour les secrets.
- Persistance : Spring Data JPA avec PostgreSQL / Spring Data Neo4j / Spring Data Elasticsearch.
- Outils : Lombok (obligatoire), MapStruct (pour les mappers DTO), Flyway (migrations DB).

## 3. Standards de Code (Expert)
- Architecture des packages : Domain-driven (controller, service, repository, entity, dto, mapper).
- Pattern DTO : Ne jamais exposer les Entités JPA aux Controllers. Utilise systématiquement des DTOs.
- Clean Code : SOLID, DRY, KISS. Noms de variables explicites et métier (ex: `cancelTravelSubscription` au lieu de `deleteSub`).
- Exceptions : Gestion centralisée via `@ControllerAdvice` et `ProblemDetail` (RFC 7807).
- Validation : Utilise `jakarta.validation.constraints` sur les DTOs d'entrée.

## 4. Patterns Microservices
- Sécurité : Extraction des rôles depuis les claims JWT "roles" ou "scope".
- Documentation : Swagger/OpenAPI 3 (SpringDoc) sur chaque service.
- Résilience : Pattern Circuit Breaker (Resilience4j) pour les appels inter-services.
- Logging : Format JSON pour ingestion par la stack ELK.

## 5. Tests
- JUnit 5 et Mockito.
- Stratégie : Testcontainers pour les tests d'intégration (PostgreSQL/RabbitMQ).
- Objectif : Couverture minimale de 80% sur la logique métier.

## 6. Architecture dossier
```
.
├── exceptions
├── config
│   └── SecurityConfig.java
├── data
│   ├── entities
│   └── repositories
|   └── records
├── services
│   ├── implementation
│   ├── SocialService.java
│   ├── UserService.java
│   └── WatchlistService.java (interface)
├── UserServiceApplication.java
└── web
    ├── controllers
    │   ├── implementation
    │   ├── SocialController.java
    ├── dto
    │   ├── requests
    │   └── responses
    └── mappers
```
Au besoin, tu peux creer des dossiers supplémentaires, mais respecte l'architecture globale.

## 7. Diagramme de Classes Simplifie

### 1. Noyau Identité (Service : Auth)

C'est ici que l'on gère l'accès.

* **User**
* `id`: UUID
* `email`: String (Unique)
* `password`: String (Hashé)
* `role`: Enum (ADMIN, MANAGER, TRAVELER)
* `status`: Enum (ACTIVE, BANNED)
* `performanceScore`: Float (Calculé pour les Managers)



---

### 2. Noyau Voyage (Service : Travel)

C'est le cœur du métier. Note que le `Travel` est l'agrégat principal.

* **Travel**
* `id`: UUID
* `managerId`: UUID (Lien vers User)
* `title`: String
* `description`: String
* `startDate` / `endDate`: LocalDate
* `duration`: Integer
* `price`: Double
* `destinations`: List<Destination>
* `activities`: List<Activity>
* `accommodation`: Accommodation
* `transportation`: Transportation


* **Subscription (L'inscription)**
* `id`: UUID
* `travelerId`: UUID
* `travelId`: UUID
* `status`: Enum (PENDING, CONFIRMED, CANCELLED)
* `createdAt`: LocalDateTime (Pour la règle des 3 jours)



---

### 3. Noyau Finance (Service : Payment)

* **Payment**
* `id`: UUID
* `subscriptionId`
Puisque nous sommes dans une architecture microservices, un diagramme de classe unique "monolithique" n'aurait pas de sens techniquement. En revanche, un **diagramme de domaine logique** qui montre comment les entités interagissent entre les services est indispensable.

Voici la structure complète du domaine **Travel**, conçue pour respecter les contraintes de tes deux cahiers des charges (cascades, rôles, recherche et recommandations).

---: UUID
* `amount`: Double
* `currency`: String
* `method`: Enum (STRIPE, PAYPAL, WAVE)
* `transactionId`: String (ID externe)
* `status`: Enum (SUCCESS, FAILED)



---

### 4. Noyau Social & Satisfaction (Service : Rec/Review)

Ces données alimentent Elasticsearch et Neo4j.

* **Feedback**
* `id`: UUID
* `travelerId`: UUID
* `travelId`: UUID
* `rating`: Integer (1-5)
* `comment`: String


* **Report (Signalement)**
* `id`: UUID
* `reporterId`: UUID
* `reportedEntityId`: UUID (Manager ou Traveler)
* `reason`: String
* `status`: Enum (PENDING, RESOLVED)



---

### Relations et Contraintes Critiques

| Relation | Type | Logique Métier |
| --- | --- | --- |
| **User <-> Travel** | 1:N | Un Manager crée plusieurs voyages. Un voyage a un seul Manager. |
| **Travel <-> Subscription** | 1:N | **Cascade Delete** : Si un voyage est supprimé, toutes les inscriptions sont annulées. |
| **Traveler <-> Subscription** | 1:N | Un voyageur peut s'inscrire à plusieurs voyages. |
| **Subscription <-> Payment** | 1:1 | Une inscription génère exactement une transaction financière. |

---

## 8. Gestion des exceptions
- Crée des exceptions personnalisées pour les erreurs métier (e.g., `TravelNotFoundException`, `UnauthorizedAccessException`).
- Utilise un gestionnaire global d'exceptions avec `@ControllerAdvice` pour formater les réponses d'erreur de manière cohérente.
- Logue toutes les exceptions critiques avec des niveaux appropriés (ERROR, WARN).
- Toutes les exceptions doivent inclure des messages clairs et des codes d'erreur spécifiques pour faciliter le débogage.
- Toutes les exceptions doivent avoir un pattern identique.
