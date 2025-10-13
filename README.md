# JAGUAR Banking App

This is the backend source for the **JAGUAR** banking application, built with Spring Boot. This RESTful API provides a number of banking operations including user authentication, account management, and transaction processing.

> **Frontend Repository**: The frontend version of this project is located at [https://github.com/juIio/jaguar-frontend](https://github.com/juIio/jaguar-frontend)

## Overview

JAGUAR is a modern banking application that provides users with essential banking features including account registration, secure authentication, money transfers, and transaction history management. The backend is built using Spring Boot with a PostgreSQL database and JWT-based authentication.


## Architecture

JAGUAR is built using a **microservices architecture**, enabling scalability, maintainability, and independent deployment of services. The system leverages:

- **gRPC** for synchronous inter-service communication, providing high performance, type safe communication.
- **Apache Kafka** for asynchronous message processing and event driven operations.


## Features

### 🔐 Authentication & Security
- JWT-based authentication
- Secure password handling
- CORS configuration for cross-origin requests

### 👤 User Management
- User account creation and management
- Balance tracking and updates
- User data retrieval and validation

### 💸 Transaction Management
- Money transfers between users
- Transaction history tracking
- Balance updates in real-time
- Transaction filtering and search capabilities
- Recent transactions retrieval
- Transaction amount range queries

### 📊 Banking Operations
- Account balance management
- Transaction audit trail
- User-to-user money transfers

## Tech Stack

- **Framework**: Spring Boot 3.5.5
- **Language**: Java 17
- **Architecture**: Microservices
- **Database**: PostgreSQL
- **ORM**: Spring Data JPA with Hibernate
- **Authentication**: JSON Web Tokens (JWT)
- **Inter Service Communication**: gRPC (synchronous), Apache Kafka (asynchronous)
- **Build Tool**: Maven


## Installation & Running

### 1. Clone the Repository
```bash
git clone https://github.com/juIio/jaguar-backend
cd jaguar-backend
```

### 2. Install Dependencies
```bash
mvn clean install
```

### 3. Run the Application
```bash
# Development mode
mvn spring-boot:run

# Or using the Maven wrapper
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`

### 4. Build for Production
```bash
mvn clean package
java -jar target/jaguar-0.0.1-SNAPSHOT.jar
```

---

For the frontend implementation and user interface, visit: [https://github.com/juIio/jaguar-frontend](https://github.com/juIio/jaguar-frontend)
