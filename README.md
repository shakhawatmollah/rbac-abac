# 📘 RBAC-ABAC API

![Java](https://img.shields.io/badge/Java-25-orange?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.0-brightgreen?style=for-the-badge&logo=spring)
![Spring Security](https://img.shields.io/badge/Spring%20Security-7.x-green?style=for-the-badge&logo=spring-security)
![PostgreSQL](https://img.shields.io/badge/postgresql-4169e1?style=for-the-badge&logo=postgresql&logoColor=white)

Modern Spring Boot 4.x • JWT Auth • RBAC/ABAC • Pagination • PostgreSQL

---

## 🚀 Overview

The **RBAC-ABAC** is a modern REST API built with:

* **Spring Boot 4.x**
* **Spring Security + JWT**
* **Role-Based + Attribute-Based Access Control (RBAC/ABAC)**
* **PostgreSQL**
* **JPA/Hibernate**
* **Global API response wrapper**
* **Centralized pagination utility**

This API allows managing employees, departments, roles, permissions, authentication, and more.

---

## 🏗 Tech Stack

* **Java 23+**
* **Spring Boot 4.x**
* **Spring Security (JWT)**
* **Spring Data JPA**
* **PostgreSQL**
* **Lombok**

---

## 📦 Project Structure

```
src/main/java/com/shakhawat/rbacabac
 ├── config
 ├── controller
 ├── dto
 ├── entity
 ├── exception
 ├── filter
 ├── repository
 ├── security 
 ├── service
 └── util
```

---

## ⚙️ Setup Instructions

### 1️⃣ Clone the repository

```bash
git clone https://github.com/shakhawatmollah/rbac-abac.git
cd rbac-abac
```

### 2️⃣ Configure PostgreSQL

Create database:

```sql
CREATE DATABASE spring_demo;
```

### 3️⃣ Update `application.yml`

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/spring_demo?currentSchema=rbac_abac
    username: postgres
    password: yourPassword
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate.format_sql: true
```

---

## 🔐 Authentication

The API uses **JWT Access Tokens** and **Rotating Refresh Tokens**.

### Login

```
POST /api/auth/login
```

#### Request

```json
{
  "username": "admin",
  "password": "secret"
}
```

#### Response

```json
{
  "token": "jwt-access-token",
  "refreshToken": "refresh-token",
  "expiresIn": 3600
}
```

### Refresh Token

```
POST /api/auth/refresh
```

---

## 👥 Employees API

### ➤ Get All Employees (Paginated)

```
GET /api/employees?page=0&size=20&sortBy=id&direction=asc
```

### ➤ Get Employee by ID

```
GET /api/employees/{id}
```

### ➤ Create Employee

```
POST /api/employees
```

### ➤ Update Employee

```
PUT /api/employees/{id}
```

### ➤ Delete Employee

```
DELETE /api/employees/{id}
```

---

## 📄 Pagination

The system uses **PaginationUtil** to standardize all pagination requests.

```java
Pageable pageable =
    PaginationUtil.createPageRequest(page, size, sortBy, direction);
```

Each paginated response includes:

```json
"pagination": {
  "page": 0,
  "size": 20,
  "totalPages": 10,
  "totalElements": 200
}
```

---

## 🛠 Global Response Format

Every endpoint returns:

```json
{
  "success": true,
  "message": "Operation successful",
  "data": {},
  "pagination": null,
  "timestamp": "2025-01-01T10:20:30"
}
```

---

## 🔒 Role & Permission Annotations

* `@CanReadEmployee`
* `@CanCreateEmployee`
* `@CanUpdateEmployee`
* `@CanDeleteEmployee`

These map to RBAC + ABAC policies.

---

## 🧪 Running Tests

```bash
./mvnw test
```

---

## 🧭 API Documentation

You can also export:

[![Collection](https://run.pstmn.io/button.svg)](https://github.com/shakhawatmollah/rbac-abac/blob/main/docs/rbac-abac.postman_collection.json)

---

## 📊 Project Statistics

![GitHub stars](https://img.shields.io/github/stars/shakhawatmollah/clinic-management?style=social)
![GitHub forks](https://img.shields.io/github/forks/shakhawatmollah/clinic-management?style=social)
![GitHub watchers](https://img.shields.io/github/watchers/shakhawatmollah/clinic-management?style=social)
![GitHub issues](https://img.shields.io/github/issues/shakhawatmollah/clinic-management)

---

## 🤝 Contributing

Contributions are welcome! Here's how you can help:

### Ways to Contribute

- 🐛 Report bugs
- 💡 Suggest new features
- 📝 Improve documentation
- 🔧 Submit pull requests
- ⭐ Star the repository

### Development Process

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Code Standards

- Follow Java naming conventions
- Use Lombok for boilerplate reduction
- Write meaningful commit messages
- Add unit tests for new features
- Update documentation

---

## 👥 Author

**Shakhawat Mollah**
- GitHub: [@shakhawatmollah](https://github.com/shakhawatmollah)
- LinkedIn: [Shakhawat Mollah](https://linkedin.com/in/shakhawatmollah)

---

<div align="center">

**⭐ If you find this project helpful, please consider giving it a star!**

Made with ❤️ using Java 25, Spring Boot 4.0, and Spring Security 7

[⬆ Back to Top](#-RBAC-ABAC-API)

</div>