#!/usr/bin/env bash
set -euo pipefail

# Usage: deploy-helm.sh <env> <image>
ENV=${1:?"env required (dev/pre/prod)"}
IMAGE=${2:?"image required"}
RELEASE_NAME=book-orders-service
CHART_DIR=helm
NAMESPACE=default

VALUES_FILE="cicd/variables/${ENV}.yaml"

if [[ ! -f "${VALUES_FILE}" ]]; then
  echo "Values file not found: ${VALUES_FILE}" >&2
  exit 2
fi

# Merge common values with env values (yq required). If yq not available, Helm supports multiple -f flags.
helm upgrade --install "${RELEASE_NAME}" "${CHART_DIR}" \
  --namespace "${NAMESPACE}" --create-namespace \
  --set image.repository=$(echo "${IMAGE}" | cut -d':' -f1) \
  --set image.tag=$(echo "${IMAGE}" | cut -d':' -f2) \
  -f cicd/variables/common.yaml -f "${VALUES_FILE}"

# Optionally wait
helm rollout status deployment/${RELEASE_NAME} -n ${NAMESPACE} --timeout 2m

echo "Helm deploy complete for ${ENV} using image ${IMAGE}"
