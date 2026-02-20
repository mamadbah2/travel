#!/bin/bash

# Couleurs pour la lisibilité
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${BLUE}=== Lancement des Tunnels Travel (Dev Environment) ===${NC}"

# Fonction pour nettoyer les processus quand on fait CTRL+C
cleanup() {
    echo -e "\n${RED}🛑 Arrêt de tous les tunnels...${NC}"
    # Tue tous les jobs lancés par ce script
    kill $(jobs -p) 2>/dev/null
    echo -e "${GREEN}✅ Tout est coupé. Bye !${NC}"
}

# Intercepte le signal SIGINT (Ctrl+C) et exécute cleanup
trap cleanup SIGINT SIGTERM

# --- Lancement des tunnels (avec & pour les mettre en parallèle) ---

echo -e "${GREEN}🐘 Postgres${NC} (5432)"
kubectl port-forward svc/postgres-travel-postgresql 5432:5432 -n travel &

echo -e "${GREEN}🔐 Vault${NC} (8200)"
kubectl port-forward svc/vault 8200:8200 -n travel &

echo -e "${GREEN}🐰 RabbitMQ${NC} (5672, 15672)"
kubectl port-forward svc/rabbitmq 5672:5672 15672:15672 -n travel &

echo -e "${GREEN}📧 MailDev${NC} (1025, 1080)"
kubectl port-forward svc/maildev 1025:1025 1080:1080 -n travel &

echo -e "${GREEN}🔍 Elasticsearch${NC} (9200)"
kubectl port-forward svc/elasticsearch-master 9200:9200 -n travel &

# --- Attente ---
echo -e "${BLUE}======================================================${NC}"
echo -e "${BLUE}🚀 Tunnels actifs ! Les logs s'afficheront ci-dessous.${NC}"
echo -e "${BLUE}⌨️  Appuie sur [CTRL+C] pour tout arrêter.${NC}"
echo -e "${BLUE}======================================================${NC}"

# La commande wait empêche le script de se terminer tout seul
wait