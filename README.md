# CloudTrim

CloudTrim is a modular Spring Boot SaaS for multi-tenant cloud waste detection and FinOps reporting.

## Architecture

```text
Browser / Tailwind dashboard
            |
       Spring MVC REST
            |
Spring Security + JWT + RBAC
            |
Scanner -> Waste Rules -> JPA
            |
        PostgreSQL 16
```

The application uses tenant-scoped repositories and JWT tenant claims. Administrative actions require `ADMIN`; viewing requires authentication.

## Run locally

```bash
docker compose up -d postgres
export JAVA_HOME=/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home
mvn spring-boot:run
```

Open `http://localhost:8080`, Swagger at `http://localhost:8080/swagger-ui.html`, and the health endpoint at `http://localhost:8080/api/health`.

## Live demo

Click **Launch Live Demo**. CloudTrim creates an isolated demo tenant and user, scans 35 representative AWS/Azure-style resources, runs waste rules, and refreshes executive KPIs.

## Production

Set:

- `DATABASE_URL`
- `DATABASE_USERNAME`
- `DATABASE_PASSWORD`
- `JWT_SECRET`
- `JWT_EXPIRATION_MINUTES`

Run:

```bash
mvn package -DskipTests
docker build -t cloudtrim .
docker run --env-file .env -p 8080:8080 cloudtrim
```

## Cost model

Monthly detected waste equals the sum of open finding estimates. Annual waste equals monthly waste multiplied by 12. Realized savings are the monthly waste value of resolved findings. USD/INR conversion is configurable with `USD_INR`.
