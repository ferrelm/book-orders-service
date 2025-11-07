Jenkins setup for book-orders-service

Prerequisites
- Jenkins master + agents with Docker, AWS CLI, Helm, Kubectl, and yq installed.
- Jenkins credentials configured as listed below.

Required Credentials (add these to Jenkins Credentials)
- aws_credentials - AWS IAM keys or role credential (used for ECR login and Terraform)
- maven_settings - Config File Credential (settings.xml) with access to private maven feeds
- maven_repo_creds - Username/Password for private Maven feed (if used separately)
- aws_ecr_registry - Plain text credential for the ECR registry URL (e.g. 123456789012.dkr.ecr.us-east-1.amazonaws.com)
- datadog_api_key - Datadog API key (string)

Notes
- The `Jenkinsfile` is a starter. You'll likely want to integrate with your org's shared library for ECR login / terraform helpers.
- Do NOT store secrets in repo.
- Make sure the Jenkins agent has permission to assume the AWS role or uses IAM keys with least privilege for ECR / Terraform.

Recommended agent image
- Ubuntu 22.04 with Docker, AWS CLI v2, Helm 3, kubectl, yq, and Maven installed.
