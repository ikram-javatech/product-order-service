# 🛒 Product Order Service

A **Spring Boot REST application** to manage **products, users, and orders** with **JWT-based authentication**, **role-based access control**, and **dynamic discount calculation**.

This project demonstrates **clean architecture**, **SOLID principles**, and **production-grade security practices**, making it suitable for **real-world backend systems**.

---

## 🚀 Tech Stack

- Java 17
- Spring Boot
- Spring Security (JWT)
- Spring Data JPA
- H2 / In-memory DB (tests)
- JUnit 5 & Mockito
- Lombok
- BCrypt
- OpenAPI / Swagger

---

## 📐 Architecture Overview

The application follows a **layered architecture** with strict separation of responsibilities.  
Each layer has a single responsibility and depends only on the layer below it.

### 🧩 Diagram-Style Architecture

```
┌───────────────────────────┐
│        Controller         │
│  (REST API Endpoints)     │
│                           │
│ - Request mapping         │
│ - Input validation        │
│ - Delegates to services   │
└─────────────▲─────────────┘
              │
┌─────────────┴─────────────┐
│     Service Interface     │
│  (Business Contracts)     │
│                           │
│ - Defines operations      │
│ - Enables loose coupling  │
│ - Mockable for tests      │
└─────────────▲─────────────┘
              │
┌─────────────┴─────────────┐
│   Service Implementation  │
│ (Core Business Logic)     │
│                           │
│ - Validation              │
│ - Authorization checks    │
│ - Discount calculation    │
│ - Inventory updates       │
│ - Centralized logging     │
└─────────────▲─────────────┘
              │
┌─────────────┴─────────────┐
│        Repository         │
│     (Spring Data JPA)     │
│                           │
│ - Persistence logic       │
│ - CRUD operations         │
│ - No business rules       │
└───────────────────────────┘
```

---

## 🧩 Layer Responsibilities

### 🔹 Controller Layer
- Handles HTTP requests and responses
- Performs request validation
- Delegates business logic to services
- Contains **no business logic**

### 🔹 Service Layer (Interface)
- Defines business contracts
- Decouples controllers from implementations
- Enables mocking and unit testing

### 🔹 Service Implementation Layer
- Contains core business rules
- Performs authorization and ownership checks
- Coordinates repositories
- **All application logging is centralized here**

### 🔹 Repository Layer
- Handles persistence via Spring Data JPA
- Contains no business or security logic

---

## 🧠 Mapping Architecture to SOLID Principles

- **SRP** – Each layer has a single responsibility  
- **OCP** – Discount logic extensible via Strategy Pattern  
- **LSP** – Service implementations interchangeable  
- **ISP** – Lean service interfaces  
- **DIP** – Controllers depend on interfaces, not implementations  

---

## 🔐 Security Design

### Authentication
- JWT-based authentication
- Token generated on successful login
- Token validated on every protected request

### Authorization (RBAC)

| Role         | Permissions                                  |
|--------------|----------------------------------------------|
| ADMIN        | Full CRUD on products, view all orders       |
| USER         | View products, place orders, view own orders |
| PREMIUM_USER | Same as USER + discounts                     |

---

## 🔑 Password Security

✔ No plain-text passwords  
✔ BCrypt hashing  
✔ Secure comparison using `passwordEncoder.matches(...)`

---

## 📦 Data Bootstrapping

```
src/main/resources/
 ├── users.json     (BCrypt-hashed passwords)
 └── products.json
```

Disabled in tests using `@Profile("!test")`.

---

## 🛍️ Order Management

- Multi-item orders supported
- Stock validation before placement
- Inventory reduced after success
- Ownership enforced at service layer

---

## 💸 Discount Calculation (Strategy Pattern)

- USER → No discount
- PREMIUM_USER → 10%
- Orders > $500 → Extra 5%

---

## 🧪 Testing Strategy

- Unit tests with Mockito
- Integration tests for security & controllers
- Isolated test profile

---

## 📄 Request & Response Logging

`RequestResponseLoggingFilter` logs:
- HTTP method and URI
- Status & execution time
- Masks sensitive data

---

## 📘 API Documentation (Swagger / OpenAPI)

```
http://localhost:8080/swagger-ui/index.html
http://localhost:8080/v3/api-docs
```

Authorization:
```
Bearer <JWT_TOKEN>
```

---

## 📘 API Endpoints (Controller-Aligned)

### Auth – `/api/auth`
| Method | Endpoint | Description |
|------|---------|------------|
| POST | /api/auth/login | Login & get JWT |

### Products – `/api/products`
| Method | Endpoint | Role |
|------|---------|------|
| POST | /create | ADMIN |
| GET | / | ALL |
| GET | /{id} | ALL |
| PUT | /{id} | ADMIN |
| DELETE | /{id} | ADMIN |

### Orders – `/api/orders`
| Method | Endpoint | Role |
|------|---------|------|
| POST | / | USER / PREMIUM |
| GET | / | USER / PREMIUM |
| GET | /{id} | USER / PREMIUM |

---

## 🧪 Sample Request / Response

### Login
```json
{ "username": "user1", "password": "password123" }
```

```json
{ "token": "eyJhbGciOiJIUzI1NiJ9..." }
```

---

## 📮 Postman Collection

A ready-to-use **Postman collection** is included **inside the project** and committed to the repository.

📁 **Location**
```
postman/Product-Order-Service.postman_collection.json
```

### Features
- Login request with **JWT auto extraction**
- JWT stored as a **collection variable**
- All secured APIs automatically use:
```
Authorization: Bearer {{jwt}}
```

### How to Use
1. Import the collection into Postman
2. Ensure `baseUrl` is set (default: `http://localhost:8080`)
3. Run **Auth → Login** once
4. Call Product and Order APIs without manually setting JWT

---

## 🏁 How to Run

```bash
mvn clean install
mvn spring-boot:run
```

Runs at:
```
http://localhost:8080
```

---

