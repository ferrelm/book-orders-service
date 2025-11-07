# Book Orders Service
A demo microservice using Java 17, Spring Boot 3, MongoDB, OAuth2 (JWT), OpenAPI, Docker, Helm, and Terraform.

## Run locally
mvn spring-boot:run

## Build Docker image
docker build -t book-orders-service .
docker run -p 8080:8080 book-orders-service

Access API docs: http://localhost:8080/swagger-ui.html

## Deploy to Kubernetes
helm install book-orders ./helm

## Infrastructure (optional)
cd terraform && terraform init && terraform apply

## Generating OpenAPI code and building

This project keeps OpenAPI code generation behind a Maven profile. Use the provided Makefile targets to generate sources and build the project.

- Generate OpenAPI sources (writes to target/generated-sources/openapi):

```bash
make generate
```

- Compile locally:

```bash
make compile
```

- Build package (JAR):

```bash
make build
```

If you prefer to run maven directly, the equivalent commands are:

```bash
mvn -Popenapi-generate -DskipTests generate-sources
mvn -DskipTests clean compile
```

## Docker quick start (compose)

You can start MongoDB and the app together using docker-compose and the Makefile targets included in this repo.

- Bring the stack up (build the app image and start services):

```bash
make docker-up
```

This runs `docker-compose up -d --build`. The compose file starts `mongo` and `app`. The app is configured to connect to Mongo at the service hostname `mongo`.

- Tail logs:

```bash
docker-compose logs -f
```

- Tear down the stack and remove volumes:

```bash
make docker-down
```

Notes:
- If you already have a container named `mongo` (for example you started one with `docker run --name mongo ...`), stop/remove it before running `make docker-up` or change the port mapping in `docker-compose.yml`.
- The app will use environment variables set in the compose file to point to Mongo; change them if you need a different DB host or credentials.
- The app by default validates JWTs using the issuer URI in `application.yml`. For quick local testing you may want a dev profile that disables security (I can add one on request).

Actuator & example curl commands
--------------------------------

When running via `docker-compose` the app is started with the `dev` profile which exposes the actuator `health` and `info` endpoints without requiring authentication.

Override the profile: the compose file uses environment substitution so the profile is configurable without editing the file:

```yaml
# in docker-compose.yml
SPRING_PROFILES_ACTIVE: ${SPRING_PROFILES_ACTIVE:-dev}
```

Examples to override at runtime:

```bash
# start compose with a different profile (inline override)
SPRING_PROFILES_ACTIVE=prod docker-compose up -d --build

# or use the Makefile helper I added:
make docker-up-profile PROFILE=prod
```

Unauthenticated (dev) actuator health:

```bash
# returns HTTP 200 when app is up
curl -sS -o /dev/null -w "%{http_code}" http://localhost:8080/actuator/health
```

Authenticated API example (when security is enabled):

```bash
# Replace <TOKEN> with a valid bearer JWT
curl -sS -H "Authorization: Bearer <TOKEN>" http://localhost:8080/api/orders
```

There is also a Makefile target to wait for a healthy app:

```bash
make smoke
```

This will poll the actuator health endpoint for up to ~30s and return success when it becomes available.

Dev-only test token
-------------------

For quick local testing the `dev` profile includes a tiny development JwtDecoder that accepts a fixed token value. When running the app with the `dev` profile you can use the header:

```http
Authorization: Bearer dev-token
```

Example calls using the dev token:

```bash
# List orders (no external IdP required when running with 'dev')
curl -H "Authorization: Bearer dev-token" http://localhost:8080/api/orders

# Create an order
curl -X POST -H "Content-Type: application/json" -H "Authorization: Bearer dev-token" \
	-d '{"customerName":"Alice","bookTitle":"1984","quantity":1,"status":"NEW"}' \
	http://localhost:8080/api/orders
```

Security note: `dev-token` is intentionally insecure and only available when the `dev` profile is active (the repo includes a dev-only config class that registers this decoder). Do NOT use this token or enable this behavior in shared/production environments.

Implementation note: the dev Jwt decoder is implemented in `src/main/java/com/example/bookorders/config/DevJwtConfig.java` — see that file if you want to change the token behavior or claims used for local testing.


Script: scripts/check-actuator.sh
---------------------------------

A small helper script is included at `scripts/check-actuator.sh` that automates bringing the compose stack up for a given Spring profile, polling the `\/actuator\/health` endpoint until it returns HTTP 200 (or a timeout is reached), and optionally tearing the stack down.

Key behavior:

- Starts the compose stack with the requested profile (defaults to `dev` if not specified).
- Polls `http://localhost:8080/actuator/health` for up to ~30s and prints the final HTTP status code.
- Exits with success (0) when HTTP 200 is observed, or non-zero on timeout / non-200.
- By default the script will bring the stack down after the check; pass `--no-down` to leave the containers running for manual inspection.

Usage examples:

```bash
# start, check actuator for dev, then tear down
./scripts/check-actuator.sh dev

# start, check actuator for prod, and leave containers running (useful for debugging)
./scripts/check-actuator.sh prod --no-down

# convenience Makefile targets
make test-actuator-dev    # runs the script for the dev profile
make test-actuator-prod   # runs the script for the prod profile
```

This script is handy for CI smoke checks or quick local verification that a profile exposes (or protects) the actuator endpoints as expected.


Recommended profiles
--------------------

A couple of profiles are useful while developing and deploying:

- dev — local development. This profile (the default when using the included compose file) exposes the actuator `health` and `info` endpoints so you can quickly check app readiness. The repository provides a small dev security configuration that permits unauthenticated access to those management endpoints while keeping the application's API endpoints protected by the resource-server configuration. Use `dev` for local debugging and fast feedback.

- prod — production. Use this profile for deployed environments. Actuator endpoints should not be exposed unauthenticated in production; full security and production-grade settings (secrets, monitoring, DB connections, etc.) should be used.

How to run with a specific profile
---------------------------------

You can override the profile when starting the compose stack, for example:

```bash
# start compose with prod profile
SPRING_PROFILES_ACTIVE=prod docker-compose up -d --build

# or use the Makefile helper
make docker-up-profile PROFILE=prod
```

Keep `dev` as the default in the compose file to make local development frictionless, but always review what each profile enables before using it in shared or CI environments.

