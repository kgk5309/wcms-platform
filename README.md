# WCMS Platform

DID-centered WCMS SaaS platform.

## Repository Layout

```text
backend/   Spring Boot services and shared backend modules
frontend/  React applications
infra/     Local and deployment infrastructure configuration
docs/      Product, architecture, and implementation context
```

## Baseline

- Backend: Spring Boot, Java 17, MSA, JPA
- Database: MariaDB single instance with service-level logical schemas
- Security: Spring Security, JWT, Redis
- Messaging: RabbitMQ
- Routing: Nginx
- Frontend: React

## Local Development

See [docs/development/LOCAL_DOCKER.md](docs/development/LOCAL_DOCKER.md) for Docker Compose startup, local auth account, and auth API smoke-test commands.
