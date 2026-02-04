### Auth-Service guideline

> **@workspace** / **Act as a Senior Backend Architect**.
> **Project Context:** We are building the `auth-service` for the "Travel" project, a microservices-based Travel Management System. This service is the **Identity and Access Management (IAM)** heart of the ecosystem.
> **Business Role:**
> The `auth-service` is the gatekeeper. Its mission is to manage Users and provide secure access tokens (JWT) for the entire platform. It must support three distinct roles:
> 1. **ADMIN**: Full system oversight, reports, and user management.
> 2. **MANAGER**: Creation and management of travel offers and subscriber lists.
> 3. **TRAVELER**: Search, booking, and feedback.
> 
> 
> **Technical Requirements (Strict):**
> * **Database:** PostgreSQL (already running on K3s, accessible via localhost:5432).
> * **Patterns:** Domain-Driven Design (DDD). Separate Entities from DTOs using MapStruct. Use Lombok to minimize boilerplate.
> * **Secret Management:** Integration with HashiCorp Vault for database credentials.
> **Final Note:** Refer to `.github/copilot-instructions.md` for coding standards and naming conventions.

---
