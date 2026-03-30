#!/bin/sh
# ================================================================
#  Initialisation SonarQube : Quality Gate "Regles-Souples"
#  Container one-shot exécuté après le démarrage de SonarQube
# ================================================================

SONAR_URL="http://sonarqube:9000"
AUTH="admin:admin"
GATE_NAME="Regles-Souples"

echo "==> Création du Quality Gate '${GATE_NAME}'..."

# Créer le Quality Gate (ignore l'erreur si déjà existant)
curl -sf -u "$AUTH" -X POST \
  "${SONAR_URL}/api/qualitygates/create" \
  -d "name=${GATE_NAME}" >/dev/null 2>&1

# Vérifier que le gate existe
EXISTS=$(curl -sf -u "$AUTH" "${SONAR_URL}/api/qualitygates/list" \
  | grep -o "\"name\":\"${GATE_NAME}\"")

if [ -z "$EXISTS" ]; then
  echo "==> ERREUR: impossible de créer ou trouver le Quality Gate"
  exit 1
fi

echo "==> Quality Gate '${GATE_NAME}' trouvé/créé avec succès"

# Ajouter des conditions souples (SonarQube 10 utilise gateName)
echo "==> Ajout des conditions souples..."

# Coverage > 0% (très permissif)
curl -sf -u "$AUTH" -X POST "${SONAR_URL}/api/qualitygates/create_condition" \
  -d "gateName=${GATE_NAME}" \
  -d "metric=new_coverage" \
  -d "op=LT" \
  -d "error=0" >/dev/null 2>&1 || echo "  (condition coverage déjà existante)"

# Duplication < 50% (très permissif)
curl -sf -u "$AUTH" -X POST "${SONAR_URL}/api/qualitygates/create_condition" \
  -d "gateName=${GATE_NAME}" \
  -d "metric=new_duplicated_lines_density" \
  -d "op=GT" \
  -d "error=50" >/dev/null 2>&1 || echo "  (condition duplication déjà existante)"

# Définir comme gate par défaut
echo "==> Définition comme Quality Gate par défaut..."
curl -sf -u "$AUTH" -X POST "${SONAR_URL}/api/qualitygates/set_as_default" \
  -d "name=${GATE_NAME}" >/dev/null 2>&1 || echo "  (déjà défini par défaut)"

# ── Génération du token pour Jenkins ──
echo "==> Génération du token SonarQube pour Jenkins..."

# Révoquer l'ancien token s'il existe
curl -sf -u "$AUTH" -X POST "${SONAR_URL}/api/user_tokens/revoke" \
  -d "name=jenkins-ci" >/dev/null 2>&1 || true

# Créer un nouveau token
TOKEN_RESPONSE=$(curl -sf -u "$AUTH" -X POST "${SONAR_URL}/api/user_tokens/generate" \
  -d "name=jenkins-ci")

TOKEN_VALUE=$(echo "$TOKEN_RESPONSE" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

if [ -z "$TOKEN_VALUE" ]; then
  echo "==> ERREUR: impossible de générer le token"
  exit 1
fi

# Persister dans un volume partagé
echo "$TOKEN_VALUE" > /opt/sonarqube/data/jenkins-token.txt
chmod 644 /opt/sonarqube/data/jenkins-token.txt
echo "==> Token généré et sauvegardé dans /opt/sonarqube/data/jenkins-token.txt"

echo "==> Initialisation SonarQube terminée !"
