# Student Data Management with Spring Boot

A full-stack web application for managing student records per institution. Built with Java and Spring Boot, it provides a RESTful API with multi-tenant authentication — each institution registers separately and can only access its own student data.

## What This Project Shows

I built this to practice and demonstrate backend development skills:

- **Core Java & Spring Boot**: Built a complete standalone web application using Spring Boot's auto-configuration and embedded Tomcat.
- **REST API Development**: Designed and implemented RESTful endpoints with clear, logical structure.
- **Multi-Tenant Architecture**: Each institution has its own isolated data — students are scoped to an `institution_id` at the database level.
- **Custom Authentication**: Session-based auth using HTTP-only cookies and a `user_session` table, without relying on Spring Security's web layer — just `spring-security-crypto` for BCrypt password hashing.
- **Database Interaction**: Spring Data JPA + Hibernate for ORM. All queries are institution-scoped to prevent data leakage.
- **Layered Architecture**: Controller → Service → Repository separation throughout.
- **Data Validation**: Server-side validation on all incoming requests using Jakarta Bean Validation.
- **Error Handling**: Centralized `GlobalExceptionHandler` covering validation, not-found, and auth errors.
- **Secure Credential Management**: Database credentials loaded from environment variables via a `.env` file.
- **Frontend Pages**: A landing page, login page, registration page, and student management UI — all served as static HTML from Spring Boot.

## Design Patterns and Principles

- **Layered Architecture**: Controller handles HTTP, Service contains business logic, Repository manages data access.
- **Repository Pattern**: Data access is abstracted behind JPA repository interfaces.
- **DTO Pattern**: `StudentDTO` separates the API contract from the internal entity, preventing over-exposure of the database model.
- **Dependency Injection**: Spring's DI manages all component wiring.
- **Single Responsibility Principle**: Each class has one clear job.
- **Servlet Filter for Auth**: `AuthFilter` (extends `OncePerRequestFilter`) validates the session cookie on every protected request and injects the `institutionId` as a request attribute.

## Features

- **Institution Login System**: Institutions register with a name, email, and password. Passwords are stored as BCrypt hashes. On login, a UUID session token is saved to the database and set as an HTTP-only cookie.
- **Data Isolation**: Every student record is linked to its institution. One institution cannot see or modify another's students — enforced at the query level.
- **CRUD Operations**: Create, read, update, and delete student records.
- **Pagination**: Configurable page size for student listings.
- **Search**: Search students by name or email within the institution's own records.
- **Web UI**: A browser-based interface for day-to-day operations without touching the API directly.
- **Landing Page**: An overview page (`/landing.html`) that introduces the application.

## Technologies Used

- **Backend**: Java 17, Spring Boot, Spring Web MVC, Spring Data JPA, Hibernate
- **Auth**: `spring-security-crypto` (BCrypt only — no Spring Security web layer)
- **Database**: MySQL
- **Build Tool**: Maven
- **Frontend**: Vanilla HTML + CSS + JavaScript (no framework)

## Project Structure

```
src/main/
├── java/com/example/student/
│   ├── controller/
│   │   ├── AuthController.java       ← /auth/* endpoints
│   │   └── StudentController.java    ← /students/* endpoints
│   ├── dto/
│   │   ├── LoginRequest.java
│   │   ├── RegisterRequest.java
│   │   └── StudentDTO.java
│   ├── entity/
│   │   ├── Institution.java          ← institution accounts
│   │   ├── Student.java              ← student records (with institution_id)
│   │   └── UserSession.java          ← session tokens
│   ├── exception/
│   │   ├── GlobalExceptionHandler.java
│   │   └── ResourceNotFoundException.java
│   ├── filter/
│   │   └── AuthFilter.java           ← session cookie validation
│   ├── repository/
│   │   ├── InstitutionRepository.java
│   │   ├── StudentRepository.java
│   │   └── UserSessionRepository.java
│   ├── service/
│   │   ├── AuthService.java
│   │   └── StudentService.java
│   └── StudentApplication.java
└── resources/
    ├── application.properties
    └── static/
        ├── landing.html   ← public marketing/overview page
        ├── login.html     ← institution login
        ├── register.html  ← institution registration
        └── index.html     ← student management UI (requires login)
```

## Prerequisites

- Java 17 or later
- Maven
- MySQL

## Getting Started

1. **Clone the repository:**

    ```bash
    git clone <repository-url>
    cd student-management-system
    ```

2. **Create a MySQL database:**

    ```sql
    CREATE DATABASE student_db;
    ```

3. **Set up environment variables:**

    Create a `.env` file in the project root:

    ```
    DB_USERNAME=your-username
    DB_PASSWORD=your-password
    ```

    > **Important**: Keep `.env` in `.gitignore` — never commit credentials.

    The app falls back to `root` / `mysql` if variables are not set.

4. **Build and run:**

    ```bash
    mvn clean install
    mvn spring-boot:run
    ```

    Hibernate will auto-create the required tables (`institution`, `user_session`, `student`) on first run.

5. **Access the application:**

    | URL | Description |
    | --- | --- |
    | `http://localhost:8080/landing.html` | Overview / landing page |
    | `http://localhost:8080/register.html` | Register a new institution |
    | `http://localhost:8080/login.html` | Institution login |
    | `http://localhost:8080/` | Student management UI (login required) |

## API Endpoints

### Auth (`/auth`)

| Method | Endpoint | Auth Required | Description |
| --- | --- | --- | --- |
| POST | `/auth/register` | No | Register a new institution |
| POST | `/auth/login` | No | Log in — sets `SESSION_TOKEN` cookie |
| POST | `/auth/logout` | Yes | Log out — clears session and cookie |
| GET | `/auth/me` | Yes | Returns the current institution's details |

### Students (`/students`) — all require a valid session cookie

| Method | Endpoint | Description |
| --- | --- | --- |
| POST | `/students` | Create a new student |
| GET | `/students` | List students (paginated + search), scoped to the logged-in institution |
| PUT | `/students/{id}` | Update a student (must belong to the logged-in institution) |
| DELETE | `/students/{id}` | Delete a student (must belong to the logged-in institution) |

**Query parameters for `GET /students`:**

- `page` — page number, 0-indexed (default: `0`)
- `size` — records per page (default: `5`)
- `query` — search term matched against name or email

## How Authentication Works

1. Institution registers at `/auth/register` (password is BCrypt-hashed and stored).
2. On login (`POST /auth/login`), the server creates a `UserSession` row with a UUID token and a 7-day expiry, then sets it as an `HttpOnly` cookie named `SESSION_TOKEN`.
3. `AuthFilter` intercepts all non-public requests. It reads the cookie, finds the matching session, and if valid, stores the `institutionId` as a request attribute.
4. Controllers read `institutionId` from the request attribute and pass it to the service — no endpoint can access data from a different institution.
5. Logout deletes the session row and expires the cookie.

**Public paths** (no session needed): `/auth/login`, `/auth/register`, `/landing.html`, `/login.html`, `/register.html`, and all static assets.
