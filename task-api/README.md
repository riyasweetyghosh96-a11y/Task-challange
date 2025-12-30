# Task Management API

A RESTful web service built with **Spring Boot** to manage daily tasks and productivity. This application provides a clean separation of concerns using a layered architecture (Controller, Service, Repository) and supports full CRUD operations.

## Features

* **Full CRUD**: Create, Read, Update, and Delete tasks.
* **Status Tracking**: Mark tasks as CREATED/IN_PROGRESS/CANCELLED/COMPLETED.
* **Input Validation**: Integrated Spring Boot Validation to ensure data integrity.
* **Database**: PostgreSql database to maintain data in disk.
* **API Documentation**: Automated documentation via Swagger/OpenAPI.


## Tech Stack

* **Java**: 25
* **Framework**: Spring Boot 3.5.9
* **Persistence**: Spring Data JPA, Flyway
* **Database**: PostgreSql, H2(in-memory e2e test)
* **Documentation**: Springdoc OpenAPI
* **Build Tool**: Maven


## Getting Started
### Installation
1. **Clone the repository:**
   ```bash
   git clone https://github.com/riyasweetyghosh96-a11y/Task-challange.git
   cd Task-challange/task-api
2. **Build project:**
    ```bash
   mvn clean install
3. **Start postgreSql docker container:**
    ```bash
   docker-compose up -d
4. **Start the application from TaskApiApplication.java**
5. **Check swagger UI in http://localhost:8090/swagger-ui/index.html and api-docs in http://localhost:8090/v3/api-docs**
6. **Docker compose have pgweb container, so you can browse the postgresSql table via - http://localhost:8081**
7. Flyway will create the table **Tasks** in application startup
8. Table schema can be found in **resource/db.migration**
9. Try all the endpoint from postman with the help of api-docs in port: 8090
10. It has a cross-origin bypass for http://localhost:3100
