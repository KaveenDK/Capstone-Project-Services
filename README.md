# Capstone Project Services (Backend Services)

## Student Information
- **Student Name:** Dimantha Kaveen
- **Student Number:** 241722029
- **Slack Handle:** @KaveenDK
- **GCP Project ID:** project-46ec948e-0d3d-4923-927

## Project Description
This repository contains the business microservices for the ECA Campus Management System: **Student Service**, **Program Service**, and **Enrollment Service**. It demonstrates a robust polyglot persistence architecture integrating both Relational (PostgreSQL) and Non-Relational (MongoDB) databases, alongside Google Cloud Storage for handling media and file uploads.

## Technology Stack
- Java 25
- Spring Boot
- Spring Data
- PostgreSQL / MySQL
- MongoDB
- GCP Cloud Storage

## Setup / Getting Started
Ensure your local databases (PostgreSQL on port 5432, MongoDB on port 27017) are running. Start each microservice using the Maven wrapper:
\\\ash
./mvnw spring-boot:run
\\\
**Required Environment Variables:**
- SPRING_PROFILES_ACTIVE: dev
- SPRING_CLOUD_CONFIG_URI: URL to the Config Server (e.g., http://localhost:9000)
