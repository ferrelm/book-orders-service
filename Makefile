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
