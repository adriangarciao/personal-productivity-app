# Person Productivity App 🚀

A Spring Boot backend application for managing people and their tasks.  
Uses **PostgreSQL** as the database 🐘.

---

## Features ✨
- [x] Manage people (create, read, update, delete) 👤
- [x] Manage tasks linked to people 📝
- [x] Full CRUD functionality for both entities ✅
- [x] Integration with PostgreSQL 🐘
- [x] Comprehensive test coverage:
    - Service tests for `Person` and `Task` 🧪
    - Integration tests covering all CRUD operations 🔄

---

## Technologies Used 💻
- **Java 20**
- **Spring Boot**
- **Spring Data JPA**
- **PostgreSQL** 🐘
- **JUnit 5**
- **Mockito**
- **MapStruct** (DTO mapping) 🔄
- **Hibernate Validator** (input validation) ⚡

---

## Architecture 🏗️
- **Controllers** 📡: Handle HTTP requests and map them to service methods.
- **Services** ⚙️: Contain business logic, handle entity retrieval, updates, and DTO mapping.
- **Repositories** 🗄️: Interact with the database via Spring Data JPA.
- **Mappers** 🔄: Convert between entities and DTOs using MapStruct.
- **Models** 🧩: JPA entities for `Person` and `Task`.
- **DTOs** 📦: Data Transfer Objects for clean API responses and request validation.
- **Exceptions** ⚠️: Custom exceptions with a global handler for meaningful error messages.
- **Tests** 🧪: Service and integration tests for both `Person` and `Task`.

---

## Database 🐘
- PostgreSQL as the primary database.
- Tables: `people` and `task`.
- Relationships:
    - `Person` has many `Task`s
    - `Task` belongs to a `Person` 🔗

---

## Testing 🧪
- **Service Tests**: Cover all service layer methods for `Person` and `Task`.
- **Integration Tests**: Ensure full CRUD operations work across controllers, services, repositories, and database 🔄.
