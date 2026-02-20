# ✈️ Travel Service - Microservice de Gestion des Voyages

> **Service Core** de la plateforme Travel. Responsable du cycle de vie des offres de voyages, de la gestion des inscriptions (subscriptions) et de la recherche.

---

## 📋 Table des Matières
- [Contexte](#-contexte)
- [Stack Technique](#-stack-technique)
- [Architecture & Design](#-architecture--design)
- [Fonctionnalités Métier](#-fonctionnalités-métier)
- [Installation & Démarrage](#-installation--démarrage)
- [Configuration](#-configuration)
- [Base de Données (Schéma)](#-base-de-données-schéma)
- [API & Documentation](#-api--documentation)
- [Tests](#-tests)

---

## 📖 Contexte

Le `travel-service` est le cœur du métier. Il permet aux **Travel Managers** de créer et gérer leurs offres, et aux **Travelers** de consulter et s'inscrire aux voyages.

Ce service implémente des règles métier critiques telles que :
- La gestion des **capacités** (places limitées).
- La règle d'annulation des **3 jours** avant le départ.
- La propagation des événements via **RabbitMQ** (ex: paiement validé).

---

## 🛠 Stack Technique

Ce projet utilise les dernières versions stables de l'écosystème Java/Spring (Février 2026).

| Composant | Technologie | Version | Description |
|-----------|-------------|---------|-------------|
| **Langage** | Java | **21 (LTS)** | Records, Pattern Matching, Sequenced Collections. |
| **Framework** | Spring Boot | **4.0.2** | Core Application Framework. |
| **Build Tool** | Maven | 3.9+ | Gestion des dépendances. |
| **Database** | PostgreSQL | 16+ | Persistance relationnelle. |
| **Migration** | Flyway | Latest | Versionning du schéma de base de données. |
| **Messaging** | RabbitMQ | 3.12+ | Communication asynchrone (Event-driven). |
| **Security** | Spring Security | 7+ | OAuth2 Resource Server (JWT). |
| **Mapping** | MapStruct | 1.6.3 | Mapping DTO <-> Entity haute performance. |
| **Secrets** | HashiCorp Vault | Latest | Gestion sécurisée des secrets (DB pwd, JWT key). |
| **Docs** | SpringDoc OpenAPI | 2.x | Documentation Swagger automatique. |

---

## 🏛 Architecture & Design

Le projet respecte scrupuleusement les principes **SOLID** et une **Architecture en Couches (Layered Architecture)** stricte pour garantir maintenabilité et testabilité.

### Structure des Dossiers
```
src/main/java/sn/travel/travel_service/
├── config/           # Configuration Beans (Security, RabbitMQ, Swagger...)
├── data/             # Couche Persistance
│   ├── entities/     # Entités JPA (Travel, Subscription...)
│   ├── enums/        # Énumérations (TravelStatus, SubscriptionStatus...)
│   └── repositories/ # Interfaces Spring Data JPA
├── exceptions/       # Gestionnaire global d'exceptions (ControllerAdvice)
├── services/         # Logique Métier (Business Layer)
│   └── impl/         # Implémentations des interfaces de service
├── web/              # Couche Présentation (REST)
│   ├── controllers/  # Endpoints REST
│   ├── dto/          # Data Transfer Objects (Requests/Responses)
│   └── mappers/      # Interfaces MapStruct
└── TravelServiceApplication.java # Entrypoint
```

### Principes Clés
1.  **DTO Pattern** : Aucune entité JPA n'est jamais exposée ou acceptée par les contrôleurs. On utilise systématiquement des DTO (`CreateTravelRequest`, `TravelResponse`).
2.  **Interface-based Services** : Tous les services sont définis par une interface pour faciliter les mocks lors des tests.
3.  **Global Exception Handling** : Les erreurs sont standardisées selon la RFC 7807 (`ProblemDetail`).

---

## 💼 Fonctionnalités Métier

### 1. Gestion des Voyages (Travels)
- **Création/Edition** : Réservé aux managers. Permet de définir dates, prix, capacité, et détails logistiques.
- **Workflow d'État** : `DRAFT` -> `PUBLISHED` -> `CANCELLED` / `COMPLETED`.
- **Contrôles** : Validation des dates (start < end), prix positif, capacité > 0.
- **Cascade** : La suppression d'un voyage entraîne l'annulation de toutes les inscriptions associées (ou soft-delete selon config).

### 2. Inscriptions (Subscriptions)
- **Règle des 3 jours** : Un voyageur ne **PEUT PAS** annuler son inscription à moins de 3 jours de la date de départ (`startDate`).
- **Gestion de la Capacité** : Verrouillage optimiste (`@Version` et `OptimisticLocking`) pour éviter le surbooking (Overbooking) lors d'inscriptions concurrentes.
- **Flux de Paiement** : 
    1. Inscription créée -> Statut `PENDING_PAYMENT`.
    2. Événement envoyé au `payment-service`.
    3. Écoute du résultat du paiement -> Statut mis à jour vers `CONFIRMED` ou `CANCELLED`.

### 3. Recherche (Search)
- Recherche simple par mot-clé (titre, description, destination).
- Pagination native (`Pageable`) sur tous les endpoints de liste.

---

## 🚀 Installation & Démarrage

### Prérequis
- **JDK 21** installé.
- **Docker** et **Docker Compose** en cours d'exécution (pour PostgreSQL, RabbitMQ, Vault).
- Un service **Auth** fonctionnel (ou JWT mockés).

### Étapes
1.  **Cloner le dépôt** :
    ```bash
    git clone <repo_url>
    cd travel/backend/travel-service
    ```

2.  **Lancer l'infrastructure (si non démarrée globalement)** :
    Assurez-vous que PostgreSQL, RabbitMQ et Vault sont accessibles.
    ```bash
    # Exemple depuis la racine du projet backend
    docker-compose up -d postgres rabbitmq vault
    ```

3.  **Configuration Environnement** :
    Vérifiez `src/main/resources/application.properties`. 
    Les secrets critiques doivent être soit dans Vault, soit passés en variables d'environnement.
    
    Variables d'environnement courantes (override automatique) :
    - `SPRING_DATASOURCE_PASSWORD`
    - `SPRING_RABBITMQ_PASSWORD`
    - `VAULT_TOKEN`

4.  **Compiler et Lancer** :
    ```bash
    ./mvnw spring-boot:run
    ```

Le service démarrera sur le port **8082**.

---

## ⚙ Configuration

Le fichier `application.properties` définit les comportements par défaut.

| Propriété | Valeur par défaut | Description |
|-----------|-------------------|-------------|
| `server.port` | 8082 | Port d'écoute du service. |
| `spring.datasource.url` | `jdbc:postgresql://localhost:5432/travel_db` | URL Database. |
| `jwt.secret` | (défini dans Vault) | Clé de signature des tokens HS256. |
| `spring.flyway.enabled` | `true` | Exécution automatique des migrations au démarrage. |

### Note sur Vault
Si Vault est indisponible pour le développement local, vous pouvez désactiver l'import Vault dans `application.properties` ou définir la propriété `spring.cloud.vault.enabled=false`.

---

## 🗄 Base de Données (Schéma)

### Tables Principales
- **`travels`** : Stocke les offres.
    - `id` (UUID, PK)
    - `manager_id` (UUID) : Liaison lâche avec `auth-service`.
    - `current_bookings` / `max_capacity` : Pour la concurrence.
    - `version` : Pour le verrouillage optimiste.
- **`subscriptions`** : Table de jointure enrichie.
    - `traveler_id` (UUID)
    - `travel_id` (FK)
    - `status` (`PENDING_PAYMENT`, `CONFIRMED`, `CANCELLED`).
- **`destinations`** / **`activities`** : Tables liées en OneToMany aux `travels`.

---

## 📡 API & Documentation

Une fois le service lancé, la documentation interactive Swagger UI est disponible à :

👉 **http://localhost:8082/swagger-ui.html**

La définition OpenAPI (JSON) est accessible sur :
👉 **http://localhost:8082/api-docs**

### Endpoints Principaux
- `GET /api/v1/travels` : Liste publique des voyages.
- `POST /api/v1/travels` : Créer un voyage (Manager).
- `POST /api/v1/subscriptions/{travelId}` : S'inscrire (Traveler).
- `DELETE /api/v1/subscriptions/{subId}` : Annuler (sous contrainte 3 jours).

---

## 🧪 Tests

Nous visons une couverture de **80%**.

### Exécuter les tests unitaires et d'intégration
```bash
./mvnw test
```

### Stack de Test
- **JUnit 5** : Framework de test.
- **Mockito** : Pour mocker les services tiers.
- **Testcontainers** (Recommandé) : Pour lancer une vraie base PostgreSQL isolée pendant les tests d'intégration.
- **H2 Database** (Alternative) : Base en mémoire si Testcontainers n'est pas configuré.

---

## 🤝 Contribution

1. **Convention de nommage** : CamelCase pour le Java, snake_case pour la BDD.
2. **Commit Messages** : Conventional Commits (ex: `feat: add travel caching`, `fix: subscription date check`).
3. **Pull Requests** : Doivent impérativement passer les pipelines CI (Build + Test + Sonar).

---

_Généré pour l'équipe Travel Project - Février 2026_
