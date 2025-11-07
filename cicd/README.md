cicd/ — Jenkins pipeline and helpers

This folder contains starter files for a Jenkins-based CI/CD pipeline and helper scripts. It's a scaffold you should adapt to your org's Jenkins, credentials and security policies.

Overview
- `jenkins/Jenkinsfile` - Declarative pipeline that builds, runs tests, pushes image to ECR and runs Terraform + Helm for deploy.
- `jenkins/README.md` - Quick setup notes for Jenkins credentials and agents.
- `scripts/` - Small helper scripts: `build-and-test.sh`, `publish-image.sh`, `deploy-helm.sh`.
- `sonar/sonar-project.properties` - Minimal Sonar config used by the pipeline.
- `templates/helm-values-overrides.tpl` - Example showing how to merge environment variables/values for Helm deploys.
- `secrets.example.yaml` - Placeholders for CI secrets (DO NOT store real secrets here).

How to use
1. Add the Jenkins credentials listed in `cicd/jenkins/README.md` to your Jenkins credential store.
2. Update the `Jenkinsfile` to match your agent labels and any organizational steps.
3. Ensure the pipeline runner (Jenkins agent) has `docker`, `aws` CLI, `helm`, `kubectl`, and `yq` installed.
4. Use the scripts in `cicd/scripts/` as drop-in helpers or call their commands directly in your Jenkins pipeline.

Security & secrets
- Do NOT commit real secrets. Use the CI provider's secure store (Jenkins Credentials, Vault, etc.).
- `cicd/secrets.example.yaml` documents credential IDs and environment variables the pipeline expects.

Next steps
- If you want, I can open a branch with these files created and a sample commit/PR. Or I can adjust the Jenkinsfile to match your existing Jenkins shared libraries.
