#!/usr/bin/env bash
set -euo pipefail

# Usage: publish-image.sh IMAGE [: tag included] [DATADOG_API_KEY]
IMAGE=${1:?"IMAGE required (e.g. 123..dkr.ecr.us-east-1.amazonaws.com/book-orders-service:tag)"}
DD_API_KEY=${2:-}

echo "Logging in to ECR"
# The pipeline should run on a node with AWS CLI configured. Use assume-role or env creds in CI.
ACCOUNT=$(echo "${IMAGE}" | cut -d'.' -f1)
REGION=$(echo "${IMAGE}" | cut -d'.' -f4)
aws ecr get-login-password --region "${REGION}" | docker login --username AWS --password-stdin "${ACCOUNT}.dkr.ecr.${REGION}.amazonaws.com"

echo "Building docker image ${IMAGE}"
docker build --pull -t "${IMAGE}" .

echo "Pushing image to ECR"
docker push "${IMAGE}"

echo "Image published: ${IMAGE}"

if [[ -n "${DD_API_KEY}" ]]; then
  echo "Datadog API key provided; ensure image is annotated or registry sidecars are configured to collect traces/logs."
fi
