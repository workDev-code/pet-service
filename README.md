# Pet Service Booking Management System MVP

Production-style MVP for a pet grooming booking system with role-based customer, staff, and admin workflows.

## Stack

- Frontend: React, TypeScript, Vite, React Router, Axios, TanStack Query, React Hook Form, Zod, TailwindCSS
- Backend: Java 21, Spring Boot 3, Spring Web, Spring Data JPA, Spring Security, PostgreSQL, Lombok, Validation, Flyway
- Database: PostgreSQL via Docker Compose, Flyway migrations

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

### 1. Start PostgreSQL and pgAdmin

```bash
cd pet-service-booking
docker compose up -d
```

PostgreSQL is exposed on `localhost:5433`.
pgAdmin is available at `http://localhost:5051`.

Default pgAdmin login:

- Email: `admin@admin.com`
- Password: `admin`

### 2. Run Backend

```bash
cd backend
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

### 4. Run Checks

```bash
cd backend
mvn test
```

```bash
cd frontend
npm run build
```

## Sample Accounts

All sample accounts use password `Password123!`.

| Role | Email |
| --- | --- |
| CUSTOMER | `customer@example.com` |
| STAFF | `staff@example.com` |
| ADMIN | `admin@example.com` |

Additional sample staff and customer accounts are seeded by Flyway migrations. They also use `Password123!`.

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
- `DELETE /api/bookings/{id}`

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

Admin users:

- `GET /api/admin/users?role=CUSTOMER`
- `GET /api/admin/users?role=STAFF`
- `POST /api/admin/users`
- `PUT /api/admin/users/{id}`
- `DELETE /api/admin/users/{id}`
- `GET /api/admin/users/deleted`
- `PATCH /api/admin/users/{id}/restore`

## Authorization Rules

- `CUSTOMER` users can manage their own pets, upload pet photos, create bookings for their own pets, view their own bookings, and cancel their own active bookings.
- `STAFF` users can view bookings assigned to them and mark assigned bookings as `COMPLETED` or `CANCELLED`.
- `ADMIN` users can view all bookings, assign staff, and manage customer/staff accounts.
- `ADMIN` users can list deleted customer/staff accounts and restore them.
- `ADMIN` accounts cannot be created through public registration and cannot be deleted through normal admin user CRUD.
- Role restrictions are enforced server-side. Frontend route guards only improve UX.

## Delete Behavior

The system uses soft delete for important business records.

- `deleted_at IS NULL` means active.
- `deleted_at IS NOT NULL` means soft deleted.
- Normal list/get APIs exclude soft-deleted rows.
- Accessing a soft-deleted record by ID returns `404`.
- Soft-deleted users cannot login.
- Restored users can login again with their existing password.

Rules:

- Users are always soft-deleted in normal API flows.
- Staff cannot be deleted while they have active `PENDING` or `ASSIGNED` bookings.
- Bookings are always soft-deleted.
- Pets with booking history are soft-deleted.
- Pets without booking history are hard-deleted.
- Pet photo files are physically deleted when a pet is deleted or a photo is replaced. File cleanup is best-effort and does not rollback database changes.

## Postman

Import `postman_collection.json` to test the API.

Recommended flow:

1. Run `Auth > Login - Admin`, `Login - Customer`, or `Login - Staff` to set the collection bearer token.
2. Use the `Pets`, `Bookings`, `Staff`, and `Admin Users` folders.
3. For pet photo upload, choose a local image file in the `file` form-data field.

## Implemented

- JWT authentication with Spring Security
- Layered backend packages with controllers, services, repositories, DTOs, mappers, and entities
- Flyway migrations for schema and sample data
- Soft delete for users, pets, and bookings
- Customer pet CRUD and pet photo upload
- Admin customer and staff CRUD
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
