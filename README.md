# Speedrun PMS (Postal Management System)

A production-grade Postal Management System built with a modern tech stack.

## Architecture

-   **Frontend**: React (Vite), TailwindCSS, Shadcn/UI
-   **Backend**: Java (Spring Boot), JPA/Hibernate
-   **Database**: MySQL 8.0
-   **Infrastructure**: Docker Compose, Nginx

## Getting Started

### Prerequisites

-   Docker & Docker Compose
-   Node.js 20+ (for local dev)
-   Java 17+ (for local dev)

### Running with Docker (Recommended)

To run the full stack (Frontend + Backend + DB) in production-like containers:

```bash
# 1. Start all services
docker compose up --build -d

# 2. Access the application
# Frontend: http://localhost:80
# Backend API: http://localhost:8080/api (proxied) or direct http://localhost:8080
# Database: localhost:3307
```

### Local Development

#### Backend

```bash
cd backend
./mvnw spring-boot:run
```

#### Frontend

```bash
cd frontend
pnpm install
pnpm dev
```

## Configuration

Environment variables are managed in the root `.env` file. Change secrets here before deploying to production.

## Testing

-   **Frontend**: `cd frontend && pnpm test`
-   **Backend**: `cd backend && ./mvnw test`
