# Travel App — Claude Briefing
## Project Overview
This is a Travel Management System — an Airbnb-like platform for organizing and booking travel experiences.
It supports three roles: Admin, Travel Manager, and Traveler.
For a full understanding of the project vision, requirements, and expected features, read:

[`README-1.md`](./docs/README-1.md)— Part 1 of the project brief: infrastructure setup and Admin Dashboard requirements.

[`README-2.md`](./docs/README-2.md) — Part 2 of the project brief: traveler features, manager dashboard, personalized recommendations, and role-based functionalities.

These are the official project specs (énoncé). Always refer to them to understand the intent behind a feature before implementing it.

## Architecture
See [`SOURCE_OF_TRUTH.md`](./docs/SOURCE_OF_TRUTH.md) for the full DDD structure, Angular 19 rules, and coding conventions. Always follow it strictly.

## Features to build
See [`FRONTEND_FEATURES.md`](./docs/FRONTEND_FEATURES.md) for the full feature scope and implementation order.

## API contracts
See [`API_ENDPOINTS_DTO.md`](./docs/API_ENDPOINTS_DTO.md) for all endpoints and DTOs to use as TypeScript types.

## Design
- Color palette: Use evrything that is around those color : #223843, #eff1f3, #dbd3d8, #d8b4a0, #d77a61
- The main theme have to be bright maybe whitish
- Warm, editorial, adventurous aesthetic (Airbnb-inspired but bolder)
- Tailwind CSS, mobile-first, Angular 19 standalone components

## Implementation Progress

### Step 1: API Client + Auth JWT + RBAC Guards — DONE
- Angular 21 project scaffolded (zoneless, SSR, Tailwind v4, NgRx SignalStore)
- All API traffic goes through gateway (`http://localhost:8080`) — no direct service access
- `src/environments/environment.ts` — single `apiBaseUrl`
- `src/app/core/config/api.config.ts` — all 42 endpoint URLs centralized
- `src/app/shared/models/api.models.ts` — all DTOs + enums from API spec
- `src/app/core/auth/data-access/auth.service.ts` — login, register, refresh, logout, currentUser signal, hasRole()
- `src/app/core/auth/data-access/auth.interceptor.ts` — Bearer injection + 401 refresh with request queuing
- `src/app/core/auth/guards/auth.guard.ts` + `role.guard.ts` — functional guards
- `src/app/core/error-handling/global-error.handler.ts` — HTTP error interceptor
- `src/app/shared/data-access/notification.service.ts` — signal-based toast stub
- `src/app/app.routes.ts` — 10 lazy-loaded domain routes with guards
- 11 placeholder components for all feature domains

### Step 2: Traveler flow (catalog → detail → subscription → tracking) — DONE
- Layout shell with responsive navbar (auth-aware, role-based nav links, mobile hamburger)
- Auth pages: login + register with forms, redirect logic, toast notifications
- Shared UI: pagination, status-badge, toast-container, date utils (3-day cancellation calc)
- Travel catalog page with search (debounced), paginated grid of travel cards
- Travel detail page with full info, destinations, activities, subscribe button (role-gated)
- TravelStore (NgRx SignalStore) + TravelService for data fetching
- Subscription list + detail pages with payment status and notification history
- SubscriptionStore (NgRx SignalStore) + SubscriptionService
- Cancel button with 3-day rule enforcement and contextual messaging
- PaymentService + PaymentStatusComponent, NotificationApiService + NotificationListComponent
- SSR config: static prerender for home/auth, client render for dynamic routes
### Step 3: Manager flow (CRUD travels + subscribers) — DONE
- Manager dashboard page with travel list (draft/published counts), publish/cancel/delete actions
- Travel create page with full form (destinations + activities dynamic add/remove)
- Travel edit page with pre-populated form
- Travel manage page (subscribers list with remove, notifications)
- ManagerService (CRUD + publish/cancel + subscribers) + ManagerStore (NgRx SignalStore)
- TravelFormComponent (dumb, reusable for create/edit), TravelManagerCard, SubscriberList
- NotificationApiService extended with getNotificationsByTravel()
### Step 4: Admin flow (users + notifications + payments) — DONE
- Admin dashboard with navigation cards (users, payments, notifications)
- User management page: table with search, role/status filters, inline ban/unban/delete
- User detail page: full profile, ban/unban/delete, performance score update for managers
- Admin payments page: paginated table of all payments with status badges
- Admin notifications page: paginated list of all system notifications
- AdminService (all user CRUD + ban/unban + performance score + global payments/notifications)
- AdminUsersStore (NgRx SignalStore) with search/role/status filtering
- UserTableComponent, UserDetailCardComponent (dumb UI)
### Step 5: Advanced search (Elasticsearch) — DONE
- Search page with filters: text query, min/max price, from date
- SearchService with parameterized HTTP GET to gateway search endpoint
- SearchResultCardComponent (dumb) displaying travel results with destinations, dates, price, spots
- Search/clear buttons, result count, paginated results grid
- Navbar updated with "Search" link in both desktop and mobile menus
### Step 6: Missing features (profiles, dashboards, reviews, reports, recommendations) — DONE
- User profile page: view/edit profile info, change password
- Traveler payments page: paginated payment table with summary cards (total spent, success, pending)
- Traveler notifications page: notification inbox with type icons, status, failure reasons
- Reviews system: ReviewService, ReviewListComponent, ReviewFormComponent (star picker), integrated into travel detail page
- Reports system: ReportService, AdminReportsPage with review/dismiss actions
- Recommendations: RecommendationService, RecommendationsPage with score badges and destination tags
- Home page redesigned: hero section, featured travels grid, personalized recommendations, quick access cards
- Admin analytics page: platform stats (users, managers, travelers, revenue, payments), manager ranking with performance bars
- Manager analytics page: performance score, stats (travels, published, drafts, revenue, occupancy), travel performance breakdown
- Admin dashboard updated with Reports and Analytics navigation cards
- Navbar updated: Payments + Notifications links for travelers in both desktop and mobile menus
- Mock data + interceptor already covers reviews, reports, recommendations endpoints