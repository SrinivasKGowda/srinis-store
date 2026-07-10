# Srini's Store API

E-commerce REST API built with Spring Boot, Spring Data JPA, and MySQL.

## Tech Stack
- Java 17, Spring Boot 3.2.5
- Spring Data JPA / Hibernate
- MySQL + HikariCP
- Bean Validation, Springdoc OpenAPI (Swagger)
- Lombok

## Database Setup
1. Create MySQL user/database access for `root` (or update credentials).
2. Database `srinis_store` is auto-created on first run (`createDatabaseIfNotExist=true`).
3. Schema is managed by Hibernate (`ddl-auto=update`) — no manual scripts needed.

Config: `src/main/resources/application.properties`

## Features
- Product, Customer, Order, Category, Tag management
- Pagination & sorting (`?page=0&size=10&sort=price`)
- Product search, price/category filters, top-selling & low-stock queries
- Order placement with inventory deduction, status updates, cancellation
- Global exception handling with clean JSON error responses
- No authentication — open API, ready for local/Postman use

## Swagger URL
http://localhost:8080/swagger-ui.html

## Key Endpoints
| Method | Endpoint | Description |
|---|---|---|
| GET | /api/products | List products (paginated) |
| GET | /api/products/search?name= | Search products |
| GET | /api/products/top-selling | Top selling products |
| GET | /api/products/low-stock | Low stock products |
| POST | /api/products | Create product |
| GET | /api/customers | List customers |
| POST | /api/customers | Create customer |
| POST | /api/orders | Place an order |
| GET | /api/orders/{id} | Order details |
| PATCH | /api/orders/{id}/status | Update order status |
| POST | /api/products/{id}/image | Upload product image (multipart) |
| GET | /api/products/{id}/image | Download product image |
| GET | /actuator/storestats | Custom endpoint: product/customer/order counts |

## Response Formats
JSON by default. XML also supported — send `Accept: application/xml` to any endpoint.

## How to Run
```bash
mvn clean install
mvn spring-boot:run
```
App starts at `http://localhost:8080`.

## Future Improvements
- Optional auth layer (JWT/OAuth2) if needed later
- Docker Compose for MySQL + app
- Caching tuning, integration tests
