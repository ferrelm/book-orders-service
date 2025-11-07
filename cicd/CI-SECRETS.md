CI secrets and checklist

Purpose
This document lists the credentials and secrets the Jenkins pipeline expects and provides a short checklist to configure your CI environment safely. Do NOT store real secrets in the repo.

Required secrets (names shown are examples used in `cicd/jenkins/Jenkinsfile`)
- `aws_credentials` (Jenkins credential)
  - AWS access key ID / secret access key pair or an IAM role binding. Used for ECR login and Terraform.
  - Minimum permissions: ECR push/pull, STS:AssumeRole if using role chaining, and the specific infra operations needed by Terraform.

- `aws_ecr_registry` (plain-text string credential)
  - ECR registry hostname (e.g. 123456789012.dkr.ecr.us-east-1.amazonaws.com).

- `maven_settings` (Config File credential)
  - Maven `settings.xml` with `<servers>` entries for private Maven feeds if required.
  - Use Jenkins Config File Provider to inject the file during the build.

- `maven_repo_creds` (username/password credential)
  - Optional: a username/password pair for private Maven repo access (if not embedded in `settings.xml`).

- `datadog_api_key` (string credential)
  - Datadog API key for integrations (only if `datadog_enabled: true`). Keep this secret in the CI store or vault.

- `sonar_token` (string credential) — optional
  - Token for SonarQube scans (if you enable Sonar in the pipeline).

- `kubeconfig` or `kube_credentials` (file / credential)
  - If the pipeline uses `kubectl` directly from Jenkins agents, provide a Kubeconfig file or Jenkins Kubernetes plugin credentials to permit `helm` / `kubectl` operations.

- `terraform_state_credentials` (optional)
  - If Terraform backend requires special credentials (S3, GCS, etc.), ensure they are available to the pipeline (via environment variables or IAM role the agent assumes).

- `auth_service_account` (optional)
  - If you need to call internal auth endpoints or seed tokens during smoke tests, provide a service account/token with least privilege.

Notes on secrets and artifacts
- Do NOT commit secrets to the repo. Use Jenkins Credentials, HashiCorp Vault, or your cloud provider's secret manager. Document the credential IDs clearly in `cicd/secrets.example.yaml`.
- Keep Datadog or any monitoring keys scoped and rotated regularly.
- Ensure private Maven feeds are reachable from CI runners and the `settings.xml` contains only the required credentials (no hardcoded plaintext in the build scripts).

Checklist to configure pipeline (quick)
1. Add credentials to Jenkins (or your CI secrets store)
   - Create credentials with the IDs used in `cicd/jenkins/Jenkinsfile` (or update the Jenkinsfile to match your existing IDs):
     - `aws_credentials`, `aws_ecr_registry`, `maven_settings`, `maven_repo_creds`, `datadog_api_key`, `sonar_token`, `kubeconfig`.
2. Validate AWS permissions
   - From the Jenkins agent, verify `aws sts get-caller-identity` works with the provided `aws_credentials`.
   - Verify ECR login works:
     - `aws ecr get-login-password --region <region> | docker login --username AWS --password-stdin <account>.dkr.ecr.<region>.amazonaws.com`
3. Validate Maven access
   - Run `mvn -s <settings.xml> dependency:resolve` on an agent using the injected `maven_settings` to confirm private repos are reachable.
4. Validate Docker & ECR publish
   - From an agent, build a small image and push to the target ECR registry with the provided creds.
5. Validate Kubernetes access
   - Confirm `kubectl`/`helm` commands with the provided `kubeconfig` can list namespaces and perform a dry-run upgrade:
     - `helm upgrade --install --dry-run --debug <release> ./helm -f cicd/variables/common.yaml -f cicd/variables/dev.yaml`
6. Validate Terraform (if used)
   - Ensure the Terraform backend is reachable and `terraform init` + `plan` works from the agent with the provided credentials.
7. Validate Sonar (optional)
   - If you plan Sonar scans, add `sonar_token` and run a local scan to validate the token and permissions.
8. Smoke test authorization and actuator access
   - After a deploy to `dev`, run the smoke checks (actuator `/actuator/health` and a simple API GET) using the configured `auth_service_account` or dev token.
9. Confirm Datadog setup
   - Ensure `datadog_api_key` is configured and that the agent or image instrumentation is applied according to your image build policy.
10. Document credential rotation & owners
   - Put a quick note in your `cicd/README.md` about who owns which credentials and a rotation schedule.

Extra recommendations
- Use short-lived credentials or IAM roles for agents whenever possible.
- Prefer injecting secrets as environment variables or mounted files rather than writing them into built images.
- Use sealed secrets or KMS-encrypted secrets for any values that need to be stored in git (rare cases).

If you want, I can:
- Create a Jenkins credential mapping snippet or a `cicd/jenkins/credentials-setup.md` with CLI commands to create the required Jenkins credentials programmatically.
- Or open a branch with the `CI-SECRETS.md` file committed. 
