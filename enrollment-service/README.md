# Enrollment-Service

A microservice responsible for managing student enrollments into academic programs. It integrates with the Student-Service via REST to enrich enrollment responses with student details.

## About

This project is part of the Enterprise Cloud Application (ECA) module in the Higher Diploma in Software Engineering (HDSE) program at the Institute of Software Engineering (IJSE). It is intended exclusively for students enrolled in this program.

## Tech Stack

| Technology | Details |
|---|---|
| Java | 25 |
| Spring Boot | 4.0.3 |
| Spring Cloud | 2025.1.0 |
| Spring Data JPA | ORM / persistence layer |
| MySQL | Relational database (port `14500`) |
| Spring RestClient | HTTP client for inter-service calls |
| MapStruct | DTO ↔ Entity mapping |
| Lombok | Boilerplate reduction |
| Spring Validation | Bean validation |
| Spring Cloud Netflix Eureka Client | Service registration & discovery |
| Spring Cloud Config Client | Fetches config from Config-Server |
| Spring Boot Actuator | Health & management endpoints |

## Service Details

| Property | Value |
|---|---|
| Port | `8002` |
| Artifact ID | `Enrollment-Service` |
| Group ID | `lk.ijse.eca` |
| Database | MySQL — `jdbc:mysql://localhost:14500/eca` (auto-created) |

## API Endpoints

Base path: `/api/v1/enrollments`

| Method | Path | Description | Content-Type |
|---|---|---|---|
| `POST` | `/api/v1/enrollments` | Create a new enrollment | `application/json` |
| `GET` | `/api/v1/enrollments` | Get all enrollments | — |
| `GET` | `/api/v1/enrollments?programId={id}` | Get enrollments filtered by program | — |
| `GET` | `/api/v1/enrollments/{id}` | Get an enrollment by ID | — |
| `PUT` | `/api/v1/enrollments/{id}` | Update an enrollment | `application/json` |
| `DELETE` | `/api/v1/enrollments/{id}` | Delete an enrollment | — |

> **Enrollment ID** is an auto-generated numeric value (`Long`). The `student` field in responses is populated by calling the Student-Service.

## Sample Request Body

> Requests must use `Content-Type: application/json`.

**POST** `/api/v1/enrollments`

```json
{
  "date": "2025-01-15",
  "studentId": "123456789V",
  "programId": "HDSE"
}
```

**PUT** `/api/v1/enrollments/{id}`

```json
{
  "date": "2025-02-01",
  "studentId": "123456789V",
  "programId": "BSC"
}
```

**Sample response:**

```json
{
  "id": 1,
  "date": "2025-01-15",
  "studentId": "123456789V",
  "programId": "HDSE",
  "student": {
    "name": "Kasun Perera",
    "address": "123 Main Street, Colombo",
    "mobile": "0771234567",
    "email": "kasun@example.com",
    "picture": "/api/v1/students/123456789V/picture"
  }
}
```

## Getting Started

Follow the lecture guidelines, refer to the lecture video for more information and how to get started correctly.

> **Prerequisites:** Config-Server, Service-Registry, Api-Gateway, and Student-Service must be running. A MySQL instance must be accessible on port `14500`.

**Startup order:**
1. Config-Server (`9000`)
2. Service-Registry (`9001`)
3. Api-Gateway (`7000`)
4. Student-Service (`8000`)
5. Program-Service (`8001`)
6. **Enrollment-Service** (`8002`)

```bash
./mvnw spring-boot:run
```

## Testing

A Postman collection is available for testing the API endpoints:

**Enrollment Service:** [Open Collection](https://www.postman.com/ijse-eca-5768309/workspace/eca-69-70/collection/47280517-00b85c96-410b-4bfd-b622-30b989db2a7b?action=share&creator=47280517)

## Need Help?

If you encounter any issues, feel free to reach out and start a discussion via the Slack workspace.
