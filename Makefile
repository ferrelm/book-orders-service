SHELL := /bin/bash

# Generate OpenAPI sources (writes to target/generated-sources/openapi)
.PHONY: generate
generate:
	mvn -Popenapi-generate -DskipTests generate-sources

# Build the project (skip tests by default for speed)
.PHONY: build
build:
	mvn -DskipTests clean package

.PHONY: compile
compile:
	mvn -DskipTests clean compile

.PHONY: clean
clean:
	mvn clean


.PHONY: docker-up
docker-up:
	docker-compose up -d --build

.PHONY: docker-up-profile
# Start the compose stack with a specific Spring profile (defaults to dev).
# Usage: make docker-up-profile PROFILE=prod
docker-up-profile:
	@PROFILE=${PROFILE:-dev}; \
	echo "Starting compose with SPRING_PROFILES_ACTIVE=$$PROFILE"; \
	SPRING_PROFILES_ACTIVE=$$PROFILE docker-compose up -d --build

.PHONY: test-actuator-dev
test-actuator-dev:
	@./scripts/check-actuator.sh dev

.PHONY: test-actuator-prod
test-actuator-prod:
	@./scripts/check-actuator.sh prod

.PHONY: docker-down
docker-down:
	docker-compose down -v

.PHONY: smoke
smoke:
	@echo "Waiting for app actuator /actuator/health to return 200..."
	@for i in $$(seq 1 30); do \
		code=$$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/actuator/health || true); \
		if [ "$${code}" = "200" ]; then echo "ok (200)"; exit 0; fi; \
		echo -n "."; sleep 1; \
	done; \
	echo; echo "timeout waiting for actuator health"; exit 1
