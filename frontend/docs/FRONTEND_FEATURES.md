# Frontend Scope — Fonctionnalités à implémenter

## 1) Objectif

Définir tout ce que le frontend doit implémenter en s’alignant:
- sur les endpoints disponibles aujourd’hui,
- sur le cahier des charges produit,
- et sur les gaps backend (backlog non encore exposé).

## 2) Règles transverses frontend

- Auth JWT avec rotation (`login`, `refresh`, `logout`).
- RBAC UI: `ADMIN`, `MANAGER`, `TRAVELER`.
- Gestion globale des erreurs API (`400/401/403/404/409/500`).
- Pagination réutilisable (`page,size,sort`) pour toutes les listes.
- État asynchrone (chargement/empty/error/success) sur chaque vue.

## 3) MVP basé sur endpoints existants

## 3.1 Authentification & session

### Fonctionnalités
- Inscription (`register`) avec choix de rôle.
- Connexion (`login`) et stockage sécurisé des tokens.
- Renouvellement automatique du token d’accès (`refresh`).
- Déconnexion (`logout`).
- Profil utilisateur courant (`/users/me`), mise à jour profil, changement mot de passe.

### Endpoints requis
- `POST /api/v1/auth/register`
- `POST /api/v1/auth/login`
- `POST /api/v1/auth/refresh`
- `POST /api/v1/auth/logout`
- `GET /api/v1/users/me`
- `PUT /api/v1/users/me`
- `POST /api/v1/users/me/change-password`

---

## 3.2 Parcours Traveler

### Fonctionnalités
- Catalogue public des voyages.
- Détail voyage.
- Recherche voyage basique (`travels/search`).
- Recherche avancée Elasticsearch (`search`) avec filtres prix/date.
- Souscription à un voyage.
- Liste de mes souscriptions + détail + annulation.
- Suivi paiements (par voyageur / par souscription).
- Consultation notifications voyageur.

### Endpoints requis
- `GET /api/v1/travels`
- `GET /api/v1/travels/{travelId}`
- `GET /api/v1/travels/search?search=...`
- `GET /api/v1/search?q=&minPrice=&maxPrice=&fromDate=`
- `GET /api/v1/search/{travelId}`
- `POST /api/v1/subscriptions/travel/{travelId}`
- `GET /api/v1/subscriptions`
- `GET /api/v1/subscriptions/{subscriptionId}`
- `POST /api/v1/subscriptions/{subscriptionId}/cancel`
- `GET /api/v1/payments/subscription/{subscriptionId}`
- `GET /api/v1/payments/traveler/{travelerId}`
- `GET /api/v1/notifications/traveler/{travelerId}`
- `GET /api/v1/notifications/subscription/{subscriptionId}`

### Points UI critiques
- Afficher clairement la contrainte d’annulation (règle des 3 jours, validée serveur).
- Gérer l’asynchronisme paiement/notification après souscription (statut en attente puis succès/échec).

---

## 3.3 Parcours Manager

### Fonctionnalités
- CRUD offres de voyage (create/update/delete).
- Publication / annulation d’un voyage.
- Vue “mes voyages”.
- Gestion des abonnés d’un voyage.
- Consultation notifications liées à ses voyages.
- Suivi paiements d’un voyage.

### Endpoints requis
- `POST /api/v1/travels`
- `PUT /api/v1/travels/{travelId}`
- `DELETE /api/v1/travels/{travelId}`
- `POST /api/v1/travels/{travelId}/publish`
- `POST /api/v1/travels/{travelId}/cancel`
- `GET /api/v1/travels/manager/me`
- `GET /api/v1/travels/{travelId}/subscribers`
- `DELETE /api/v1/travels/{travelId}/subscribers/{subscriptionId}`
- `GET /api/v1/notifications/travel/{travelId}`
- `GET /api/v1/payments/travel/{travelId}`

### Points UI critiques
- Formulaire voyage complexe (destinations + activités dynamiques).
- Prévisualisation du statut (`DRAFT`, `PUBLISHED`, `CANCELLED`, `COMPLETED`).

---

## 3.4 Parcours Admin

### Fonctionnalités
- Gestion utilisateurs complète.
- Modération basique (ban/unban/delete).
- Gestion score performance manager.
- Consultation globale notifications et paiements.

