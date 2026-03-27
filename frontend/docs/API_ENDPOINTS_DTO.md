# Référence API Frontend — Endpoints + DTO

## 1) Vue d’ensemble

Ce document regroupe les endpoints backend disponibles et les contrats DTO à utiliser côté frontend.

- Version API: `v1`
- Format: JSON
- Pagination standard: `page`, `size`, `sort`
- Auth: JWT Bearer (`Authorization: Bearer <accessToken>`)

## 2) URLs de base

### 2.1 Via API Gateway (recommandé frontend)
- Base: `http://localhost:8080`
- Routé actuellement:
  - `/api/v1/auth/**` -> auth-service
  - `/api/v1/travels/**` -> travel-service
  - `/api/v1/payments/**` -> payment-service

### 2.2 Accès direct services (utile en dev, hors gateway)
- Auth service: `http://localhost:8081`
- Travel service: `http://localhost:8082`
- Payment service: `http://localhost:8083`
- Notification service: `http://localhost:8084`
- Search service: `http://localhost:8085`

> Note: la gateway ne route pas encore `/api/v1/users/**`, `/api/v1/subscriptions/**`, `/api/v1/notifications/**`, `/api/v1/search/**` dans la config actuelle.

## 3) Conventions DTO partagées

### 3.1 `MessageResponse`
```json
{ "message": "string" }
```

### 3.2 `PageResponse<T>`
```json
{
  "content": ["T"],
  "page": 0,
  "size": 20,
  "totalElements": 0,
  "totalPages": 0,
  "first": true,
  "last": false
}
```

## 4) Auth Service (`/api/v1/auth`) + Users (`/api/v1/users`)

### 4.1 Enums
- `UserRole`: `ADMIN | MANAGER | TRAVELER`
- `UserStatus`: `ACTIVE | BANNED | PENDING_VERIFICATION`

### 4.2 DTO request

#### `RegisterRequest`
```json
{
  "email": "string(email)",
  "password": "string(min:8,max:100)",
  "firstName": "string(min:2,max:100)",
  "lastName": "string(min:2,max:100)",
  "phoneNumber": "string(max:20) | null",
  "role": "ADMIN|MANAGER|TRAVELER"
}
```

#### `LoginRequest`
```json
{
  "email": "string(email)",
  "password": "string(min:8)"
}
```

#### `RefreshTokenRequest`
```json
{ "refreshToken": "string" }
```

#### `ChangePasswordRequest`
```json
{
  "currentPassword": "string",
  "newPassword": "string(min:8,max:100)"
}
```

#### `UpdateUserRequest`
```json
{
  "email": "string(email) | null",
  "firstName": "string(min:2,max:100) | null",
  "lastName": "string(min:2,max:100) | null",
  "phoneNumber": "string(max:20) | null",
  "role": "UserRole | null",
  "status": "UserStatus | null",
  "performanceScore": "number(float) | null"
}
```

### 4.3 DTO response

#### `UserResponse`
```json
{
  "id": "uuid",
  "email": "string",
  "firstName": "string",
  "lastName": "string",
  "phoneNumber": "string | null",
  "role": "UserRole",
  "status": "UserStatus",
  "performanceScore": "number | null",
  "createdAt": "datetime",
  "lastLoginAt": "datetime | null"
}
```

#### `AuthResponse`
```json
{
  "accessToken": "string",
  "refreshToken": "string",
  "tokenType": "Bearer",
  "expiresIn": 86400000,
  "user": "UserResponse"
}
```

### 4.4 Endpoints Auth

#### `POST /api/v1/auth/register`
- Auth: public
- Body: `RegisterRequest`
- Response: `AuthResponse` (201)

#### `POST /api/v1/auth/login`
- Auth: public
- Body: `LoginRequest`
- Response: `AuthResponse` (200)

#### `POST /api/v1/auth/refresh`
- Auth: public
- Body: `RefreshTokenRequest`
- Response: `AuthResponse` (200)

#### `POST /api/v1/auth/logout`
- Auth: JWT
- Body: none
- Response: `MessageResponse` (200)

### 4.5 Endpoints Users

#### `GET /api/v1/users/me`
- Auth: JWT
- Response: `UserResponse`

#### `PUT /api/v1/users/me`
- Auth: JWT
- Body: `UpdateUserRequest` (rôle/statut/performance ignorés côté serveur)
- Response: `UserResponse`

#### `POST /api/v1/users/me/change-password`
- Auth: JWT
- Body: `ChangePasswordRequest`
- Response: `MessageResponse`

