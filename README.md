# Book Orders Service

A demo REST microservice for managing book orders, built with Java 17 and Spring Boot 3.

## Table of Contents

- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Project Structure](#project-structure)
- [Building](#building)
- [Running Locally](#running-locally)
- [Docker Compose](#docker-compose)
- [API Reference](#api-reference)
- [Authentication](#authentication)
- [Spring Profiles](#spring-profiles)
- [Kubernetes](#kubernetes)
- [Infrastructure](#infrastructure)
- [Code Quality](#code-quality)

---

## Tech Stack

| Layer            | Technology                              |
| ---------------- | --------------------------------------- |
| Language         | Java 17                                 |
| Framework        | Spring Boot 3.3, Spring Cloud OpenFeign |
| Database         | MongoDB 6                               |
| Security         | Spring OAuth2 Resource Server (JWT)     |
| API Docs         | OpenAPI 3 / Swagger UI (springdoc)      |
| Mapping          | MapStruct                               |
| Scheduling       | ShedLock (MongoDB provider)             |
| Containerization | Docker, Docker Compose                  |
| Deployment       | Helm 3                                  |
| Infrastructure   | Terraform                               |

---

## Prerequisites

- Java 17+
- Maven 3.8+
- Docker & Docker Compose (for containerized runs)
- `make` (optional, for Makefile targets)

---

## Project Structure

```
src/main/java/com/example/bookorders/
├── client/         # Feign HTTP clients (BookClient)
├── config/         # Security and dev-profile configuration
├── controller/     # REST controllers (OrderController)
├── mapper/         # MapStruct mappers
├── model/          # Domain model and DTOs
├── repository/     # MongoDB repositories
└── service/        # Business logic
src/main/resources/
├── application.yml           # Base configuration
├── application-dev.yml       # Dev profile overrides
└── openapi/book-orders-api.yaml
```

---

## Building

OpenAPI code generation is behind a dedicated Maven profile. Use the Makefile targets for common tasks:

```bash
# Generate OpenAPI sources → target/generated-sources/openapi
make generate

# Compile (skip tests)
make compile

# Package JAR (skip tests)
make build

# Clean
make clean
```

Equivalent Maven commands:

```bash
mvn -Popenapi-generate -DskipTests generate-sources
mvn -DskipTests clean compile
mvn -DskipTests clean package
```

---

## Running Locally

**Option 1 — Maven (requires a running MongoDB on localhost:27017):**

```bash
mvn spring-boot:run
```

**Option 2 — Docker Compose (recommended):** see [Docker Compose](#docker-compose).

Swagger UI is available at <http://localhost:8080/swagger-ui.html> once the app is running.

---

## Docker Compose

The compose file starts both MongoDB and the application. The app defaults to the `dev` Spring profile.

```bash
# Build and start all services
make docker-up

# Tail logs
docker-compose logs -f

# Wait for the app actuator to report healthy (~30 s timeout)
make smoke

# Tear down and remove volumes
make docker-down
```

**Override the Spring profile at startup:**

```bash
# Inline environment override
SPRING_PROFILES_ACTIVE=prod docker-compose up -d --build

# Makefile helper
make docker-up-profile PROFILE=prod
```

> **Note:** If you have an existing container named `mongo`, stop it first or adjust the port mapping in `docker-compose.yml` before running `make docker-up`.

### Actuator health check script

`scripts/check-actuator.sh` brings the compose stack up for a given profile, polls `/actuator/health` until HTTP 200 (up to ~30 s), and then tears the stack down.

```bash
# Start, check, then tear down
./scripts/check-actuator.sh dev

# Start, check, and leave containers running for inspection
./scripts/check-actuator.sh prod --no-down

# Makefile shortcuts
make test-actuator-dev
make test-actuator-prod
```

---

## API Reference

Base path: `/api/orders`

| Method | Path          | Description        |
| ------ | ------------- | ------------------ |
| `GET`  | `/api/orders` | List all orders    |
| `POST` | `/api/orders` | Create a new order |

**Create order — request body:**

```json
{
  "customerName": "Alice",
  "bookTitle": "1984",
  "quantity": 1,
  "status": "NEW"
}
```

**Actuator endpoints** (unauthenticated on `dev` profile):

```
GET /actuator/health
GET /actuator/info
```

---

## Authentication

The service is an OAuth2 resource server that validates Bearer JWTs. The issuer URI is configured in `application.yml`:

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: https://cognito-idp.<region>.amazonaws.com/<pool>
```

**Example authenticated request:**

```bash
curl -H "Authorization: Bearer <TOKEN>" http://localhost:8080/api/orders
```

### Dev-only token

The `dev` profile registers a permissive `JwtDecoder` (see [DevJwtConfig.java](src/main/java/com/example/bookorders/config/DevJwtConfig.java)) that accepts a fixed token so you can test without a real IdP:

```bash
# List orders
curl -H "Authorization: Bearer dev-token" http://localhost:8080/api/orders

# Create an order
curl -X POST \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer dev-token" \
  -d '{"customerName":"Alice","bookTitle":"1984","quantity":1,"status":"NEW"}' \
  http://localhost:8080/api/orders
```

> **Warning:** `dev-token` is intentionally insecure. Never enable the `dev` profile in shared or production environments.

---

## Spring Profiles

| Profile                    | Purpose                                                                   |
| -------------------------- | ------------------------------------------------------------------------- |
| `dev` (default in Compose) | Exposes actuator endpoints without auth; enables `dev-token` JWT decoder. |
| `prod`                     | Full security; actuator endpoints require authentication.                 |

---

## Kubernetes

Deploy with Helm:

```bash
helm install book-orders ./helm
```

The chart is located in `helm/`. Adjust `helm/values.yaml` for image tag, replica count, and environment-specific settings.

---

## Infrastructure

Terraform configuration for provisioning cloud resources is in `infra/terraform/`:

```bash
cd infra/terraform
terraform init
terraform apply
```

---

## Code Quality

**Checkstyle** rules are defined in `config/checkstyle/checkstyle.xml` and wired into the Maven build. The plugin is configured in report-only mode by default; violations will not fail the build unless enforcement is explicitly enabled.

**CI/CD** pipeline definitions and scripts live under `cicd/`. See [cicd/README.md](cicd/README.md) for details on the Jenkins pipeline, Helm deploy scripts, and SonarQube integration.

**Datadog APM (optional):** To attach the Datadog Java agent, obtain `dd-java-agent.jar` from official Datadog releases (do not commit it to source control) and configure it via `JAVA_TOOL_OPTIONS`:

```bash
JAVA_TOOL_OPTIONS="-javaagent:/opt/datadog/dd-java-agent.jar -Ddd.service=book-orders-service -Ddd.env=local"
```
