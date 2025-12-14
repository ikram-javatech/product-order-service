# 🛒 Product Order Service

Runnable **Spring Boot** backend application developed as an **interview assignment**, demonstrating clean architecture, JWT-based security, and role-based access control.

---

## 📌 Features

- Product management (CRUD)
- Order placement and viewing
- JWT-based authentication
- Role-based authorization (`ADMIN`, `USER`, `PREMIUM_USER`)
- Global exception handling (`@ControllerAdvice`)
- Request/response logging filter
- H2 in-memory database
- Spring Actuator endpoints
- Swagger / OpenAPI documentation

---

## 🏗️ Tech Stack

- Java 17
- Spring Boot 3.x
- Spring Security 6
- Spring Data JPA
- H2 Database
- JWT (jjwt)
- Maven
- Springdoc OpenAPI (Swagger UI)

---

## 👥 Roles & Permissions

| Role | Permissions |
|-----|------------|
| **ADMIN** | Full CRUD on products, view all orders, view order by id |
| **USER** | View products, place orders, view order by id |
| **PREMIUM_USER** | View products, place orders, view order by id |

---

## 🔐 Security Design

- Stateless authentication using **JWT**
- Authorization enforced via **Spring Security filter chain**
- Roles stored as authorities (`ADMIN`, `USER`, `PREMIUM_USER`)
- Controllers rely on Spring Security context
- Custom `JwtAuthenticationFilter` validates token and sets security context

---

## ⚙️ Setup Instructions

### Prerequisites
- Java 17+
- Maven 3.8+

### Run Application
```bash
mvn clean spring-boot:run
```

Application runs at:
```
http://localhost:8080
```

---

## 🗄️ H2 Database

- Console: http://localhost:8080/h2-console
- JDBC URL: jdbc:h2:mem:db
- Username: sa
- Password: (empty)

---

## 🔑 Authentication APIs

### Register
POST /api/auth/register

### Login
POST /api/auth/login

Use JWT:
```
Authorization: Bearer <JWT>
```

---

## 📦 Product APIs

- GET /api/products
- GET /api/products/{id}
- POST /api/products (ADMIN)
- PUT /api/products/{id} (ADMIN)
- DELETE /api/products/{id} (ADMIN)

---

## 🧾 Order APIs

- POST /api/orders (USER / PREMIUM_USER)
- GET /api/orders/{id} (USER / PREMIUM_USER / ADMIN)
- GET /api/orders (ADMIN)

---

## 📘 Swagger / OpenAPI

- UI: http://localhost:8080/swagger-ui/index.html
- Spec: /v3/api-docs

---

## 📊 Actuator Endpoints

- /actuator/health
- /actuator/info
- /actuator/metrics

---

## 👨‍💻 Author

Ikram