#### `GET /api/v1/users/{userId}`
- Auth: JWT
- Role: `ADMIN`
- Path: `userId: uuid`
- Response: `UserResponse`

#### `GET /api/v1/users`
- Auth: JWT
- Role: `ADMIN`
- Query: `page,size,sort`
- Response: `PageResponse<UserResponse>`

#### `GET /api/v1/users/role/{role}`
- Auth: JWT
- Role: `ADMIN`
- Path: `role: UserRole`
- Query: `page,size,sort`
- Response: `PageResponse<UserResponse>`

#### `GET /api/v1/users/status/{status}`
- Auth: JWT
- Role: `ADMIN`
- Path: `status: UserStatus`
- Query: `page,size,sort`
- Response: `PageResponse<UserResponse>`

#### `GET /api/v1/users/search?search=...`
- Auth: JWT
- Role: `ADMIN`
- Query: `search` + `page,size,sort`
- Response: `PageResponse<UserResponse>`

#### `PUT /api/v1/users/{userId}`
- Auth: JWT
- Role: `ADMIN`
- Body: `UpdateUserRequest`
- Response: `UserResponse`

#### `POST /api/v1/users/{userId}/ban`
- Auth: JWT
- Role: `ADMIN`
- Response: `UserResponse`

#### `POST /api/v1/users/{userId}/unban`
- Auth: JWT
- Role: `ADMIN`
- Response: `UserResponse`

#### `DELETE /api/v1/users/{userId}`
- Auth: JWT
- Role: `ADMIN`
- Response: `MessageResponse`

#### `PUT /api/v1/users/{managerId}/performance-score?score=...`
- Auth: JWT
- Role: `ADMIN`
- Query: `score: float`
- Response: `UserResponse`

## 5) Travel Service (`/api/v1/travels`) + Subscriptions (`/api/v1/subscriptions`)

### 5.1 Enums
- `TravelStatus`: `DRAFT | PUBLISHED | CANCELLED | COMPLETED`
- `SubscriptionStatus`: `PENDING_PAYMENT | CONFIRMED | CANCELLED`
- `AccommodationType`: `HOTEL | HOSTEL | RESORT | APARTMENT | CAMPING | GUESTHOUSE | OTHER`
- `TransportationType`: `FLIGHT | BUS | TRAIN | BOAT | CAR | MINIBUS | OTHER`

### 5.2 DTO request

#### `DestinationRequest`
```json
{
  "name": "string(max:255)",
  "country": "string(max:100)",
  "city": "string(max:100) | null",
  "description": "string(max:5000) | null",
  "displayOrder": "integer | null"
}
```

#### `ActivityRequest`
```json
{
  "name": "string(max:255)",
  "description": "string(max:5000) | null",
  "location": "string(max:255) | null",
  "displayOrder": "integer | null"
}
```

#### `CreateTravelRequest`
```json
{
  "title": "string(max:255)",
  "description": "string(max:5000) | null",
  "startDate": "date(future)",
  "endDate": "date(future)",
  "price": "number(positive)",
  "maxCapacity": "integer(min:1)",
  "accommodationType": "AccommodationType | null",
  "accommodationName": "string(max:255) | null",
  "transportationType": "TransportationType | null",
  "transportationDetails": "string(max:500) | null",
  "destinations": ["DestinationRequest"],
  "activities": ["ActivityRequest"]
}
```

#### `UpdateTravelRequest`
```json
{
  "title": "string(max:255) | null",
  "description": "string(max:5000) | null",
  "startDate": "date(future) | null",
  "endDate": "date(future) | null",
  "price": "number(positive) | null",
  "maxCapacity": "integer(min:1) | null",
  "status": "TravelStatus | null",
  "accommodationType": "AccommodationType | null",
  "accommodationName": "string(max:255) | null",
  "transportationType": "TransportationType | null",
  "transportationDetails": "string(max:500) | null",
  "destinations": ["DestinationRequest"] | null,
  "activities": ["ActivityRequest"] | null
}
```

### 5.3 DTO response

#### `DestinationResponse`
```json
{
  "id": "uuid",
  "name": "string",
  "country": "string",
  "city": "string | null",
  "description": "string | null",
  "displayOrder": "integer | null"
}
```

#### `ActivityResponse`
```json
{
  "id": "uuid",
  "name": "string",
  "description": "string | null",
  "location": "string | null",
  "displayOrder": "integer | null"
}
```

