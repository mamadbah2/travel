# ============================================================
#  Travel Backend — Commandes Docker Compose
# ============================================================

.PHONY: infra infra-down up down up-build logs logs-svc clean ps

# Infra seule (comportement par défaut)
infra:
	docker compose up -d

infra-down:
	docker compose down

# Tout : infra + microservices
up:
	docker compose --profile app up -d

down:
	docker compose --profile app down

# Tout avec rebuild des images
up-build:
	docker compose --profile app up -d --build

# Logs
logs:
	docker compose --profile app logs -f

logs-svc:
	docker compose --profile app logs -f $(SVC)

# Nettoyage complet (volumes inclus)
clean:
	docker compose --profile app down -v

# Statut
ps:
	docker compose --profile app ps
