# Financial Book Management System API

Production-grade Spring Boot REST API for managing books, authors, categories, users, and reviews with validation, role-based access control, JWT authentication, pagination/filtering, and structured error handling.

## Tech Stack

- Java 21
- Spring Boot 3.4
- Spring Data JPA
- Spring Security
- Hibernate Validator
- PostgreSQL
- Lombok
- JUnit 5 + Mockito
- Springdoc OpenAPI

## Project Structure

```text
src/main/java/com/bookmanagement/bookmanagementapp/
├── config/
├── controller/
├── dto/
├── entity/
├── exception/
├── repository/
├── security/
├── service/
│   └── impl/
└── util/
    ├── mapper/
    └── validation/
```

## Setup Instructions

### Prerequisites

- Java 21
- PostgreSQL running locally or remotely
- Maven Wrapper included in the project

### Database

Create a PostgreSQL database, for example:

```sql
CREATE DATABASE bookmanagement;
```

### Environment / Configuration

Default local settings are in [application.properties](/C:/Users/Florence%20Adepoju/Documents/Book-Management-Project/bookmanagementapp/bookmanagementapp/src/main/resources/application.properties).

You can override them with environment variables:

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `JWT_SECRET`
- `JWT_EXPIRATION`

For production profile, use [application-prod.properties](/C:/Users/Florence%20Adepoju/Documents/Book-Management-Project/bookmanagementapp/bookmanagementapp/src/main/resources/application-prod.properties).

### Run the Application

```bash
./mvnw spring-boot:run
```

### Run Tests

```bash
./mvnw test
```

## Authentication and Authorization

### Roles

- `ADMIN`
- `USER`

### Rules

- Only `ADMIN` users can create, update, and delete books
- Only `ADMIN` users can create, update, and delete authors
- Only `ADMIN` users can create, update, and delete categories
- Authenticated users can create reviews
- Review updates/deletes are restricted to the review owner or an admin
- User `GET/PUT/DELETE` endpoints are restricted to the same user or an admin
- Public user registration is allowed for `USER` accounts
- Creating an `ADMIN` account requires an authenticated admin

### Login

`POST /auth/login`

Request:

```json
{
  "username": "admin",
  "password": "Password123"
}
```

Response:

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "admin",
  "role": "ADMIN"
}
```

Use the token in the `Authorization` header:

```text
Authorization: Bearer <jwt-token>
```

## API Summary

### Books

- `GET /books?page=0&size=10&authorId=1&categoryId=2&ratingMin=3&ratingMax=5&publishedStart=2020-01-01&publishedEnd=2024-12-31&sortBy=title&sortDirection=asc`
- `GET /books/{id}`
- `POST /books`
- `PUT /books/{id}`
- `DELETE /books/{id}`

### Authors

- `GET /authors`
- `GET /authors/{id}`
- `POST /authors`
- `PUT /authors/{id}`
- `DELETE /authors/{id}`

### Categories

- `GET /categories`
- `GET /categories/{id}`
- `POST /categories`
- `PUT /categories/{id}`
- `DELETE /categories/{id}`

### Users

- `POST /users`
- `GET /users/{id}`
- `PUT /users/{id}`
- `DELETE /users/{id}`

### Reviews

- `POST /books/{bookId}/reviews`
- `GET /books/{bookId}/reviews`
- `PUT /reviews/{id}`
- `DELETE /reviews/{id}`

## Sample Requests and Responses

### Create User

Request:

```json
{
  "username": "reader1",
  "email": "reader1@example.com",
  "password": "Password123",
  "role": "USER"
}
```

Response:

```json
{
  "id": 1,
  "username": "reader1",
  "email": "reader1@example.com",
  "role": "USER"
}
```

### Create Author

Request:

```json
{
  "name": "Benjamin Graham",
  "email": "bgraham@example.com",
  "bio": "Widely regarded as the father of value investing."
}
```

Response:

```json
{
  "id": 1,
  "name": "Benjamin Graham",
  "email": "bgraham@example.com",
  "bio": "Widely regarded as the father of value investing.",
  "books": []
}
```

### Create Category

Request:

```json
{
  "name": "Finance"
}
```

Response:

```json
{
  "id": 1,
  "name": "Finance"
}
```

### Create Book

Request:

```json
{
  "title": "The Intelligent Investor",
  "isbn": "9780060555665",
  "publishedDate": "1949-01-01",
  "authorId": 1,
  "categoryIds": [1]
}
```

Response:

```json
{
  "id": 1,
  "title": "The Intelligent Investor",
  "isbn": "9780060555665",
  "publishedDate": "1949-01-01",
  "author": {
    "id": 1,
    "name": "Benjamin Graham"
  },
  "categories": [
    {
      "id": 1,
      "name": "Finance"
    }
  ],
  "rating": 0.00
}
```

### Get Book Details

Response:

```json
{
  "id": 1,
  "title": "The Intelligent Investor",
  "isbn": "9780060555665",
  "publishedDate": "1949-01-01",
  "author": {
    "id": 1,
    "name": "Benjamin Graham",
    "email": "bgraham@example.com",
    "bio": "Widely regarded as the father of value investing."
  },
  "categories": [
    {
      "id": 1,
      "name": "Finance"
    }
  ],
  "rating": 4.50,
  "reviews": [
    {
      "id": 10,
      "book": {
        "id": 1,
        "title": "The Intelligent Investor"
      },
      "user": {
        "id": 2,
        "username": "reader1"
      },
      "rating": 5,
      "comment": "A timeless classic.",
      "createdAt": "2026-01-01T12:00:00"
    }
  ]
}
```

### Create Review

Request:

```json
{
  "rating": 5,
  "comment": "Excellent practical lessons.",
  "userId": 2
}
```

Response:

```json
{
  "id": 10,
  "book": {
    "id": 1,
    "title": "The Intelligent Investor"
  },
  "user": {
    "id": 2,
    "username": "reader1"
  },
  "rating": 5,
  "comment": "Excellent practical lessons.",
  "createdAt": "2026-01-01T12:00:00"
}
```

### Validation Error Example

```json
{
  "timestamp": "2026-01-01T12:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Request validation failed",
  "path": "/books",
  "validationErrors": {
    "isbn": "ISBN must be a valid ISBN-10 or ISBN-13",
    "categoryIds": "At least one category is required"
  }
}
```

## Design Decisions

- DTOs are used for all request and response payloads to keep entities internal
- Mapping is manual through a dedicated mapper component for readability and control
- `@RestControllerAdvice` centralizes structured error responses
- ISBN validation is implemented with a custom validation annotation supporting ISBN-10 and ISBN-13
- Review averages are recalculated automatically whenever reviews are created, updated, or deleted
- `JpaSpecificationExecutor` is used for flexible book filtering
- Security is stateless and JWT-based, with role checks enforced in both the filter chain and method security
- Business-sensitive checks such as review ownership and user self-access are handled through a dedicated security helper

## Assumptions

- A user can submit only one review per book
- Book ratings are stored as a 2-decimal average derived entirely from reviews
- Public registration is intended for `USER` accounts only
- Deleting an author or category that is still referenced by books is treated as a business validation error
- `PUT` requests are treated as full updates for the respective resource payloads

## API Documentation

After starting the application, Swagger UI is available at:

- [Swagger UI](http://localhost:8080/swagger-ui/index.html)

