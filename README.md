# Device Service

![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![MongoDB](https://img.shields.io/badge/MongoDB-4.4+-47A248?style=for-the-badge&logo=mongodb&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=for-the-badge&logo=docker&logoColor=white)

A RESTful API designed to manage the lifecycle of device resources. Focusing on scalability, clean
code practices, and production readiness.

---

## Getting Started

This project is fully containerized. You can run the entire environment (API + Database +
Unit Tests) using
Docker Compose.

### Prerequisites

* **Docker Engine** (v20.10+)
* **Docker Compose**

### Running the Application

1. Clone the repository:
   ```bash
   git clone https://github.com/guilantorres/device-service.git
   cd device-service
   ```

2. Build and start the containers:
   ```bash
   docker-compose up -d --build
   ```

3. The API will be available at:
    * **Base URL:** `http://localhost:8080/devices`
    * **Swagger UI (Docs):**
      [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

### Stopping the Application

To stop and remove containers:

```bash
docker-compose down
```

---

## Running Tests

The **Unit Tests** run automatically during the Docker build process. If any test fails, the build
stops, and the container will not start.

### Prerequisites for manual testing

* **Java 17 installed locally**
* **Maven (or use the provided `./mvnw` wrapper)**

***Note**: Integration tests are excluded from the default build lifecycle. They require the
specific mongo-test container to be active.*

### Running Unit Tests Manually

Runs service layer logic and validations.

```bash
./mvnw test -Dtest=!DeviceMongoRepositoryTests
```

### Running Integration tests

Verifies the repository layer against a real MongoDB instance.

First, start the isolated test database:

```bash
docker compose up -d mongo-test
```

Then, execute the tests using the test profile:

```bash
./mvnw test -Dtest=DeviceMongoRepositoryTests -Dspring.profiles.active=test
```

### Running All Tests

To run the full suite (Unit + Integration):

Ensure the test database is up:

```bash
docker compose up -d mongo-test
```

And run the tests with:

```bash
./mvnw test -Dspring.profiles.active=test
```

---

## Architecture & Design Decisions

### 1. Layered Architecture

The project follows the standard **Controller-Service-Repository** pattern, widely adopted in the
Spring ecosystem.

* **Controller:** Handles HTTP requests, input validation (`@Valid`), and response formatting.
* **Service:** Encapsulates all business rules, ensuring the "Domain Validations" required by the
  challenge.
* **Repository:** Handles data access. It uses Spring Data MongoDB to abstract boilerplate CRUD
  operations and implementation details.

**Decision:** I opted for an Anemic Domain Model with a rich Service Layer instead of a rich DDD
approach. Given the CRUD nature of the challenge.

### 2. API Design & Performance

* **Pagination:** The `GET /devices` endpoint implements pagination (`Pageable`) by default. This is
  a critical decision to prevent **Denial of Service (DoS)** and memory exhaustion issues when
  handling large datasets.
* **DTO Pattern:** Data Transfer Objects (DTOs) are used to decouple the internal database entity (
  `Device`) from the public API contract.
* **PUT vs. PATCH:**
    * **PUT:** Implemented with strict idempotency semantics.
    * **PATCH:** Implemented to allow partial updates without requiring the client to send the full
      payload.

### 3. Error Handling

A global `@RestControllerAdvice` handles exceptions centrally instead of multiple try/catch blocks.
It maps application exceptions (like`DeviceInUseException`) to proper HTTP Status Codes (
`409 Conflict`).

### 4. Infrastructure & Testing Strategy

* **Docker Compose:** Used to orchestrate the application and the MongoDB database.
* **External Managed Infrastructure for Tests:** Instead of mocking the database for integration
  tests, using Testcontainers or using embedded binaries (Flapdoodle), the project uses a dedicated
  `mongo-test` container via Docker Compose.

---

## Scalability & Performance

This service was designed with horizontal scalability in mind to support high-load scenarios
required by the challenge.

* **Stateless Architecture:** The application contains no user session state. This allows for easy
  horizontal scaling by simply deploying multiple instances of the `device-api` container behind
  a Load Balancer.
* **Database Scaling & Consistency:** MongoDB was chosen for its native horizontal scaling
  capabilities via Replica Sets. Regarding the CAP Theorem, MongoDB defaults to Consistency &
  Partition Tolerance.

---

## Trade-offs & Technical Debt

1. **Integration Testing Strategy (Docker Compose vs. Testcontainers):**
    * *Context:* Ideally, `Testcontainers` would be used to manage the database lifecycle within the
      JUnit execution.
    * *Decision:* Due to persistent environment-specific conflicts between the `docker-java`
      transport library and the Docker Socket API on certain Linux distributions (API version
      mismatches), a decision was made to decouple the infrastructure.
    * *Result:* A dedicated `mongo-test` container managed by Docker Compose. This ensures
      stability and removes build fragility, at the cost of requiring an external command to run
      integration tests.

2. **Security (Auth):**
    * To focus on the core CRUD domain logic and keep the scope manageable, the API is currently
      public. In a real-world scenario, **Spring Security** (with OAuth2/JWT) would be mandatory.

---

## Future Improvements

If this project were to evolve into a production microservice, these would be the next steps:

* **Observability:** Implement **Spring Boot Actuator** with **Micrometer** to export metrics to
  Prometheus/Grafana. Add Distributed Tracing (OpenTelemetry) to track requests across services.
* **Security:** Implement **OAuth2 Resource Server/Spring Security** to secure endpoints, allowing
  only authenticated clients/users to perform state-changing operations.
* **CI/CD Pipeline:** Create a GitHub Actions workflow to automatically run unit tests, build the
  Docker image, and push it to a registry upon every push to `main`.
* **Soft Deletes:** Instead of physically removing records (`DELETE`), implement a logical
  deletion (e.g., `deletedAt` timestamp) to support data recovery and auditing.