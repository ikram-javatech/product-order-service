# 🛒 Product Order Service

A Spring Boot REST application to manage **products, users, and orders** with **JWT-based authentication**, **role-based
access control**, and **secure password handling**.

This project demonstrates **clean architecture**, **security best practices**, and **testable service design**, suitable
for real-world backend systems.

---

## 🚀 Tech Stack

- Java 17
- Spring Boot
- Spring Security (JWT)
- Spring Data JPA
- H2 / In-memory DB (tests)
- JUnit 5 & Mockito
- Lombok
- BCrypt password hashing

---

## 📐 Architecture Overview

The application follows a **layered architecture** with strict separation of responsibilities.  
Each layer depends only on the layer below it, ensuring **maintainability, testability, and scalability**.

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
              │ depends on
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
              │ implemented by
              │
┌─────────────┴─────────────┐
│   Service Implementation  │
│ (Core Business Logic)     │
│                           │
│ - Validation              │
│ - Authorization checks    │
│ - Discount calculation    │
│ - Inventory updates       │
│ - Logging (only here)     │
└─────────────▲─────────────┘
              │
              │ uses
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

### Layer Responsibilities

#### 🔹 Controller Layer
- Handles HTTP requests and responses
- Performs request validation
- Delegates business logic to services
- Contains no business logic

#### 🔹 Service Layer (Interface)
- Defines business contracts
- Decouples controllers from implementations
- Enables mocking and unit testing

#### 🔹 Service Implementation Layer
- Contains core business rules
- Performs authorization and ownership checks
- Coordinates repositories
- **All application logging is centralized here**

#### 🔹 Repository Layer
- Handles persistence via Spring Data JPA
- Contains no business or security logic

---

## 🧠 Mapping Architecture to SOLID Principles

### Single Responsibility Principle (SRP)
Each layer has a single responsibility:
- Controllers → API handling
- Services → Business logic
- Repositories → Data access

### Open/Closed Principle (OCP)
- Discount rules can be extended without modifying existing logic
- Strategy Pattern supports extension without changes

### Liskov Substitution Principle (LSP)
- Service implementations can be swapped without affecting controllers

### Interface Segregation Principle (ISP)
- Service interfaces expose only required operations

### Dependency Inversion Principle (DIP)
- Controllers depend on service interfaces, not implementations
- Promotes loose coupling and testability

---

## 🔐 Security Design

### Authentication
- JWT-based authentication
- Token generated on successful login
- Token validated on every protected request

### Authorization (Role-Based Access Control)

| Role         | Permissions                                  |
|--------------|----------------------------------------------|
| ADMIN        | Full CRUD on products, view all orders       |
| USER         | View products, place orders, view own orders |
| PREMIUM_USER | Same as USER + discounts                     |

---

## 🔑 Password Security

✔ **No plain-text passwords anywhere**

- Passwords are stored as **BCrypt hashes**
- Seed users also use **pre-hashed BCrypt passwords**
- Authentication uses:

```java
passwordEncoder.matches(rawPassword, storedHash)
```

---

## 📦 Data Bootstrapping (External Resources)

Initial data is loaded from external JSON files:

```
src/main/resources/
 ├── users.json     (BCrypt-hashed passwords)
 └── products.json
```

Bootstrap is disabled during tests using `@Profile("!test")`.

---

## 🛍️ Order Management

- Stock validation before order placement
- Inventory reduced after successful order
- Order ownership enforced at service layer
- ADMIN can access all orders
- USER / PREMIUM_USER can access only their own orders

---

## 💸 Discount Calculation (Strategy Pattern)

Discounts are calculated using the **Strategy Pattern**, allowing dynamic discount logic based on:
- User role
- Order total

This avoids conditional logic and supports easy extensibility.

---

## 🧪 Testing Strategy

- Unit tests using Mockito
- Integration tests for security and authorization
- Test profile ensures isolation and reliability

---

## 📄 Request & Response Logging

A global `RequestResponseLoggingFilter` logs:
- HTTP method and URI
- Response status and duration
- Request and response bodies (size-limited)

Sensitive fields like passwords and authorization headers are masked.

---

## 🏁 How to Run

```bash
mvn clean install
mvn spring-boot:run
```

Application runs at:

```
http://localhost:8080
```

---

## 📌 Notes

- No plain-text secrets
- No reversible encryption
- Clean, testable architecture
- Production-grade design practices
