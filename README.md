# Pet Service Booking Management System MVP

Production-style 8-hour MVP skeleton for a pet grooming booking system.

## Stack

- Frontend: React, TypeScript, Vite, React Router, Axios, TanStack Query, React Hook Form, Zod, TailwindCSS
- Backend: Java 21, Spring Boot 3, Spring Web, Spring Data JPA, Spring Security, PostgreSQL, Lombok, Validation, Flyway
- Database: PostgreSQL via Docker Compose

## Project Structure

```text
pet-service-booking/
  backend/
    src/main/java/com/example/petservice/
      config/
      auth/
      user/
      pet/
      servicecatalog/
      booking/
      common/
    src/main/resources/db/migration/
    src/main/resources/application.yml
    pom.xml
  frontend/
    src/
      app/
      routes/
      features/
      shared/
    package.json
  docker-compose.yml
  README.md
```

## Setup

### 1. Start PostgreSQL

```bash
cd pet-service-booking
docker compose up -d
```

### 2. Run Backend

```bash
cd backend
./mvnw spring-boot:run
```

If Maven Wrapper is not available locally:

```bash
mvn spring-boot:run
```

The API runs at `http://localhost:8080`.

### 3. Run Frontend

```bash
cd frontend
npm install
npm run dev
```

The app runs at `http://localhost:5173`.

## Sample Accounts

All sample accounts use password `Password123!`.

| Role | Email |
| --- | --- |
| CUSTOMER | `customer@example.com` |
| STAFF | `staff@example.com` |
| ADMIN | `admin@example.com` |

## API Overview

Auth:

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/auth/me`

Bookings:

- `POST /api/bookings`
- `GET /api/bookings`
- `GET /api/bookings/{id}`
- `PATCH /api/bookings/{id}/assign`
- `PATCH /api/bookings/{id}/status`

Pets:

- `POST /api/pets`
- `GET /api/pets`
- `PUT /api/pets/{id}`
- `DELETE /api/pets/{id}`
- `POST /api/pets/{id}/photo`

Services:

- `GET /api/services`

Staff:

- `GET /api/staff`

## Authorization Rules

- `CUSTOMER` users can create bookings for their own pets and view their own bookings.
- `STAFF` users can view assigned bookings and update assigned booking status.
- `ADMIN` users can view all bookings and assign staff.
- Role restrictions are enforced server-side. Frontend route guards only improve UX.

## Implemented

- JWT authentication with Spring Security
- Layered backend packages with controllers, services, repositories, DTOs, mappers, and entities
- Flyway migrations for schema and sample data
- Global exception handling with clean JSON errors
- React route protection and role guards
- Axios auth interceptor
- TanStack Query data fetching
- React Hook Form and Zod validation
- Dashboard-style pages for customer, staff, and admin workflows

## Intentionally Excluded

- Payments
- Chat
- Notifications
- Maps or routing
- WebSockets
- Redis, Kafka, Kubernetes, or microservices
- Full staff scheduling and availability logic
- Production secret management beyond environment variable hooks

## Useful Environment Variables

Backend:

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `JWT_SECRET`
- `JWT_EXPIRATION_MINUTES`
- `CORS_ALLOWED_ORIGINS`
- `PET_PHOTOS_DIR`, defaults to `uploads/pets`

Frontend:

- `VITE_API_URL`, defaults to `http://localhost:8080/api`
