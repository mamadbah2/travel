# Migration K8s → Docker Compose Local

## Contexte

L'abonnement VPS OVH (qui hebergeait le cluster K3s) a expire. Toutes les dependances externes (PostgreSQL, RabbitMQ, Elasticsearch, Vault, MailDev) tournaient sur Kubernetes et etaient accessibles via `kubectl port-forward` (`tunnels.sh`).

Cette migration remplace le cluster K8s par un simple `docker-compose.yml` a la racine du projet, pour continuer le developpement en local sans aucun changement dans le code des services.

---

## Ce qui a ete fait

### 1. `docker-compose.yml` (racine du projet)

Cree un fichier Docker Compose qui lance les 5 services externes sur les **memes ports** que ceux du K8s :

| Service | Image | Port(s) | Pourquoi cette image |
|---------|-------|---------|----------------------|
| PostgreSQL | `postgres:16-alpine` | 5432 | Version 16 comme en prod, Alpine pour la legerete |
| RabbitMQ | `rabbitmq:3.13-management-alpine` | 5672, 15672 | Inclut l'UI de management (port 15672) pour debugger les queues |
| Elasticsearch | `elasticsearch:8.17.0` | 9200 | Version 8.17 — la meme que celle utilisee en K8s, compatible avec le client ES 9.x de Spring Boot 4.0.2 |
| Vault | `hashicorp/vault:1.17` | 8200 | Mode dev (`DEV_ROOT_TOKEN_ID=root`) — pas besoin d'unseal, token `root` comme avant |
| MailDev | `maildev/maildev:2.1.0` | 1025, 1080 | SMTP mock (1025) + UI web (1080) pour visualiser les emails |

**Choix cle : memes ports, memes credentials.** Aucun service Spring Boot n'a besoin de changer sa config de connexion (host=localhost, memes ports, memes users/passwords).

### 2. `infra/postgres/init-databases.sql`

Script SQL execute automatiquement au premier demarrage de PostgreSQL (via `docker-entrypoint-initdb.d`). Cree les 4 bases :

- `travel_auth_db`
- `travel_db`
- `travel_payment_db`
- `travel_notification_db`

**Pourquoi :** Sur K8s, ces bases existaient deja. En local, le container PostgreSQL demarre vide — il faut les creer. Flyway gere ensuite les tables (migrations `V1__*.sql` dans chaque service).

### 3. `infra/vault/init-secrets.sh`

Script qui s'execute automatiquement via le container `vault-init` (depend de Vault healthy). Il :

1. Active les moteurs KV `travel-backend` et `kv`
2. Injecte les secrets pour chaque service (DB password, JWT secret, RabbitMQ password, ES password)

**Pourquoi un container separe :** Vault en mode dev perd ses donnees a chaque restart. Le container `vault-init` re-injecte les secrets a chaque `docker compose up`. C'est idempotent.

### 4. Modification de `search-service/application.properties`

Deux changements :

| Avant | Apres | Raison |
|-------|-------|--------|
| `spring.elasticsearch.uris=https://localhost:9200` | `http://localhost:9200` | Le K8s utilisait un certificat SSL auto-signe. Le Docker local n'a pas SSL (`xpack.security.http.ssl.enabled=false`). Le `ElasticsearchConfig.java` gere deja le scheme dynamiquement. |
| `VAULT_ELASTICSEARCH_PASSWOR` (typo) | `VAULT_ELASTICSEARCH_PASSWORD` | Correction d'un typo dans le nom de la variable d'environnement. Le fallback a aussi ete mis a jour avec le bon mot de passe (`lbB07FlWk4MDeGYx`). |

---

## Comment utiliser

### Demarrage

```bash
# 1. Lancer toute l'infra
docker compose up -d

# 2. Verifier que tout est healthy
docker compose ps

# 3. Lancer un service Spring Boot
cd auth-service && ./mvnw spring-boot:run
```

### Arret

```bash
# Arreter les containers (conserve les donnees)
docker compose down

# Reset complet (supprime les volumes = perte des donnees)
docker compose down -v
```

### Acces aux UIs

| UI | URL | Credentials |
|----|-----|-------------|
| RabbitMQ Management | http://localhost:15672 | `user` / `PASSWORD_RABBIT` |
| MailDev (emails) | http://localhost:1080 | aucun |
| Vault | http://localhost:8200 | token: `root` |
| Swagger (par service) | http://localhost:{port}/swagger-ui.html | — |

### Healthchecks

Tous les containers (sauf MailDev) ont des healthchecks Docker. Vault-init attend que Vault soit healthy avant d'injecter les secrets. On peut verifier :

```bash
# PostgreSQL
PGPASSWORD=5l6LoHoDiI psql -h localhost -U postgres -c "\l"

# Elasticsearch
curl -u elastic:lbB07FlWk4MDeGYx http://localhost:9200

# RabbitMQ
curl -u user:PASSWORD_RABBIT http://localhost:15672/api/overview

# Vault secrets
VAULT_ADDR=http://localhost:8200 VAULT_TOKEN=root vault kv get travel-backend/auth-service
```

### Si Flyway echoue (comme avant)

Les migrations manuelles restent possibles :

```bash
PGPASSWORD=5l6LoHoDiI psql -h localhost -p 5432 -U postgres -d travel_db \
  -c "DROP SCHEMA public CASCADE; CREATE SCHEMA public;" \
  -f travel-service/src/main/resources/db/migration/V1__init_travel_schema.sql
```

---

## Pourquoi ces choix

**Docker Compose plutot que Minikube/K3s local :** Minikube ou K3s aurait reproduit l'environnement K8s, mais c'est beaucoup plus lourd (RAM, CPU, complexite). Docker Compose demarre en secondes et suffit largement pour du dev local. Les services Spring Boot se connectent a `localhost:{port}` — exactement comme avec les `kubectl port-forward`.

**Memes credentials partout :** Le but est zero changement dans le code des services. Les passwords, users et ports sont identiques a ce qui etait configure sur le K8s. Seul le passage HTTPS→HTTP pour Elasticsearch a necessite un changement de config.

**Vault en mode dev :** En production, Vault est sealed et necessite unseal keys. En dev local, le mode dev (`VAULT_DEV_ROOT_TOKEN_ID=root`) evite cette complexite. C'etait deja le cas sur le K8s (token `root`).

**Elasticsearch sans SSL :** Le certificat auto-signe du K8s necessitait un `TrustAllCertsManager` custom. En local, desactiver SSL simplifie tout en gardant l'authentification basic (user/password). Le `ElasticsearchConfig.java` detecte automatiquement le scheme (http vs https).
