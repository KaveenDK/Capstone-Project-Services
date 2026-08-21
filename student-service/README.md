# Student-Service

A microservice responsible for managing student records, including profile information and profile pictures. It exposes a RESTful API consumed by the API Gateway.

## About

This project is part of the Enterprise Cloud Application (ECA) module in the Higher Diploma in Software Engineering (HDSE) program at the Institute of Software Engineering (IJSE). It is intended exclusively for students enrolled in this program.

## Tech Stack

| Technology | Details |
|---|---|
| Java | 25 |
| Spring Boot | 4.0.3 |
| Spring Cloud | 2025.1.0 |
| Spring Data JPA | ORM / persistence layer |
| PostgreSQL | Relational database (port `12500`) |
| MapStruct | DTO ↔ Entity mapping |
| Lombok | Boilerplate reduction |
| Spring Validation | Bean validation |
| Spring Cloud Netflix Eureka Client | Service registration & discovery |
| Spring Cloud Config Client | Fetches config from Config-Server |
| Spring Boot Actuator | Health & management endpoints |

## Service Details

| Property | Value |
|---|---|
| Port | `8000` |
| Artifact ID | `Student-Service` |
| Group ID | `lk.ijse.eca` |
| Database | PostgreSQL — `jdbc:postgresql://localhost:12500/eca` |
| Picture Storage | `~/.ijse/eca/students/` |

## API Endpoints

Base path: `/api/v1/students`

| Method | Path | Description | Content-Type |
|---|---|---|---|
| `POST` | `/api/v1/students` | Create a new student | `multipart/form-data` |
| `GET` | `/api/v1/students` | Get all students | — |
| `GET` | `/api/v1/students/{nic}` | Get a student by NIC | — |
| `PUT` | `/api/v1/students/{nic}` | Update a student | `multipart/form-data` |
| `DELETE` | `/api/v1/students/{nic}` | Delete a student | — |
| `GET` | `/api/v1/students/{nic}/picture` | Get a student's profile picture | — |

> **NIC format:** `^\d{9}[vV]$` — 9 digits followed by `V` or `v` (e.g., `123456789V`). NIC is the primary key and cannot be changed after creation.

## Sample Request Body

> Requests must use `Content-Type: multipart/form-data`.

**POST / PUT** `/api/v1/students`

| Field | Type | Required | Validation |
|---|---|---|---|
| `nic` | `string` | Yes (on create) | `^\d{9}[vV]$` |
| `name` | `string` | Yes | Letters and spaces only |
| `address` | `string` | Yes | — |
| `mobile` | `string` | Yes | — |
| `email` | `string` | No | Valid email format |
| `picture` | `file` | Yes (on create) | Image file, max 5 MB |

**Sample response:**

```json
{
  "nic": "123456789V",
  "name": "Kasun Perera",
  "address": "123 Main Street, Colombo",
  "mobile": "0771234567",
  "email": "kasun@example.com",
  "picture": "/api/v1/students/123456789V/picture"
}
```

## Getting Started

Follow the lecture guidelines, refer to the lecture video for more information and how to get started correctly.

> **Prerequisites:** Config-Server, Service-Registry, and Api-Gateway must be running. A PostgreSQL instance must be accessible on port `12500` with a database named `eca`.

**Startup order:**
1. Config-Server (`9000`)
2. Service-Registry (`9001`)
3. Api-Gateway (`7000`)
4. **Student-Service** (`8000`)

```bash
./mvnw spring-boot:run
```

## Testing

A Postman collection is available for testing the API endpoints:

**Student Service:** [Open Collection](https://www.postman.com/ijse-eca-5768309/workspace/eca-69-70/collection/47280517-c0d82f07-2650-4406-9dae-4f7ceab70669?action=share&creator=47280517)

## Need Help?

If you encounter any issues, feel free to reach out and start a discussion via the Slack workspace.
