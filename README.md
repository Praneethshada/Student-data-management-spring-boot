# Student Data Management with Spring Boot

This is a simple web application I built to manage student records. It's a RESTful API created with Java and Spring Boot that lets you perform all the basic CRUD (Create, Read, Update, Delete) operations for students.

## What This Project Shows

I built this project to practice and demonstrate my skills in backend development. Here’s a quick look at what I've learned and used here:

- **Core Java & Spring Boot**: I used the fundamentals of Java and the Spring Boot framework to build a complete, standalone web application.
- **REST API Development**: I designed and built a RESTful API from scratch, focusing on creating clear and logical endpoints.
- **Database Interaction**: I used Spring Data JPA and Hibernate to connect the application to a MySQL database. This allowed me to work with the database using simple Java objects without writing complex SQL queries.
- **Layered Architecture**: I structured the application into layers (controller, service, repository) to keep the code organized, clean, and easy to maintain.
- **Data Validation**: I implemented server-side validation to make sure the data entered is correct and complete.
- **Error Handling**: I created a centralized system to handle errors gracefully, making the API more reliable and user-friendly.
- **Dependency Management**: I used Maven to manage all the project's dependencies and to build the application.
- **Secure Credential Management**: I configured the application to use environment variables for database credentials, which is a much safer practice than hardcoding them.

## Design Patterns and Principles

- **Layered Architecture**: I organized the code into distinct layers (Controller, Service, and Repository) to separate concerns and make the application easier to manage and scale.
- **Repository Pattern**: I used the Repository pattern to abstract the data access logic, making it easy to manage database operations without mixing them with business logic.
- **Data Transfer Object (DTO) Pattern**: I used DTOs to transfer data between the controller and service layers. This helps prevent exposing the internal database structure and allows for more flexible data contracts.
- **Dependency Injection (DI)**: I relied on Spring's dependency injection to manage the components and their dependencies, which makes the code more modular and easier to test.
- **Single Responsibility Principle (SRP)**: Each class in the application has a single, clear purpose—the controller handles web requests, the service contains business logic, and the repository manages data.

## Features

- **CRUD Operations**: You can create, read, update, and delete student records.
- **Pagination**: The student list is paginated to keep it manageable.
- **Search**: You can easily search for students by name or email.
- **Simple UI**: There's a basic user interface to make it easy to interact with the application.

## Technologies Used

- **Backend**: Java 17, Spring Boot, Spring Web MVC, Spring Data JPA, Hibernate
- **Database**: MySQL
- **Build Tool**: Maven

## Prerequisites

Before you get started, make sure you have these installed:

- Java 17 or later
- Maven
- MySQL

## Getting Started

1.  **Clone the repository:**

    ```bash
    git clone <repository-url>
    cd student-management-system
    ```

2.  **Create a MySQL database:**

    ```sql
    CREATE DATABASE student_db;
    ```

3.  **Set up your environment variables:**
    To keep your database credentials secure, this project uses environment variables.
    - Create a file named `.env` in the root of the project.
    - Add your database username and password to it like this:
      ```
      DB_USERNAME=your-username
      DB_PASSWORD=your-password
      ```
    - **Important**: Add the `.env` file to your `.gitignore` file so you don't accidentally commit your secrets.

    The application is configured to read these variables. If they aren't found, it will fall back to default values (`root` and `mysql`).

4.  **Build and run the application:**

    ```bash
    mvn clean install
    mvn spring-boot:run
    ```

5.  **Access the application:**
    - **Frontend UI**: Open your browser and go to `http://localhost:8080`
    - **API Base URL**: `http://localhost:8080/students`

## API Endpoints

| Method | Endpoint         | Description                                 |
| ------ | ---------------- | ------------------------------------------- |
| POST   | `/students`      | Create a new student                        |
| GET    | `/students`      | Get all students (with pagination & search) |
| PUT    | `/students/{id}` | Update an existing student                  |
| DELETE | `/students/{id}` | Delete a student                            |

**Query Parameters for `GET /students`:**

- `page`: The page number to retrieve (e.g., `?page=0`).
- `size`: The number of students per page (e.g., `?size=10`).
- `query`: A search term for name or email (e.g., `?query=john`).
