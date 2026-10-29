# Java Backend API with Authentication and Notes Management

A secure RESTful API built with **Spring Boot 3.5** and **Java 24**. Users can register and
log in with **JWT authentication**, then manage their personal notes through protected
CRUD endpoints. Data is persisted in **PostgreSQL**.

## Features

- User registration with **BCrypt** password hashing
- Login that returns a **signed JWT** for session management
- Protected `Note` CRUD endpoints — only accessible with a valid `Authorization: Bearer <token>` header, and users can only see their own notes
- Role-based access control (`USER` / `ADMIN`) via `@PreAuthorize`
- Unit tests with JUnit 5 + Mockito
- Docker support

## Tech stack

Spring Web · Spring Data JPA · Spring Security · PostgreSQL · H2 (tests) · jjwt · Maven

## Prerequisites

- **Java 24** JDK
- **PostgreSQL** running locally (or a reachable instance)
- No Maven install needed — the project ships with the Maven wrapper (`mvnw`)

## Setup

### 1. Create the database

```sql
CREATE DATABASE notesdb;
```

Then run [`schema.sql`](schema.sql) against it:

```powershell
psql -U postgres -d notesdb -f schema.sql
```

(Hibernate also auto-creates/updates the schema via `ddl-auto=update`, so the script is optional.)

### 2. Configure the connection

Paste your PostgreSQL connection string into
[`src/main/resources/application.yml`](src/main/resources/application.yml):

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/notesdb
    username: postgres
    password: <your-password>
```

Every value can also be overridden with environment variables:
`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`, `JWT_EXPIRATION_MS`.

### 3. Run

```powershell
.\mvnw.cmd spring-boot:run
```

The API is available at `http://localhost:8080`.

### Tests

```powershell
.\mvnw.cmd test
```

Tests run against an in-memory **H2** database — no PostgreSQL needed.

## API endpoints

| Method | Path                | Auth        | Description                     |
|--------|---------------------|-------------|---------------------------------|
| POST   | `/api/auth/register` | public      | Register a new user             |
| POST   | `/api/auth/login`    | public      | Log in, returns `{ "token" }`   |
| GET    | `/api/notes`         | Bearer JWT  | List your notes                 |
| GET    | `/api/notes/{id}`    | Bearer JWT  | Get one of your notes           |
| POST   | `/api/notes`         | Bearer JWT  | Create a note                   |
| PUT    | `/api/notes/{id}`    | Bearer JWT  | Update one of your notes        |
| DELETE | `/api/notes/{id}`    | Bearer JWT  | Delete one of your notes        |
| GET    | `/api/admin/users`   | Bearer JWT + `ADMIN` role | List all users |

### Try it out

```powershell
# Register (pass "role": "ADMIN" to create an administrator for the bonus endpoint)
curl -X POST http://localhost:8080/api/auth/register `
  -H "Content-Type: application/json" `
  -d '{ "username": "ajeet", "email": "ajeet@example.com", "password": "secret123" }'

# Login -> copy the token from the response
curl -X POST http://localhost:8080/api/auth/login `
  -H "Content-Type: application/json" `
  -d '{ "username": "ajeet", "password": "secret123" }'

# Create a note with the JWT
curl -X POST http://localhost:8080/api/notes `
  -H "Content-Type: application/json" `
  -H "Authorization: Bearer <token>" `
  -d '{ "title": "First note", "content": "Hello, notes!" }'
```

## Docker

```powershell
docker build -t notes-api .
docker run -p 8080:8080 `
  -e DB_URL=jdbc:postgresql://host.docker.internal:5432/notesdb `
  -e DB_USERNAME=postgres -e DB_PASSWORD=<your-password> `
  notes-api
```

(`host.docker.internal` lets the container reach a PostgreSQL instance running on your machine.)

### Run on H2 instead (no PostgreSQL needed)

```powershell
docker run -p 8080:8080 -e SPRING_PROFILES_ACTIVE=h2 notes-api
# or locally:
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=h2"
```

With the `h2` profile the app uses an in-memory H2 database and the console is
available at `http://localhost:8080/h2-console`
(JDBC URL `jdbc:h2:mem:notesdb`, user `sa`, empty password).

## Project layout

```
src/main/java/org/ajeet
├── controller/   # AuthController, NoteController, AdminController
├── dto/          # request/response records
├── model/        # User, Note, Role (JPA entities)
├── repository/   # UserRepository, NoteRepository
├── security/     # SecurityConfig, JwtAuthFilter, JwtService, CustomUserDetailsService
└── service/      # AuthService, NoteService
```
