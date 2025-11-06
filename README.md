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