#### `TravelResponse`
```json
{
  "id": "uuid",
  "managerId": "uuid",
  "title": "string",
  "description": "string | null",
  "startDate": "date",
  "endDate": "date",
  "duration": "integer",
  "price": "number",
  "maxCapacity": "integer",
  "currentBookings": "integer",
  "availableSpots": "integer",
  "status": "TravelStatus",
  "accommodationType": "AccommodationType | null",
  "accommodationName": "string | null",
  "transportationType": "TransportationType | null",
  "transportationDetails": "string | null",
  "destinations": ["DestinationResponse"],
  "activities": ["ActivityResponse"],
  "createdAt": "datetime",
  "updatedAt": "datetime"
}
```

#### `SubscriptionResponse`
```json
{
  "id": "uuid",
  "travelerId": "uuid",
  "travelId": "uuid",
  "travelTitle": "string",
  "status": "PENDING_PAYMENT|CONFIRMED|CANCELLED",
  "createdAt": "datetime",
  "updatedAt": "datetime"
}
```

### 5.4 Endpoints Travels

#### `GET /api/v1/travels`
- Auth: public
- Query: `page,size,sort`
- Response: `PageResponse<TravelResponse>`

#### `GET /api/v1/travels/{travelId}`
- Auth: public
- Response: `TravelResponse`

#### `GET /api/v1/travels/search?search=...`
- Auth: public
- Query: `search` + `page,size,sort`
- Response: `PageResponse<TravelResponse>`

#### `POST /api/v1/travels`
- Auth: JWT
- Role: `MANAGER`
- Body: `CreateTravelRequest`
- Response: `TravelResponse` (201)

#### `PUT /api/v1/travels/{travelId}`
- Auth: JWT
- Role: `MANAGER`
- Body: `UpdateTravelRequest`
- Response: `TravelResponse`

#### `DELETE /api/v1/travels/{travelId}`
- Auth: JWT
- Role: `MANAGER | ADMIN`
- Response: `MessageResponse`

#### `POST /api/v1/travels/{travelId}/publish`
- Auth: JWT
- Role: `MANAGER`
- Response: `TravelResponse`

#### `POST /api/v1/travels/{travelId}/cancel`
- Auth: JWT
- Role: `MANAGER`
- Response: `TravelResponse`

#### `GET /api/v1/travels/manager/me`
- Auth: JWT
- Role: `MANAGER`
- Query: `page,size,sort`
- Response: `PageResponse<TravelResponse>`

#### `GET /api/v1/travels/{travelId}/subscribers`
- Auth: JWT
- Role: `MANAGER | ADMIN`
- Query: `page,size,sort`
- Response: `PageResponse<SubscriptionResponse>`

#### `DELETE /api/v1/travels/{travelId}/subscribers/{subscriptionId}`
- Auth: JWT
- Role: `MANAGER | ADMIN`
- Response: `MessageResponse`

### 5.5 Endpoints Subscriptions

#### `POST /api/v1/subscriptions/travel/{travelId}`
- Auth: JWT
- Role: `TRAVELER`
- Body: none
- Response: `SubscriptionResponse` (201)
- Règles: capacité + règle d’annulation (3 jours) gérées backend

#### `POST /api/v1/subscriptions/{subscriptionId}/cancel`
- Auth: JWT
- Role: `TRAVELER`
- Body: none
- Response: `SubscriptionResponse`

#### `GET /api/v1/subscriptions/{subscriptionId}`
- Auth: JWT
- Role: `TRAVELER`
- Response: `SubscriptionResponse`

#### `GET /api/v1/subscriptions`
- Auth: JWT
- Role: `TRAVELER`
- Query: `page,size,sort`
- Response: `PageResponse<SubscriptionResponse>`

## 6) Payment Service (`/api/v1/payments`)

### 6.1 Enums
- `PaymentStatus`: `PENDING | SUCCESS | FAILED`
- `PaymentMethod`: `STRIPE | PAYPAL | WAVE | SIMULATED`

### 6.2 DTO response

#### `PaymentResponse`
```json
{
  "id": "uuid",
  "subscriptionId": "uuid",
  "travelId": "uuid",
  "travelerId": "uuid",
  "travelTitle": "string",
  "amount": "number",
  "currency": "string",
  "method": "PaymentMethod",
  "transactionId": "string",
  "status": "PaymentStatus",
  "failureReason": "string | null",
  "createdAt": "datetime",
  "updatedAt": "datetime"
}
```

### 6.3 Endpoints

