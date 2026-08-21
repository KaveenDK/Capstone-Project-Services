# Program-Service

A microservice responsible for managing academic programs offered by the institution. It exposes a RESTful JSON API consumed by the API Gateway.

## About

This project is part of the Enterprise Cloud Application (ECA) module in the Higher Diploma in Software Engineering (HDSE) program at the Institute of Software Engineering (IJSE). It is intended exclusively for students enrolled in this program.

## Tech Stack

| Technology | Details |
|---|---|
| Java | 25 |
| Spring Boot | 4.0.3 |
| Spring Cloud | 2025.1.0 |
| Spring Data MongoDB | NoSQL persistence layer |
| MongoDB | Document database (port `13500`) |
| MapStruct | DTO ↔ Document mapping |
| Lombok | Boilerplate reduction |
| Spring Validation | Bean validation |
| Spring Cloud Netflix Eureka Client | Service registration & discovery |
| Spring Cloud Config Client | Fetches config from Config-Server |
| Spring Boot Actuator | Health & management endpoints |

## Service Details

| Property | Value |
|---|---|
| Port | `8001` |
| Artifact ID | `Program-Service` |
| Group ID | `lk.ijse.eca` |
| Database | MongoDB — `localhost:13500` / database `eca` |

## API Endpoints

Base path: `/api/v1/programs`

| Method | Path | Description | Content-Type |
|---|---|---|---|
| `POST` | `/api/v1/programs` | Create a new program | `application/json` |
| `GET` | `/api/v1/programs` | Get all programs | — |
| `GET` | `/api/v1/programs/{programId}` | Get a program by ID | — |
| `PUT` | `/api/v1/programs/{programId}` | Update a program | `application/json` |
| `DELETE` | `/api/v1/programs/{programId}` | Delete a program | — |

> **Program ID format:** `^[A-Z]+$` — uppercase letters only (e.g., `HDSE`, `BSC`). The program ID is the primary key and cannot be changed after creation.

## Sample Request Body

> Requests must use `Content-Type: application/json`.

**POST** `/api/v1/programs`

```json
{
  "programId": "HDSE",
  "description": "Higher Diploma in Software Engineering"
}
```

**PUT** `/api/v1/programs/{programId}`

```json
{
  "description": "Higher Diploma in Software Engineering (Updated)"
}
```

**Sample response:**

```json
{
  "programId": "HDSE",
  "description": "Higher Diploma in Software Engineering"
}
```

## Getting Started

Follow the lecture guidelines, refer to the lecture video for more information and how to get started correctly.

> **Prerequisites:** Config-Server, Service-Registry, and Api-Gateway must be running. A MongoDB instance must be accessible on port `13500` with credentials `root / mongodb`.

**Startup order:**
1. Config-Server (`9000`)
2. Service-Registry (`9001`)
3. Api-Gateway (`7000`)
4. **Program-Service** (`8001`)

```bash
./mvnw spring-boot:run
```

## Testing

A Postman collection is available for testing the API endpoints:

**Program Service:** [Open Collection](https://www.postman.com/ijse-eca-5768309/workspace/eca-69-70/collection/47280517-603144f9-bfd0-4347-9660-1e198911c706?action=share&creator=47280517)

## Need Help?

If you encounter any issues, feel free to reach out and start a discussion via the Slack workspace.
