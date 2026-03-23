#!/bin/bash
# Attend que Vault soit pret
until vault status > /dev/null 2>&1; do
  echo "En attente de Vault..."
  sleep 2
done

echo "Vault est pret. Injection des secrets..."

# Activer le moteur KV v2 pour le backend travel
vault secrets enable -path=travel-backend kv 2>/dev/null || true

# Activer le moteur KV v2 pour le api-gateway
vault secrets enable -path=kv kv 2>/dev/null || true

# Secrets auth-service
vault kv put travel-backend/auth-service \
  spring.datasource.password=5l6LoHoDiI \
  jwt.secret=mySecretKeyForJWTTokenGenerationThatIsAtLeast256BitsLong2024

# Secrets travel-service
vault kv put travel-backend/travel-service \
  spring.datasource.password=5l6LoHoDiI \
  spring.rabbitmq.password=guest \
  jwt.secret=mySecretKeyForJWTTokenGenerationThatIsAtLeast256BitsLong2024

# Secrets payment-service
vault kv put travel-backend/payment-service \
  spring.datasource.password=5l6LoHoDiI \
  spring.rabbitmq.password=guest \
  jwt.secret=mySecretKeyForJWTTokenGenerationThatIsAtLeast256BitsLong2024

# Secrets notification-service
vault kv put travel-backend/notification-service \
  spring.datasource.password=5l6LoHoDiI \
  spring.rabbitmq.password=guest \
  jwt.secret=mySecretKeyForJWTTokenGenerationThatIsAtLeast256BitsLong2024

# Secrets search-service
vault kv put travel-backend/search-service \
  spring.elasticsearch.password=lbB07FlWk4MDeGYx \
  spring.rabbitmq.password=guest \
  jwt.secret=mySecretKeyForJWTTokenGenerationThatIsAtLeast256BitsLong2024

# Secrets api-gateway
vault kv put kv/api-gateway \
  jwt.secret=mySecretKeyForJWTTokenGenerationThatIsAtLeast256BitsLong2024

echo "Tous les secrets ont ete injectes avec succes !"