### Endpoints requis
- `GET /api/v1/users`
- `GET /api/v1/users/{userId}`
- `GET /api/v1/users/role/{role}`
- `GET /api/v1/users/status/{status}`
- `GET /api/v1/users/search?search=...`
- `PUT /api/v1/users/{userId}`
- `POST /api/v1/users/{userId}/ban`
- `POST /api/v1/users/{userId}/unban`
- `DELETE /api/v1/users/{userId}`
- `PUT /api/v1/users/{managerId}/performance-score?score=`
- `GET /api/v1/notifications`
- `GET /api/v1/payments`

## 4) Matrice Feature -> API -> DTO

| Feature Frontend | Endpoints clés | DTO principaux |
|---|---|---|
| Login/Register | `/auth/register`, `/auth/login`, `/auth/refresh` | `RegisterRequest`, `LoginRequest`, `RefreshTokenRequest`, `AuthResponse`, `UserResponse` |
| Profil utilisateur | `/users/me`, `/users/me/change-password` | `UpdateUserRequest`, `ChangePasswordRequest`, `UserResponse`, `MessageResponse` |
| Catalogue voyages | `/travels`, `/travels/{id}`, `/travels/search` | `TravelResponse`, `PageResponse<TravelResponse>` |
| Recherche avancée | `/search`, `/search/{id}` | `SearchResultResponse`, `PageResponse<SearchResultResponse>` |
| Gestion voyages manager | `/travels` (POST/PUT/DELETE), `/publish`, `/cancel`, `/manager/me` | `CreateTravelRequest`, `UpdateTravelRequest`, `TravelResponse`, `MessageResponse` |
| Gestion souscriptions traveler | `/subscriptions/**` | `SubscriptionResponse`, `PageResponse<SubscriptionResponse>` |
| Paiements | `/payments/**` | `PaymentResponse`, `PageResponse<PaymentResponse>` |
| Notifications | `/notifications/**` | `NotificationResponse`, `PageResponse<NotificationResponse>` |
| Admin users | `/users/**` | `UpdateUserRequest`, `UserResponse`, `PageResponse<UserResponse>` |

## 5) Backlog frontend — État actuel (mis à jour 2026-03-27)

> Toutes les features ci-dessous sont implémentées côté frontend avec **données mock** (HTTP interceptor).
> Quand le backend sera disponible, il suffira de retirer l’interceptor mock.

## 5.1 Implémenté via mock data

1. **Dashboard admin global complet** — DONE
   - Stats plateforme (users, managers, travelers, revenue, payments)
   - Classement managers par performance score
   - Page reports avec actions review/dismiss

2. **Dashboard analytics manager complet** — DONE
   - Score de performance, stats grid, breakdown par voyage
   - Revenus, taux d’occupation, nombre de voyages

3. **Social & feedback (avis + signalements)** — DONE
   - Reviews: composant star rating (1-5), formulaire review, liste reviews sur page détail voyage
   - Reports: page admin avec status OPEN/REVIEWED/DISMISSED
   - Rating manager: slider 0-100 sur page détail voyage

4. **Recommandations personnalisées (Neo4j)** — DONE
   - Page recommandations avec score badges et tags destinations
   - Intégré sur la home page pour les travelers connectés

## 5.2 Bonus (non commencé)

5. **PWA offline réservations**
   - Attendu bonus: consultation offline.

6. **Multilingue FR/EN/Wolof**
   - Attendu bonus produit.

7. **Tests E2E**
   - Attendu: tests d’intégration.

## 6) Notes techniques importantes pour le frontend

- **Gateway partielle**: aujourd’hui seuls `auth/travels/payments` sont routés via `:8080`; prévoir stratégie d’accès pour `users/subscriptions/search/notifications`.
- **Asynchronisme métier**: paiement/notification peuvent arriver après la souscription; prévoir polling ou refresh manuel sur écrans.
- **Sécurité rôles**: masquer les actions UI non autorisées même si le backend protège déjà.
- **Contrats de données**: utiliser les DTO du document `API_ENDPOINTS_DTO.md` comme source de types frontend.

## 7) Ordre recommandé d’implémentation frontend

1. Socle API client + auth JWT + guards RBAC.
2. Parcours traveler (catalogue -> détail -> souscription -> suivi).
3. Parcours manager (CRUD voyages + abonnés).
4. Parcours admin (users + notifications + paiements).
5. Recherche avancée Elasticsearch.
6. Backlog non couvert par API (en coordination backend).
