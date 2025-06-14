
# 🛒 Order Processing System

This is a **Spring Boot-based Order Processing System** designed to manage customer orders, products, and order statuses. It provides REST APIs for creating orders, updating order statuses, and retrieving order information.

The system integrates with **MySQL 8** and runs smoothly in a **Dockerized environment** for easy deployment.

---

## 🚀 Features

✅ Create and manage customer orders  
✅ Handle stock availability and validations  
✅ Update and cancel orders  
✅ Filter orders by status  
✅ Global exception handling with structured error responses  
✅ Dockerized MySQL and Spring Boot app for easy setup

---

## ⚙️ Tech Stack

- **Java 24 / Spring Boot 3.5**
- **Spring Data JPA**
- **MySQL 8**
- **Docker / Docker Compose**
- **MapStruct**
- **Lombok**

---

## 📂 Project Structure

```
src/main/java/com/project/ecommerce
├── orders
│   ├── controllers             # REST controllers for orders
│   ├── services                # Order-related business logic
│   ├── repository              # Order JPA repositories
│   ├── entities                # Order + OrderItem + enums
│   ├── dto                     # Order-specific DTOs
│   ├── mapper                  # MapStruct mappers for order entities
│   └── exceptionhandling       # Custom exceptions + global handler for orders
│
├── customers
│   ├── repository              # Customer JPA repositories
│   └── entities                # Customer entity
│
└── products
    ├── repository              # Product JPA repositories
    └── entities                # Product entity
```

---

## 🐳 Docker Setup

### 📝 `docker-compose.yml`
```yaml
services:
  mysql:
    image: mysql:8.3
    container_name: project-ecommerce-mysql
    restart: always
    environment:
      MYSQL_DATABASE: order_management
      MYSQL_ROOT_PASSWORD: '@root0302'
    ports:
      - "3307:3306"

  app:
    build: .
    container_name: project-ecommerce-app
    depends_on:
      - mysql
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/order_management?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: '@root0302'
      SPRING_JPA_HIBERNATE_DDL_AUTO: update # or none, validate, create-drop, etc.
    restart: on-failure
```

### ⚡ Run the application

```bash
docker-compose up --build
```

- Spring Boot app → [http://localhost:8080](http://localhost:8080)
- MySQL → exposed on port `3307`
- Swagger UI → http://localhost:8080/api-doc/swagger-ui/index.html
- Thymeleaf UI → 
  - dashboard - http://localhost:8080/orders-ui/dashboard
  - summary - http://localhost:8080/orders-ui/summary

---

## 📌 API Endpoints

| Method | Endpoint                     | Description                   |
|---------|-----------------------------|-------------------------------|
| POST    | `/orders`                    | Create a new order             |
| GET     | `/orders/{id}`               | Retrieve order by ID           |
| PUT     | `/orders/{id}/status`        | Update order status            |
| GET     | `/orders?status={status}`    | Get all orders (optional filter)|
| DELETE  | `/orders/{id}`               | Cancel a pending order         |

---

## ❗ Customized Error Handling

- Returns structured `ApiError` JSON for all errors
- Example:
```json
{
  "status": "NOT_FOUND",
  "message": "Order not found with ID: 1",
  "timestamp": "2025-06-14T18:35:00",
  "path": "/orders/1"
}
```

---

## 📝 Notes

- The **default MySQL port is mapped to `3307`** to avoid conflicts with local MySQL.
- Use `docker-compose down -v` to stop and remove containers + volumes if needed.
- DB schema is automatically updated (`spring.jpa.hibernate.ddl-auto=update`).
- Mock customer and product data's are inserted into db for testing purpose.

---

## 🧑‍💻 Development

### Run locally without Docker:

1️⃣ Start MySQL manually (or use a local DB).  
2️⃣ Update `application.properties` or `application.yml` datasource config.  
3️⃣ Run:
```bash
mvn spring-boot:run
```
or
```bash
./mvnw spring-boot:run
```
