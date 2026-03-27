# Project Context: Modern Angular Airbnb like (2026 Standards)

## 1. Core Architecture Philosophy
This project is built using a **Domain-Driven Design (DDD)** approach. Instead of grouping files by technical type (e.g., all services in one folder), we group them by business domain (e.g., Movies, Bookings, User).

### Key Tech Stack:
- **Framework:** Angular 19+ (Standalone Components, No NgModules).
- **Reactivity:** **Angular Signals** (Input/Output signals, Computed, Effects).
- **Data Fetching:** **Resource API** (`resource()` and `httpResource()`).
- **Performance:** **Zoneless Change Detection** (No `zone.js`) and **SSR** (Server-Side Rendering).
- **Styling:** Tailwind CSS (Mobile-first).

---

## 2. Project Directory Structure
All development must strictly adhere to the following tree structure. **"Pages"** are located in `features/`, **"Services"** are in `data-access/`, and **"Dumb Components"** are in `ui/`.

```text
src/app/
├── core/                                # Global Singletons (One-time setup)
│   ├── auth/
│   │   ├── data-access/
│   │   │   ├── auth.service.ts          # Authentication logic
│   │   │   └── auth.interceptor.ts      # Token injection
│   │   └── guards/
│   │       └── auth.guard.ts
│   └── error-handling/
│       └── global-error.handler.ts
├── shared/                              # Truly Reusable UI (Dumb Components)
│   ├── ui/
│   │   ├── sidebar/                     # Global Sidebar
│   │   │   ├── sidebar.component.ts
│   │   │   ├── sidebar.component.html
│   │   │   └── sidebar.component.css
│   │   ├── toast/                       # Global Notifications
│   │   │   ├── toast.component.ts
│   │   │   ├── toast.component.html
│   │   │   └── toast.component.css
│   │   └── loader/
│   │       ├── loader.component.ts
│   │       └── loader.component.html
│   └── data-access/
│       └── notification.service.ts      # Service to trigger toasts
├── features/
│   ├── home/
│   ├── auth/                    # Login, Register pages
│   ├── travels/                 # Browse catalog, Travel detail
│   │   ├── data-access/
│   │   │   ├── travel.service.ts
│   │   │   └── travels.store.ts
│   │   ├── ui/
│   │   │   └── travel-card/
│   │   └── features/
│   │       ├── travel-list/
│   │       └── travel-detail/
│   ├── search/                  # Elasticsearch search page
│   ├── subscriptions/           # My subscriptions, booking flow
│   ├── payments/                # Payment tracking
│   ├── notifications/           # Notification center
│   ├── manager/                 # Manager dashboard, CRUD travels
│   ├── admin/                   # Admin dashboard, user management
│   └── user/                    # Profile, settings
├── app.component.ts                     # Injects Sidebar and Toast
├── app.config.ts                        # App providers (Zoneless, Hydration)
└── app.routes.ts                        # Master list of lazy-loaded domains

Take into account that this is not the exact tree I want to implement, it is just for an example of how to organize the files.

## 3. Implementation Rules for the AI Agent

### A. Component Pattern
1. **Features (Smart Components):** Located in `features/[domain]/features/`. They inject services/stores, handle routing parameters, and coordinate the data flow.
2. **UI (Dumb Components):** Located in `features/[domain]/ui/` or `shared/ui/`. They use `input()` and `output()` signals for data communication. They **must never** inject services or handle API logic.

### B. Reactive Data Fetching
- Use the **`resource()`** or **`httpResource()`** API for all HTTP calls and data loading.
- Avoid manual `subscribe()` or the `async` pipe.
- **Standard Pattern:**
```typescript
readonly id = input.required<string>();

readonly dataResource = resource({
  request: () => this.id(),
  loader: ({request}) => fetchApi(request)
});```

### C. Routing & Lazy Loading
- The `app.routes.ts` must use `loadChildren` to point to domain-specific route files (The Shell Pattern).
- Each domain should have its own routing file (e.g., `movies.routes.ts`) located within its `features/` directory to keep the domain self-contained and support independent bundle splitting.

### D. Change Detection & Performance
- Always configure `app.config.ts` with `provideZonelessChangeDetection()`.
- Every component should be designed for **Zoneless** execution, relying purely on Signals to trigger UI updates. Avoid any logic that depends on `zone.js` monkey-patching.

### E. State Management
- Use **NgRx SignalStore** for complex, cross-component domain state (located in `data-access/`).
- Use local signals (`signal()`, `computed()`) for internal component state.
- Prefer `patchState` for updating store state to maintain immutability.

---

## 4. Immediate Instructions for Tasks
When asked to build a new feature or modify an existing one:

1. **Identify the Domain:** Determine which business domain the feature belongs to (e.g., Movies, Listings, Auth).
2. **Layered Structure:** Organize the code into `data-access/` (Logic/API), `ui/` (Presentational), and `features/` (Pages/Smart Components).
3. **Signals Over RxJS:** Always prefer Signals for state and data flow. Use RxJS only for complex event streams (like WebSockets, debounced user input, or advanced timing logic).
4. **Tailwind Styling:** Use **Tailwind CSS** for all layouts and components, following a mobile-first, responsive design approach consistent with modern UI standards.
5. **Standalone:** Ensure every new component, pipe, or directive is marked as `standalone: true`.


PS: Convert all DTOs from API_ENDPOINTS_DTO.md into TypeScript interfaces in src/app/shared/models/api.models.ts