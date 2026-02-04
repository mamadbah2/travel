# Cahier des Charges Global : Travel

## Objectives

L'objectif de ce projet est de concevoir et déployer une plateforme de gestion de voyages (TMS) robuste, scalable et centrée sur l'utilisateur. Le système doit offrir une expérience personnalisée (via l'IA et les graphes), une recherche ultra-rapide, une infrastructure automatisée (DevOps) et une sécurité de niveau bancaire.

Le projet combine une architecture microservices de pointe avec des fonctionnalités adaptées aux rôles d'Administrateur, de Travel Manager et de Voyageur.

---

## Instructions

### 1. Configuration de l'Environnement & Infrastructure

* **Architecture Microservices :** Découper le système en services autonomes (Auth, Travel, Payment, Search, Notification) suivant les principes de haute disponibilité.
* **Infrastructure as Code (IaC) :**
* **Docker & Ansible :** Conteneurisation de tous les services et bases de données. Utilisation de playbooks Ansible pour un déploiement reproductible et scalable.
* **Kubernetes (Bonus) :** Orchestration des conteneurs pour la gestion des répliques et le load-balancing.


* **Pipeline CI/CD :**
* **Jenkins :** Automatisation des tests unitaires et du déploiement à chaque Pull Request (PR).
* **SonarQube :** Analyse statique automatique du code pour garantir la qualité et la sécurité.


* **Logging & Traçabilité :** Implémenter un système de logging centralisé pour tracer les requêtes à travers les microservices.

### 2. Stratégie de Données (Polyglot Persistence)

* **PostgreSQL :** Base de données relationnelle pour la gestion critique des utilisateurs, des transactions financières et des entités de base (CRUD).
* **Neo4j :** Moteur de recommandation basé sur les graphes pour proposer des voyages personnalisés selon les comportements et affinités des voyageurs.
* **Elasticsearch :** Moteur de recherche full-text avec autocomplete pour une navigation fluide et dynamique à travers le catalogue de voyages.
* **Cohérence des données :** Assurer la synchronisation entre PostgreSQL (maître), Neo4j et Elasticsearch.

### 3. Développement et Design par Rôles

#### **A. Admin (Le Contrôleur)**

* **Dashboard Global :** Vue sur les revenus mensuels, rapports d'activité et top managers.
* **Gestion CRUD :** Contrôle total (Ajout/Modif/Suppr) sur les utilisateurs, les voyages et les méthodes de paiement (avec gestion des cascades en base de données).
* **Modération :** Revue des signalements (reports) émis par les voyageurs contre des managers ou d'autres utilisateurs.

#### **B. Travel Manager (L'Organisateur)**

* **Gestion des Offres :** Création et modification de voyages incluant destinations, dates, activités, hébergement et transport.
* **Analytics :** Dashboard dédié avec revenus, nombre de voyageurs et score de performance basé sur les feedbacks.
* **Gestion des Abonnés :** Consulter la liste des inscrits par voyage avec option de désinscription manuelle si nécessaire.

#### **C. Traveler (Le Client)**

* **Recherche Intelligente :** Barre de recherche Elasticsearch avec suggestions en temps réel.
* **Personnalisation :** Suggestions de voyages basées sur au moins 3 critères (historique, préférences, localisation).
* **Gestion de Voyage :** Inscription et désinscription avec une limite stricte de **3 jours avant le départ**.
* **Paiement :** Support natif de **Stripe** et **PayPal** (prévoir une architecture modulaire pour le Mobile Money).
* **Social & Feedback :** Système de notation (avis) et possibilité de signaler des comportements abusifs.

### 4. Sécurité et Conformité

* **Chiffrement :** SSL/TLS obligatoire pour toutes les données en transit.
* **Gestion des Secrets :** Utilisation de **HashiCorp Vault** pour stocker les clés API (Stripe, etc.) et les identifiants de base de données.
* **Contrôle d'Accès (RBAC) :** Application stricte du principe du moindre privilège selon les rôles.
* **Isolation :** Bases de données accessibles uniquement via le réseau interne ou des endpoints sécurisés.

---

## Bonus

* **PWA (Progressive Web App) :** Pour une expérience mobile fluide au Sénégal (mode offline pour consulter ses réservations).
* **Kubernetes :** Orchestration avancée avec Ansible.
* **Multilingue :** Support Français, Anglais et Wolof.
* **Mobile Money :** Intégration d'une API locale (Wave/Orange Money).
* **Tests E2E :** Automatisation des tests de bout en bout avec Cypress ou Playwright.

---

## Audit (Grille d'évaluation)

### I. Compréhension & Architecture

* Le candidat peut-il expliquer le découpage des microservices par domaine métier ?
* Comment est assurée l'indépendance de déploiement de chaque service ?
* Comment les données sont-elles synchronisées entre PG, Neo4j et Elasticsearch ?
* Expliquer le rôle des playbooks Ansible dans le déploiement.

### II. Qualité Technique & CI/CD

* Le pipeline Jenkins bloque-t-il la PR si SonarQube détecte des vulnérabilités ?
* Les tests unitaires couvrent-ils les fonctionnalités critiques (Paiement, Annulation 3 jours) ?
* Le système de logging permet-il de retrouver une erreur précise ?

### III. Fonctionnalités & Métier

* **Admin :** Peut-il voir les rapports de revenus et gérer les conflits ?
* **Manager :** Peut-il accéder à ses statistiques et gérer ses listes d'abonnés ?
* **Traveler :** La recherche Elasticsearch est-elle rapide (< 500ms) ? Les recommandations Neo4j sont-elles cohérentes ?
* **Paiement :** Le processus est-il sécurisé et fonctionnel ?

### IV. Sécurité & UI

* Les mots de passe sont-ils hashés ? Les secrets sont-ils hors du code (Vault) ?
* L'interface est-elle responsive sur Chrome/Mozilla et mobile ?
* La règle de désistement (3 jours) est-elle respectée côté serveur ?

