# 📚 User Management System – Spring Boot + Docker + MySQL + RabbitMQ

A production-ready **User Management System** built with:

* **Spring Boot 3.x**
* **Spring Security + JWT Authentication**
* **Role-Based Authorization**
* **MySQL (Dockerized)**
* **RabbitMQ Event Publishing**
* **JPA + Hibernate**
* **Dockerfile + docker-compose.yml for complete environment setup**

---

## 🚀 Features

### ✅ Authentication & Authorization

* JWT-based login
* Secure password hashing using BCrypt
* **Role-based restrictions** using `@PreAuthorize`
* Supports multiple roles: `ADMIN`, `USER`

### ✅ User Operations

* User Registration
* User Login
* Assign roles to user (ADMIN only)
* View current user profile

### ✅ Role Management

* Create new roles (ADMIN only)
* Auto insert default `USER` and `ADMIN` roles at startup using `CommandLineRunner`

### ✅ Event System (RabbitMQ)

Triggers events:

* `user.registered`
* `user.loggedin`

---

# 📦 Project Architecture

```
src/
 ├── controller/
 │     ├── AuthController.java
 │     ├── UserRoleController.java
 │     ├── RoleController.java
 │
 ├── entity/
 │     ├── User.java
 │     ├── Role.java
 │
 ├── repository/
 │     ├── UserRepository.java
 │     ├── RoleRepository.java
 │
 ├── config/
 │     ├── SecurityConfig.java
 │     ├── JWTFilter.java
 │     ├── DataInitializer.java
 │
 ├── service/
 │     ├── UserService.java
 │     ├── EventPublisherService.java
 │     ├── JWTUtil.java
 │
 ├── BooksStoreApplication.java
```

---

# 🗄 Database Schema (ER Diagram)

### **1. Users Table**

| Column   | Type    | Details     |
| -------- | ------- | ----------- |
| id       | BIGINT  | Primary Key |
| username | VARCHAR | Unique      |
| email    | VARCHAR | Unique      |
| password | VARCHAR | Encoded     |

### **2. Roles Table**

| Column | Type                 |
| ------ | -------------------- |
| id     | BIGINT               |
| name   | VARCHAR (ADMIN/USER) |

### **3. User_Roles Join Table**

| user_id | role_id |
| ------- | ------- |

---

# 🔧 Design Decisions & Assumptions

### **1. JWT-Based Authentication**

Chosen to make system stateless and scalable. Each request must send:

```
Authorization: Bearer <token>
```

### **2. Role-Based Authorization**

Used `@PreAuthorize("hasAuthority('ADMIN')")` for fine-grained access control.

### **3. RabbitMQ for Event Publishing**

To simulate real-world microservices communication. Two events published:

* `user.registered`
* `user.loggedin`

### **4. DataInitializer for Auto Role Creation**

Automatically inserts:

* USER
* ADMIN
  if not present in DB.

### **5. Dockerized Environment**

Ensures entire app (DB + RabbitMQ + Spring Boot) runs with **one command**.

---

# 🐳 Docker Setup (MySQL + RabbitMQ + App)

To run the entire system with Docker:

## **1. docker-compose.yml**

```yml
version: "3.8"
services:
  mysql:
    image: mysql:8.0
    container_name: mysql
    environment:
      MYSQL_ROOT_PASSWORD: rootpassword
      MYSQL_DATABASE: usersdb
    ports:
      - "3307:3306"
    volumes:
      - mysql-data:/var/lib/mysql
    healthcheck:
      test: ["CMD","mysqladmin","ping","-prootpassword"]
      interval: 10s
      timeout: 5s
      retries: 5

  rabbitmq:
    image: rabbitmq:3.11-management
    container_name: rabbitmq
    ports:
      - "5672:5672"
      - "15672:15672"

  app:
    build: .
    container_name: userapp
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/usersdb
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: rootpassword
      SPRING_RABBITMQ_HOST: rabbitmq
    depends_on:
      mysql:
        condition: service_healthy
      rabbitmq:
        condition: service_started
    ports:
      - "8081:8081"

volumes:
  mysql-data:
```

---

## **2. Dockerfile**

```dockerfile
# Build Stage
FROM maven:3.9.2-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Run Stage
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

# ▶️ Running the Project

### **Step 1 — Build & Start**

```
docker-compose up --build
```

### **Step 2 — Check Running Services**

* App → [http://localhost:8081](http://localhost:8081)
* MySQL → localhost:3307
* RabbitMQ UI → [http://localhost:15672](http://localhost:15672)

  * username: guest
  * password: guest

---

# 🧪 Testing APIs

## **1. User Registration**

```
POST /api/users/register
```

Body:

```json
{
  "username": "john",
  "email": "john@example.com",
  "password": "123456",
  "role": "USER"
}
```

## **2. Login**

```
POST /api/users/login
```

Response:

```json
{
  "token": "…"
}
```

## **3. Create Role (ADMIN only)**

```
POST /api/roles
Headers:
Authorization: Bearer <ADMIN_TOKEN>
```

Body:

```json
{
  "name": "MANAGER"
}
```

## **4. Assign Role to User**

```
POST /api/users/5/roles
Headers:
Authorization: Bearer <ADMIN_TOKEN>
```

Body:

```json
["ADMIN"]
```

---

# 📩 Event Publishing (RabbitMQ)

Every time a user registers:

```
exchange: user.events.exchange
routing key: user.registered
```

Payload sample:

```json
{
  "id": 5,
  "email": "john@example.com",
  "username": "john",
  "time": "2025-01-01T10:20:00"
}
```

---

# 📘 Conclusion

This project demonstrates:

✔ Complete JWT Authentication
✔ Role-Based Authorization
✔ RabbitMQ Event System
✔ Containerized Infrastructure
✔ Clean Architecture
✔ Real MySQL Database
✔ Fully runnable via Docker
