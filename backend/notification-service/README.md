# 📬 Notification Service - Microservice de Notifications

> Service de notifications de la plateforme **Travel**. Écoute les événements RabbitMQ (inscription et paiement) et envoie des emails HTML via SMTP (MailDev en développement).

---

## 📋 Table des Matières

- [Vue d'Ensemble](#-vue-densemble)
- [Stack Technique](#-stack-technique)
- [Architecture & Design](#-architecture--design)
- [Flux Métier (Business Flow)](#-flux-métier-business-flow)
- [Modèle de Données](#-modèle-de-données)
- [Communication Inter-Services (RabbitMQ)](#-communication-inter-services-rabbitmq)
- [Templates Email](#-templates-email)
- [Endpoints REST (Consultation)](#-endpoints-rest-consultation)
- [Gestion des Exceptions](#-gestion-des-exceptions)
- [Installation & Démarrage](#-installation--démarrage)
- [Configuration](#-configuration)
- [Migration Base de Données](#-migration-base-de-données)
- [Tests](#-tests)

---

## 📖 Vue d'Ensemble

Le `notification-service` est un microservice **event-driven**. Il ne crée **aucune notification via API** — les notifications sont déclenchées automatiquement par des messages RabbitMQ provenant du `travel-service` et du `payment-service`.

### Rôle dans l'architecture
```
┌──────────────┐  SubscriptionCreated   ┌──────────────────┐
│ travel-      │ ────────────────────►  │                  │ ──── Email: "Booking received,
│ service      │                        │  notification-   │       pending payment"
└──────────────┘                        │  service         │
                                        │                  │
┌──────────────┐  PaymentCompleted      │  (SMTP → MailDev)│
│ payment-     │ ────────────────────►  │                  │ ──── Email: "Payment successful!
│ service      │                        │                  │       Trip confirmed" / "Failed"
└──────────────┘                        └──────────────────┘
```

### Emails envoyés

| Événement | Email envoyé |
|-----------|-------------|
| `SubscriptionCreatedEvent` | 📩 "Booking received, pending payment." |
| `PaymentCompletedEvent` (SUCCESS) | ✅ "Payment successful! Your trip is confirmed." |
| `PaymentCompletedEvent` (FAILED) | ❌ "Payment failed. Action required." |

---

## 🛠 Stack Technique

| Composant | Technologie | Version | Description |
|-----------|-------------|---------|-------------|
| **Langage** | Java | **21 (LTS)** | Records, Pattern Matching. |
| **Framework** | Spring Boot | **4.0.2** | Core Application Framework. |
| **Build Tool** | Maven | 3.9+ | Gestion des dépendances. |
| **Database** | PostgreSQL | 16+ | Persistance des notifications (audit). |
| **Migration** | Flyway | Latest | Versionning du schéma. |
| **Messaging** | RabbitMQ | 3.12+ | Event-driven (écoute subscription + payment). |
| **Email** | Spring Mail | Latest | Envoi SMTP via MailDev. |
| **Templates** | Thymeleaf | Latest | Templates HTML pour les emails. |
| **Mapping** | MapStruct | 1.6.3 | DTO <-> Entity haute performance. |
| **Security** | Spring Security | 7+ | JWT validation (shared secret). |
| **Secrets** | HashiCorp Vault | Latest | Gestion sécurisée des secrets. |
| **Docs** | SpringDoc OpenAPI | 2.x | Swagger UI automatique. |
| **Monitoring** | Micrometer + Prometheus | Latest | Métriques applicatives. |

---

## 🏛 Architecture & Design

### Structure des Dossiers

```
src/main/java/sn/travel/notification_service/
├── config/                               # Configuration Beans
│   ├── RabbitMQConfig.java               # Exchanges, queues, bindings, JSON converter
│   ├── NotificationEventListener.java    # @RabbitListener — consomme les événements
│   ├── SecurityConfig.java               # JWT-based security filter chain
│   ├── JwtAuthenticationFilter.java      # Custom JWT filter
│   ├── JwtTokenProvider.java             # JWT parsing utility
│   └── JwtProperties.java               # JWT config properties
├── data/                                 # Couche Persistance
│   ├── entities/
│   │   └── Notification.java             # Entité JPA principale
│   ├── enums/
│   │   ├── NotificationType.java         # SUBSCRIPTION_CREATED / PAYMENT_SUCCESS / PAYMENT_FAILED
│   │   └── NotificationStatus.java       # PENDING / SENT / FAILED
│   ├── records/
│   │   ├── SubscriptionCreatedEvent.java # Event IN (depuis travel-service)
│   │   └── PaymentCompletedEvent.java    # Event IN (depuis payment-service)
│   └── repositories/
│       └── NotificationRepository.java   # Spring Data JPA
├── exceptions/                           # Gestion centralisée des erreurs
│   ├── NotificationServiceException.java # Base abstraite
│   ├── NotificationNotFoundException.java# NOTIFICATION_001
│   ├── EmailSendingException.java        # NOTIFICATION_002
│   ├── NotificationProcessingException.java # NOTIFICATION_003
│   └── GlobalExceptionHandler.java       # @ControllerAdvice (RFC 7807)
├── services/                             # Logique Métier
│   ├── EmailService.java                 # Interface envoi email
│   ├── NotificationService.java          # Interface gestion notifications
│   └── implementation/
│       ├── EmailServiceImpl.java         # Implémentation SMTP + Thymeleaf
│       └── NotificationServiceImpl.java  # Implémentation events + persistence
├── web/                                  # Couche REST (lecture seule)
│   ├── controllers/
│   │   ├── NotificationController.java   # Interface Swagger-annotée
│   │   └── implementation/
│   │       └── NotificationControllerImpl.java
│   ├── dto/
│   │   └── responses/
│   │       ├── NotificationResponse.java
│   │       ├── PageResponse.java
│   │       └── MessageResponse.java
│   └── mappers/
│       └── NotificationMapper.java       # MapStruct mapper
└── NotificationServiceApplication.java   # Entrypoint
```

### Principes Clés

1. **Event-Driven** : Aucun endpoint de création. Les notifications sont créées par le `NotificationEventListener` qui écoute RabbitMQ.
2. **DTO Pattern** : L'entité `Notification` n'est jamais exposée. Tout passe par `NotificationResponse`.
3. **Interface-based Services** : `EmailService` + `NotificationService` (interfaces) → implementations.
4. **RFC 7807** : Toutes les erreurs retournent un `ProblemDetail` avec `errorCode`, `timestamp`, et `type`.
5. **Thymeleaf Templates** : Emails HTML professionnels avec templates réutilisables.

---

## 🔄 Flux Métier (Business Flow)

### Flux 1 : Inscription (Subscription Created)

```
 Traveler s'inscrit à un voyage (travel-service)
         │
         ▼
 SubscriptionCreatedEvent publié sur RabbitMQ
 (subscription.exchange / subscription.created)
         │
         ├──► payment-service (traitement paiement)
         │
         └──► notification-service (email)
                  │
                  ▼
              NotificationEventListener.handleSubscriptionCreatedEvent()
                  │
                  ▼
              NotificationServiceImpl.handleSubscriptionCreated()
                  │
                  ├── 1. Resolve traveler email
                  ├── 2. Render Thymeleaf template "subscription-created"
                  ├── 3. Send HTML email via MailDev SMTP
                  └── 4. Persist Notification entity (SENT/FAILED)
```

### Flux 2 : Paiement (Payment Completed)

```
 payment-service termine le traitement
         │
         ▼
 PaymentCompletedEvent publié sur RabbitMQ
 (payment.exchange / payment.success ou payment.failed)
         │
         ├──► travel-service (mise à jour subscription)
         │
         └──► notification-service (email)
                  │
                  ▼
              NotificationEventListener.handlePaymentCompletedEvent()
                  │
                  ▼
              NotificationServiceImpl.handlePaymentCompleted()
                  │
                  ├── 1. Determine SUCCESS or FAILED
                  ├── 2. Render appropriate Thymeleaf template
                  ├── 3. Send HTML email via MailDev SMTP
                  └── 4. Persist Notification entity (SENT/FAILED)
```

---

## 📊 Modèle de Données

### Table `notifications`

| Colonne | Type | Contrainte | Description |
|---------|------|-----------|-------------|
| `id` | UUID | PK, auto-generated | Identifiant unique |
| `traveler_id` | UUID | NOT NULL | ID du voyageur |
| `travel_id` | UUID | NOT NULL | ID du voyage |
| `subscription_id` | UUID | NOT NULL | ID de l'inscription |
| `recipient_email` | VARCHAR(255) | NOT NULL | Adresse email destinataire |
| `subject` | VARCHAR(500) | NOT NULL | Sujet de l'email |
| `body` | TEXT | | Corps/description |
| `type` | ENUM | NOT NULL | SUBSCRIPTION_CREATED, PAYMENT_SUCCESS, PAYMENT_FAILED |
| `status` | ENUM | NOT NULL, DEFAULT 'PENDING' | PENDING, SENT, FAILED |
| `failure_reason` | VARCHAR(500) | | Raison d'échec |
| `created_at` | TIMESTAMP | NOT NULL | Date de création |
| `updated_at` | TIMESTAMP | | Date de mise à jour |

---

## 🐰 Communication Inter-Services (RabbitMQ)

### Events Consommés

| Event | Exchange | Routing Key | Queue (notification) | Source |
|-------|----------|-------------|---------------------|--------|
| `SubscriptionCreatedEvent` | `subscription.exchange` | `subscription.created` | `notification.subscription.queue` | travel-service |
| `PaymentCompletedEvent` | `payment.exchange` | `payment.#` (wildcard) | `notification.payment.queue` | payment-service |

> **Note** : Les queues sont dédiées au notification-service (distinctes de celles du payment-service) grâce au pattern Topic Exchange + queues séparées.

---

## ✉️ Templates Email

Trois templates Thymeleaf HTML dans `src/main/resources/templates/` :

| Template | Trigger | Description |
|----------|---------|-------------|
| `subscription-created.html` | `SubscriptionCreatedEvent` | Email de confirmation de booking en attente de paiement |
| `payment-success.html` | `PaymentCompletedEvent` (SUCCESS) | Email de confirmation de paiement et voyage confirmé |
| `payment-failed.html` | `PaymentCompletedEvent` (FAILED) | Email d'échec de paiement avec instructions |

---

## 🌐 Endpoints REST (Consultation)

Base URL: `http://localhost:8084/api/v1`

| Méthode | Endpoint | Rôle | Description |
|---------|----------|------|-------------|
| GET | `/notifications` | ADMIN | Liste paginée de toutes les notifications |
| GET | `/notifications/{id}` | Authenticated | Détail d'une notification |
| GET | `/notifications/traveler/{travelerId}` | ADMIN, TRAVELER | Notifications d'un voyageur |
| GET | `/notifications/travel/{travelId}` | ADMIN, MANAGER | Notifications d'un voyage |
| GET | `/notifications/subscription/{subscriptionId}` | Authenticated | Notifications d'une inscription |

---

## ⚠️ Gestion des Exceptions

| Code | Exception | HTTP Status | Description |
|------|-----------|-------------|-------------|
| `NOTIFICATION_001` | `NotificationNotFoundException` | 404 | Notification introuvable |
| `NOTIFICATION_002` | `EmailSendingException` | 500 | Échec d'envoi d'email |
| `NOTIFICATION_003` | `NotificationProcessingException` | 500 | Erreur de traitement |
| `NOTIFICATION_VALIDATION` | Validation errors | 400 | Erreurs de validation |
| `NOTIFICATION_INTERNAL` | Generic errors | 500 | Erreur inattendue |

---

## 🚀 Installation & Démarrage

### Prérequis
- **JDK 21** installé
- **Docker** en cours d'exécution (PostgreSQL, RabbitMQ, Vault, MailDev)
- Services **auth-service**, **travel-service**, **payment-service** fonctionnels

### Étapes

1. **Créer la base de données** :
    ```bash
    PGPASSWORD=5l6LoHoDiI psql -h localhost -p 5432 -U postgres -c "CREATE DATABASE travel_notification_db;"
    ```

2. **Appliquer la migration (si Flyway ne fonctionne pas)** :
    ```bash
    PGPASSWORD=5l6LoHoDiI psql -h localhost -p 5432 -U postgres -d travel_notification_db -f notification-service/src/main/resources/db/migration/V1__init_notification_schema.sql
    ```

3. **Compiler et Lancer** :
    ```bash
    cd notification-service
    ./mvnw clean install -DskipTests
    ./mvnw spring-boot:run
    ```

Le service démarrera sur le port **8084**.

4. **Vérifier MailDev** : Ouvrir `http://localhost:1080` pour voir les emails.

---

## ⚙ Configuration

### `application.properties`

| Propriété | Valeur par défaut | Description |
|-----------|-------------------|-------------|
| `server.port` | `8084` | Port HTTP du service |
| `spring.datasource.url` | `jdbc:postgresql://localhost:5432/travel_notification_db` | URL PostgreSQL |
| `spring.rabbitmq.host` | `localhost` | Hôte RabbitMQ |
| `spring.rabbitmq.port` | `5672` | Port RabbitMQ |
| `spring.mail.host` | `localhost` | Hôte SMTP (MailDev) |
| `spring.mail.port` | `1025` | Port SMTP (MailDev) |
| `jwt.secret` | `${jwt.secret:...}` | Secret JWT (overridden by Vault) |

### Vault Secrets (Production)

```
vault kv put kv/notification-service \
  spring.datasource.password=<DB_PASSWORD> \
  spring.rabbitmq.password=<RABBITMQ_PASSWORD> \
  jwt.secret=<JWT_SECRET>
```

---

## 🗂 Migration Base de Données

| Fichier | Description |
|---------|-------------|
| `V1__init_notification_schema.sql` | Table `notifications` + indexes |

---

## 🧪 Tests

### Fichier HTTP
Utiliser `notif-service-test.http` pour tester le flux complet :
1. Login (auth-service)
2. Créer + publier un voyage (travel-service)
3. S'inscrire (déclenche SubscriptionCreatedEvent → Email 1)
4. Attendre ~2s (PaymentCompletedEvent → Email 2)
5. Consulter les notifications via les endpoints REST
6. Vérifier les emails dans MailDev (`http://localhost:1080`)

### Tests Unitaires
```bash
cd notification-service
./mvnw test
```
