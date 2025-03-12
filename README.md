# Web Store Api Overview

This API is designed for an online store with a focus on scalability, security, and maintainability. It provides comprehensive management for users, products, and shopping carts, along with robust error handling and security features.

## Key Features

- **User Management:**  
  - CRUD operations with role-based access control.

- **Product Management:**  
  - Integration with the OpenAI API for generating product descriptions.
  - Bulk product import from CSV files.

- **Security:**  
  - JWT authentication using Spring Security with custom filters.
  - Critical endpoints secured with role-based access control.

- **Shopping Cart:**  
  - Efficient cart management with Redis caching.

- **Error Handling & Validation:**  
  - Global exception handling using a dedicated handler.
  - Dynamic request validation with Jakarta Validation.

- **Pagination & Filtering:**  
  - Dynamic filtering and pagination using JPA Specifications and JpaRepository.

- **Testing & Quality Assurance:**  
  - Unit tests (Mockito) and parameterized integration tests (JUnit5, MockMvc).
  - Test Data Builder pattern for test data setup.
  - 96% test coverage as measured by Jacoco.

- **AWS S3 Integration:**  
  - Integration with AWS S3 for storing product and categories images.
  - Image upload and retrieval using the AWS SDK.

- **Swagger Documentation:**  
  - API documentation using Swagger UI.

## Project Structure

The application is organized into distinct modules, each responsible for a specific domain:

- **aws**
- **cache**
- **carts**
- **categories**
- **commons**
- **exceptions**
- **openai**
- **orders**
- **orderstatuses**
- **products**
- **roles**
- **security**
- **swagger**
- **users**

This modular structure keeps the codebase organized and supports scalable development.

## Technologies Used

- **Backend:** Java, Spring Boot, Spring Security, and JWT.  
- **Data Management:** PostgreSQL with Liquibase for schema management.  
- **Caching:** Redis for caching and session management.  
- **API & Documentation:** Swagger for API documentation (accessible at [http://localhost:3000/api/swagger-ui/index.html](http://localhost:3000/api/swagger-ui/index.html)) and integration with the OpenAI API.  
- **Testing:** JUnit5, MockMvc, Mockito, and Jacoco for code coverage.  
- **Development Tools:** Lombok to reduce boilerplate code, MapStruct for DTO mapping, and Jakarta Validation for request validation.  
- **Containerization:** Docker Compose to manage Postgres and Redis services.

## Docker Compose Setup

Use the following `docker-compose.yml` to set up the required services:

```yaml
services:
  web:
    image: john1903/web-store-api:latest
    ports:
      - "3000:3000"
    environment:
      - DB_HOST=db
      - DB_PORT=5432
      - DB_NAME=postgres
      - DB_USER=postgres
      - DB_PASSWORD=postgres
      - REDIS_URI=cache:6379
      - OPENAI_API_KEY=<YOUR_OPENAI_API_KEY>
      - JWT_SECRET=<YOUR_JWT_SECRET>
      - CORS_HOST=<YOUR_CORS_HOST>
      - AWS_ACCESS_KEY=<YOUR_AWS_ACCESS_KEY>
      - AWS_SECRET_KEY=<YOUR_AWS_SECRET_KEY>
      - AWS_S3_BUCKET=<YOUR_AWS_S3_BUCKET>
      - AWS_URL=<YOUR_AWS_URL>
      - AWS_REGION=<YOUR_AWS_REGION>
    depends_on:
      - db
      - cache
  db:
    image: postgres:17-alpine
    ports:
      - "5432:5432"
    environment:
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
      POSTGRES_DB: postgres
    volumes:
      - postgres_data:/var/lib/postgresql/data
  cache:
    image: redis:7.4.2-alpine
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data

volumes:
  postgres_data:
  redis_data:
```

## License

This project is provided under a view-only license. You are welcome to review the code and documentation; however, reproduction, modification, or any use beyond personal study is strictly prohibited without explicit permission from the author.
