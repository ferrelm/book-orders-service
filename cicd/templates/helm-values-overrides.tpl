# Template: merge common + env values when deploying via CI
# Usage: the pipeline should pass -f cicd/variables/common.yaml -f cicd/variables/${ENV}.yaml

# Example override keys you may want to set dynamically:
image:
  repository: ${ECR_REGISTRY}/book-orders-service
  tag: ${IMAGE_TAG}

replicaCount: 1
resources:
  limits:
    cpu: 500m
    memory: 512Mi

# Health probes should target the app's actuator endpoints; ensure the port matches your container's management port
livenessProbe:
  httpGet:
    path: /actuator/health
    port: 8081

# CI should not substitute secrets into values files; use Kubernetes secrets or sealed secrets for credentials.
