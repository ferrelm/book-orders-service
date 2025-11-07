#!/usr/bin/env bash
set -euo pipefail

# Usage: check-actuator.sh <profile> [--no-down]
# Examples:
#   ./scripts/check-actuator.sh dev
#   ./scripts/check-actuator.sh prod --no-down

PROFILE=${1:-dev}
NO_DOWN=false
if [ "${2:-}" = "--no-down" ]; then
  NO_DOWN=true
fi

echo "Starting docker-compose with profile=$PROFILE"
SPRING_PROFILES_ACTIVE=$PROFILE docker-compose up -d --build

echo "Waiting for /actuator/health (profile=$PROFILE)"
URL=http://localhost:8080/actuator/health
for i in $(seq 1 30); do
  code=$(curl -s -o /dev/null -w "%{http_code}" "$URL" || true)
  if [ "$code" = "200" ]; then
    echo "[$PROFILE] actuator healthy (HTTP $code)"
    RESULT=0
    break
  fi
  echo -n "."
  sleep 1
done
echo

if [ -z "${RESULT+x}" ]; then
  code=$(curl -s -o /dev/null -w "%{http_code}" "$URL" || true)
  echo "[$PROFILE] actuator check finished: HTTP $code"
  RESULT=1
fi

if [ "$NO_DOWN" = false ]; then
  echo "Tearing down compose stack"
  docker-compose down -v
else
  echo "Leaving compose stack running (no down)"
fi

exit $RESULT