#### `GET /api/v1/payments/{paymentId}`
- Auth: JWT via gateway (service direct non verrouillé actuellement)
- Response: `PaymentResponse`

#### `GET /api/v1/payments/subscription/{subscriptionId}`
- Auth: JWT via gateway
- Response: `PaymentResponse`

#### `GET /api/v1/payments/traveler/{travelerId}`
- Auth: JWT via gateway
- Query: `page,size,sort`
- Response: `PageResponse<PaymentResponse>`

#### `GET /api/v1/payments/travel/{travelId}`
- Auth: JWT via gateway
- Query: `page,size,sort`
- Response: `PageResponse<PaymentResponse>`

#### `GET /api/v1/payments`
- Auth: JWT via gateway
- Query: `page,size,sort`
- Response: `PageResponse<PaymentResponse>`

> Note architecture: création de paiement asynchrone via RabbitMQ après inscription (`SubscriptionCreatedEvent`).

## 7) Notification Service (`/api/v1/notifications`)

### 7.1 Enums
- `NotificationType`: `SUBSCRIPTION_CREATED | PAYMENT_SUCCESS | PAYMENT_FAILED`
- `NotificationStatus`: `PENDING | SENT | FAILED`

### 7.2 DTO response

#### `NotificationResponse`
```json
{
  "id": "uuid",
  "travelerId": "uuid",
  "travelId": "uuid",
  "subscriptionId": "uuid",
  "recipientEmail": "string",
  "subject": "string",
  "body": "string",
  "type": "NotificationType",
  "status": "NotificationStatus",
  "failureReason": "string | null",
  "createdAt": "datetime",
  "updatedAt": "datetime"
}
```

### 7.3 Endpoints

#### `GET /api/v1/notifications/{notificationId}`
- Auth: JWT
- Role: authentifié
- Response: `NotificationResponse`

#### `GET /api/v1/notifications`
- Auth: JWT
- Role: `ADMIN`
- Query: `page,size,sort`
- Response: `PageResponse<NotificationResponse>`

#### `GET /api/v1/notifications/traveler/{travelerId}`
- Auth: JWT
- Role: `ADMIN | TRAVELER`
- Query: `page,size,sort`
- Response: `PageResponse<NotificationResponse>`

#### `GET /api/v1/notifications/travel/{travelId}`
- Auth: JWT
- Role: `ADMIN | MANAGER`
- Query: `page,size,sort`
- Response: `PageResponse<NotificationResponse>`

#### `GET /api/v1/notifications/subscription/{subscriptionId}`
- Auth: JWT
- Role: authentifié
- Query: `page,size,sort`
- Response: `PageResponse<NotificationResponse>`

> Note architecture: notifications créées asynchrones (évènements RabbitMQ), emails visibles via MailDev (`http://localhost:1080`).

## 8) Search Service (`/api/v1/search`)

### 8.1 DTO response

#### `SearchResultResponse`
```json
{
  "id": "string",
  "managerId": "string",
  "title": "string",
  "description": "string",
  "startDate": "date",
  "endDate": "date",
  "duration": "integer",
  "price": "number",
  "maxCapacity": "integer",
  "currentBookings": "integer",
  "availableSpots": "integer",
  "status": "string",
  "accommodationType": "string | null",
  "accommodationName": "string | null",
  "transportationType": "string | null",
  "transportationDetails": "string | null",
  "destinations": [
    {
      "name": "string",
      "country": "string",
      "city": "string | null",
      "description": "string | null"
    }
  ],
  "activities": [
    {
      "name": "string",
      "description": "string | null",
      "location": "string | null"
    }
  ],
  "createdAt": "datetime",
  "updatedAt": "datetime"
}
```

### 8.2 Endpoints

#### `GET /api/v1/search`
- Auth: public
- Query:
  - `q` (string, optionnel)
  - `minPrice` (number, optionnel)
  - `maxPrice` (number, optionnel)
  - `fromDate` (`yyyy-MM-dd`, optionnel)
  - `page,size,sort`
- Response: `PageResponse<SearchResultResponse>`

#### `GET /api/v1/search/{travelId}`
- Auth: public
- Response: `SearchResultResponse`

## 9) Service Rec

- `rec-service` présent mais **pas d’endpoints REST implémentés** (pas de contrôleurs).

## 10) Résumé couverture

- Endpoints identifiés: **42**
- Services exposés: Auth, Users, Travels, Subscriptions, Payments, Notifications, Search
- Contrats DTO front: couverts pour tous les endpoints (body/response/no-body)
