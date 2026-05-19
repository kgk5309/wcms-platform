# WCMS Platform

DID 중심 WCMS SaaS 플랫폼.

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
