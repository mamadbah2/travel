# 💳 Payment Service - Microservice de Paiement

> Service de traitement des paiements de la plateforme **Travel**. Écoute les événements de souscription, simule le traitement bancaire, et notifie le résultat au `travel-service` via RabbitMQ.

---

## 📋 Table des Matières

- [Vue d'Ensemble](#-vue-densemble)
- [Stack Technique](#-stack-technique)
- [Architecture & Design](#-architecture--design)
- [Flux Métier (Business Flow)](#-flux-métier-business-flow)
- [Modèle de Données](#-modèle-de-données)
- [Communication Inter-Services (RabbitMQ)](#-communication-inter-services-rabbitmq)
- [Endpoints REST (Consultation)](#-endpoints-rest-consultation)
- [Gestion des Exceptions](#-gestion-des-exceptions)
- [Installation & Démarrage](#-installation--démarrage)
- [Configuration](#-configuration)
- [Migration Base de Données](#-migration-base-de-données)
- [Tests](#-tests)
- [Roadmap / Évolutions](#-roadmap--évolutions)

---

## 📖 Vue d'Ensemble

Le `payment-service` est un microservice **event-driven**. Il ne possède **aucun endpoint de création de paiement** — les paiements sont déclenchés automatiquement par des messages RabbitMQ provenant du `travel-service`.

### Rôle dans l'architecture
```
┌──────────────┐       RabbitMQ        ┌──────────────────┐       RabbitMQ        ┌──────────────┐
│              │  SubscriptionCreated   │                  │  PaymentCompleted     │              │
│ travel-      │ ────────────────────►  │  payment-        │ ────────────────────► │ travel-      │
│ service      │                        │  service         │                       │ service      │
│              │                        │  (simulation)    │                       │              │
└──────────────┘                        └──────────────────┘                       └──────────────┘
```

**Mode actuel** : Simulation (pas d'intégration Stripe/PayPal). Un `Thread.sleep(2000)` mime la latence bancaire.

---

## 🛠 Stack Technique

| Composant | Technologie | Version | Description |
|-----------|-------------|---------|-------------|
| **Langage** | Java | **21 (LTS)** | Records, Pattern Matching. |
| **Framework** | Spring Boot | **4.0.2** | Core Application Framework. |
| **Build Tool** | Maven | 3.9+ | Gestion des dépendances. |
| **Database** | PostgreSQL | 16+ | Persistance des transactions. |
| **Migration** | Flyway | Latest | Versionning du schéma. |
| **Messaging** | RabbitMQ | 3.12+ | Event-driven (écoute + publication). |
| **Mapping** | MapStruct | 1.6.3 | DTO <-> Entity haute performance. |
| **Secrets** | HashiCorp Vault | Latest | Gestion sécurisée des secrets. |
| **Docs** | SpringDoc OpenAPI | 2.x | Swagger UI automatique. |
| **Monitoring** | Micrometer + Prometheus | Latest | Métriques applicatives. |

---

## 🏛 Architecture & Design

### Structure des Dossiers

```
src/main/java/sn/travel/payment_service/
├── config/                            # Configuration Beans
│   ├── RabbitMQConfig.java            # Exchanges, queues, bindings, JSON converter
│   └── PaymentEventListener.java      # @RabbitListener — consomme les événements
├── data/                              # Couche Persistance
│   ├── entities/
│   │   └── Payment.java               # Entité JPA principale
│   ├── enums/
│   │   ├── PaymentStatus.java         # PENDING / SUCCESS / FAILED
│   │   └── PaymentMethod.java         # STRIPE / PAYPAL / WAVE / SIMULATED
│   ├── records/
│   │   ├── SubscriptionCreatedEvent.java  # Event IN (depuis travel-service)
│   │   └── PaymentCompletedEvent.java     # Event OUT (vers travel-service)
│   └── repositories/
│       └── PaymentRepository.java     # Spring Data JPA
├── exceptions/                        # Gestion centralisée des erreurs
│   ├── PaymentServiceException.java   # Exception de base abstraite
│   ├── PaymentNotFoundException.java  # PAYMENT_001
│   ├── DuplicatePaymentException.java # PAYMENT_002
│   ├── PaymentProcessingException.java# PAYMENT_003
│   └── GlobalExceptionHandler.java    # @ControllerAdvice (RFC 7807)
├── services/                          # Logique Métier
│   ├── PaymentService.java            # Interface
│   └── implementation/
│       └── PaymentServiceImpl.java    # Implémentation (simulation)
├── web/                               # Couche REST (lecture seule)
│   ├── controllers/
│   │   ├── PaymentController.java     # Interface Swagger-annotée
│   │   └── implementation/
│   │       └── PaymentControllerImpl.java
│   ├── dto/
│   │   └── responses/
│   │       ├── PaymentResponse.java
│   │       ├── PageResponse.java
│   │       └── MessageResponse.java
│   └── mappers/
│       └── PaymentMapper.java         # MapStruct mapper
└── PaymentServiceApplication.java     # Entrypoint
```

### Principes Clés

1. **Event-Driven** : Aucun endpoint de création/mutation. Les paiements sont créés par le `PaymentEventListener` qui écoute RabbitMQ.
2. **DTO Pattern** : L'entité `Payment` n'est jamais exposée. Tout passe par `PaymentResponse`.
3. **Interface-based Service** : `PaymentService` (interface) + `PaymentServiceImpl` (implémentation).
4. **RFC 7807** : Toutes les erreurs retournent un `ProblemDetail` avec `errorCode`, `timestamp`, et `type`.
5. **Idempotent** : Un `SubscriptionCreatedEvent` pour une même `subscriptionId` est rejeté (unicité en BDD).

---

## 🔄 Flux Métier (Business Flow)

Voici le flux complet, étape par étape :

```
 Traveler s'inscrit (travel-service)
         │
         ▼
 ┌───────────────────────────────────┐
 │  1. travel-service publie         │
 │     SubscriptionCreatedEvent      │
 │     sur subscription.exchange     │
 │     (routing: subscription.created)│
 └──────────────┬────────────────────┘
                │
                ▼  (RabbitMQ)
 ┌───────────────────────────────────┐
 │  2. PaymentEventListener reçoit   │
 │     le message sur                │
 │     subscription.created.queue    │
 └──────────────┬────────────────────┘
                │
                ▼
 ┌───────────────────────────────────┐
 │  3. PaymentServiceImpl            │
 │     .processPayment()             │
 │                                   │
 │  a) Vérifie pas de doublon        │
 │  b) Crée Payment (PENDING)       │
 │  c) Thread.sleep(2000) ← simule  │
 │  d) Si amount > 0 → SUCCESS      │
 │     Sinon         → FAILED       │
 │  e) Sauvegarde en BDD            │
 └──────────────┬────────────────────┘
                │
                ▼
 ┌───────────────────────────────────┐
 │  4. Publie PaymentCompletedEvent  │
 │     sur payment.exchange          │
 │     routing: payment.success      │
 │          ou  payment.failed       │
 └──────────────┬────────────────────┘
                │
                ▼  (RabbitMQ)
 ┌───────────────────────────────────┐
 │  5. travel-service consomme       │
 │     le message sur                │
 │     payment.result.queue          │
 │                                   │
 │  → Met à jour Subscription :      │
 │    CONFIRMED ou CANCELLED         │
 └───────────────────────────────────┘
```

### Règle de Validation (Simulation)

| Condition | Résultat | Transaction ID |
|-----------|----------|----------------|
| `amount > 0` | `SUCCESS` | `SIM-XXXXXXXX` (UUID partiel) |
| `amount <= 0` ou `null` | `FAILED` | `null` |

---

## 🗄 Modèle de Données

### Table `payments`

| Colonne | Type | Contrainte | Description |
|---------|------|------------|-------------|
| `id` | UUID | PK | Identifiant unique du paiement. |
| `subscription_id` | UUID | UNIQUE, NOT NULL | Liaison 1:1 avec la souscription. |
| `travel_id` | UUID | NOT NULL | ID du voyage concerné. |
| `traveler_id` | UUID | NOT NULL | ID du voyageur. |
| `travel_title` | VARCHAR(255) | — | Titre du voyage (dénormalisé pour logs). |
| `amount` | DOUBLE | NOT NULL | Montant de la transaction. |
| `currency` | VARCHAR(10) | NOT NULL, DEFAULT `XOF` | Devise (XOF = Franc CFA). |
| `method` | VARCHAR(20) | NOT NULL, CHECK | `STRIPE`, `PAYPAL`, `WAVE`, `SIMULATED`. |
| `transaction_id` | VARCHAR(255) | — | ID de transaction externe (`SIM-...`). |
| `status` | VARCHAR(20) | NOT NULL, CHECK | `PENDING`, `SUCCESS`, `FAILED`. |
| `failure_reason` | VARCHAR(500) | — | Raison de l'échec (si applicable). |
| `created_at` | TIMESTAMP | NOT NULL | Date de création. |
| `updated_at` | TIMESTAMP | — | Dernière mise à jour. |

### Index

- `idx_payment_subscription` : Recherche par souscription.
- `idx_payment_traveler` : Paiements d'un voyageur.
- `idx_payment_travel` : Paiements pour un voyage.
- `idx_payment_status` : Filtrage par statut.
- `idx_payment_created_at` : Tri chronologique.

---

## 🐇 Communication Inter-Services (RabbitMQ)

### Topologie des Exchanges et Queues

| Exchange | Type | Queue | Routing Key | Producteur | Consommateur |
|----------|------|-------|-------------|------------|--------------|
| `subscription.exchange` | Topic | `subscription.created.queue` | `subscription.created` | travel-service | **payment-service** |
| `payment.exchange` | Topic | `payment.result.queue` | `payment.success` | **payment-service** | travel-service |
| `payment.exchange` | Topic | `payment.result.queue` | `payment.failed` | **payment-service** | travel-service |

### Events (Records Java)

**`SubscriptionCreatedEvent`** (entrant) :
```java
record SubscriptionCreatedEvent(
    UUID subscriptionId,
    UUID travelId,
    UUID travelerId,
    String travelTitle,
    Double amount,
    String currency
) {}
```

**`PaymentCompletedEvent`** (sortant) :
```java
record PaymentCompletedEvent(
    UUID subscriptionId,
    UUID travelId,
    UUID travelerId,
    String status,        // "SUCCESS" ou "FAILED"
    String transactionId,
    String failureReason
) {}
```

---

## 📡 Endpoints REST (Consultation)

> **Note** : Ce service n'expose **aucun endpoint de création**. Les paiements sont créés via RabbitMQ.

Base URL : `http://localhost:8083/api/v1/payments`

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/api/v1/payments/{paymentId}` | Récupérer un paiement par ID. |
| `GET` | `/api/v1/payments/subscription/{subscriptionId}` | Récupérer le paiement d'une souscription. |
| `GET` | `/api/v1/payments/traveler/{travelerId}` | Paiements d'un voyageur (paginé). |
| `GET` | `/api/v1/payments/travel/{travelId}` | Paiements pour un voyage (paginé). |
| `GET` | `/api/v1/payments` | Tous les paiements (Admin, paginé). |

### Swagger UI

👉 **http://localhost:8083/swagger-ui.html**

OpenAPI JSON : **http://localhost:8083/api-docs**

---

## ⚠️ Gestion des Exceptions

Toutes les exceptions suivent la RFC 7807 (`ProblemDetail`).

| Code | Exception | HTTP Status | Description |
|------|-----------|-------------|-------------|
| `PAYMENT_001` | `PaymentNotFoundException` | 404 | Paiement introuvable. |
| `PAYMENT_002` | `DuplicatePaymentException` | 409 | Paiement déjà existant pour cette souscription. |
| `PAYMENT_003` | `PaymentProcessingException` | 500 | Erreur lors du traitement du paiement. |
| `PAYMENT_VALIDATION` | Validation Jakarta | 400 | Erreurs de validation des champs. |
| `PAYMENT_INTERNAL` | Exception générique | 500 | Erreur inattendue. |

### Exemple de réponse d'erreur

```json
{
  "type": "https://travel.sn/errors/payment_001",
  "title": "PaymentNotFoundException",
  "status": 404,
  "detail": "Payment not found with identifier: 550e8400-e29b-41d4-a716-446655440000",
  "errorCode": "PAYMENT_001",
  "timestamp": "2026-02-13T15:30:00Z"
}
```

---

## 🚀 Installation & Démarrage

### Prérequis

- **JDK 21** installé.
- **PostgreSQL** accessible (port `5432`).
- **RabbitMQ** accessible (port `5672`).
- **Vault** accessible (port `8200`) — optionnel en dev.

### Étapes

1. **Port-forwards Kubernetes** (si infrastructure K3s) :
   ```bash
   kubectl port-forward svc/postgres-travel-postgresql 5432:5432 -n travel
   kubectl port-forward svc/rabbitmq 5672:5672 15672:15672 -n travel
   kubectl port-forward svc/vault 8200:8200 -n travel
   ```

2. **Créer la base de données** (une seule fois) :
   ```bash
   PGPASSWORD=5l6LoHoDiI psql -h localhost -p 5432 -U postgres -c "CREATE DATABASE travel_payment_db;"
   ```

3. **Exécuter la migration** manuellement :
   ```bash
   PGPASSWORD=5l6LoHoDiI psql -h localhost -p 5432 -U postgres -d travel_payment_db \
     -f /home/mamadbah/Java/travel/backend/payment-service/src/main/resources/db/migration/V1__init_payment_schema.sql
   ```

4. **Compiler** :
   ```bash
   cd payment-service
   ./mvnw compile
   ```

5. **Lancer** :
   ```bash
   ./mvnw spring-boot:run
   ```

Le service démarrera sur le port **8083**.

### Vérification

```bash
# Health check
curl http://localhost:8083/actuator/health

# Swagger UI
open http://localhost:8083/swagger-ui.html
```

---

## ⚙ Configuration

Fichier : `src/main/resources/application.properties`

| Propriété | Valeur par défaut | Description |
|-----------|-------------------|-------------|
| `server.port` | `8083` | Port d'écoute. |
| `spring.datasource.url` | `jdbc:postgresql://localhost:5432/travel_payment_db` | URL Database. |
| `spring.rabbitmq.host` | `localhost` | Adresse RabbitMQ. |
| `payment.simulation.processing-delay-ms` | `2000` | Latence simulée (ms). |
| `spring.flyway.enabled` | `true` | Migration automatique au boot. |

### Variables d'environnement (override)

- `SPRING_DATASOURCE_PASSWORD` — Mot de passe PostgreSQL.
- `SPRING_RABBITMQ_PASSWORD` — Mot de passe RabbitMQ.
- `VAULT_TOKEN` — Token Vault.

---

## 🗃 Migration Base de Données

La migration est gérée par **Flyway** au démarrage de l'application. Si elle échoue (ex: base inexistante), exécutez manuellement :

```bash
# Reset complet (ATTENTION : supprime tout !)
PGPASSWORD=5l6LoHoDiI psql -h localhost -p 5432 -U postgres -d travel_payment_db \
  -c "DROP SCHEMA public CASCADE; CREATE SCHEMA public; GRANT ALL ON SCHEMA public TO postgres; GRANT ALL ON SCHEMA public TO public;" \
  -f payment-service/src/main/resources/db/migration/V1__init_payment_schema.sql
```

Fichier de migration : `src/main/resources/db/migration/V1__init_payment_schema.sql`

---

## 🧪 Tests

### Exécuter les tests
```bash
./mvnw test
```

### Stack de test
- **JUnit 5** + **Mockito** pour les tests unitaires.
- **Testcontainers** (recommandé) pour tester avec un vrai PostgreSQL + RabbitMQ.
- Couverture cible : **80%** sur la logique métier (`PaymentServiceImpl`).

### Points critiques à tester
1. **Simulation réussie** : `amount > 0` → `PaymentStatus.SUCCESS`.
2. **Simulation échouée** : `amount <= 0` → `PaymentStatus.FAILED`.
3. **Idempotence** : Rejeter un doublon (`DuplicatePaymentException`).
4. **Publication RabbitMQ** : Vérifier l'envoi de `PaymentCompletedEvent` avec le bon routing key.
5. **Interruption** : `Thread.sleep` interrompu → `PaymentProcessingException`.

---

## 🗺 Roadmap / Évolutions

| Phase | Fonctionnalité | Description |
|-------|---------------|-------------|
| 🔜 Phase 2 | **Intégration Stripe** | Remplacer la simulation par l'API Stripe Charges/PaymentIntents. |
| 🔜 Phase 2 | **Intégration PayPal** | Ajouter le provider PayPal via le Strategy Pattern. |
| 📅 Phase 3 | **Mobile Money (Wave)** | Intégration de l'API Wave pour le marché sénégalais. |
| 📅 Phase 3 | **Remboursements** | Endpoint + logique de remboursement après annulation. |
| 📅 Phase 3 | **DLQ** | Dead Letter Queue pour les messages RabbitMQ en échec. |
| 📅 Phase 4 | **Sécurité** | Ajouter le JWT filter pour protéger les endpoints de consultation. |
| 📅 Phase 4 | **Webhooks** | Recevoir les callbacks Stripe/PayPal pour les paiements asynchrones réels. |

---

## 🤝 Contribution

1. **Convention de nommage** : CamelCase (Java), snake_case (BDD).
2. **Pattern DTO strict** : Ne jamais exposer `Payment` (entité) directement.
3. **Commit Messages** : Conventional Commits (`feat:`, `fix:`, `refactor:`).
4. **Pull Requests** : Doivent passer le pipeline CI (Build + Test + Sonar).

---

_Généré pour l'équipe Travel Project — Février 2026_
