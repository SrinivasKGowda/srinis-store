# Srini's Store

Backend API for a small online shop. Built with Spring Boot and MySQL.

No frontend here — just REST APIs. I tested everything with **Postman** and checked the database in **MySQL Workbench**.

**Author:** Srinivas K Gowda  
**Repo:** https://github.com/SrinivasKGowda/srinis-store

---

## What it does

You can manage products, customers, orders, categories and tags through HTTP APIs.

When someone places an order, stock goes down from the inventory table. Order status can be updated (PENDING → CONFIRMED → SHIPPED and so on). Pending orders get cancelled automatically after 30 minutes by a scheduler.

Product images can be uploaded to `uploads/products` folder.

---

## Tech used

Java 17, Spring Boot 3.2.5, Spring Data JPA, Hibernate, MySQL, Lombok, Swagger (Springdoc).

---

## Before you run

1. MySQL should be running on your machine.
2. Update username/password in `src/main/resources/application.properties` if needed.
3. Database name is `srinis_store` — it gets created on first run.

Default port: **7090**

---

## How I run it

Usually I just open the project in IntelliJ and run `ShopSphereApplication.java`.

Maven way:

```bash
mvn clean install
mvn spring-boot:run
```

App URL: http://localhost:7090  
Swagger UI: http://localhost:7090/swagger-ui.html

Store stats: http://localhost:7090/actuator/storestats

---

## API endpoints (main ones)

| Method | URL | What it does |
|--------|-----|--------------|
| GET | `/api/products` | List products (supports pagination) |
| POST | `/api/products` | Add new product |
| PUT/PATCH | `/api/products/{id}` | Update product |
| POST | `/api/products/{id}/image` | Upload product image |
| GET | `/api/categories` | List categories |
| POST | `/api/categories` | Add category |
| GET | `/api/tags` | List tags |
| POST | `/api/tags` | Add tag |
| GET | `/api/customers` | List customers |
| POST | `/api/customers` | Add customer |
| POST | `/api/orders` | Place order |
| GET | `/api/orders/{id}` | Get order details |
| PATCH | `/api/orders/{id}/status` | Change order status |
| DELETE | `/api/orders/{id}` | Delete order |

Full list with request body examples is available in Swagger.

---

## Postman testing order

Do it in this order so IDs match:

1. Categories  
2. Tags  
3. Products  
4. Customers  
5. Orders  
6. Then try GET, PUT, PATCH, DELETE

Sample JSON bodies are in `docs/Postman_All_Data.txt`.

---

## Project structure (short)

```
src/main/java/com/shopsphere/
  controller/   -> REST APIs
  service/      -> business logic
  repository/   -> DB queries
  entity/       -> JPA tables
  dto/          -> request/response objects
  exception/    -> error handling
  config/       -> app setup, store stats endpoint
  aspect/       -> logs method time (AOP)
  scheduler/    -> cancels old pending orders
```

---

## Few things I added

- Global exception handler — errors come back as proper JSON
- AOP logging on service methods
- JPQL + native queries for top selling products, low stock, revenue
- Pagination on list APIs (`?page=0&size=10&sort=price`)
- XML response support if you send `Accept: application/xml`
- Simple in-memory cache for category list

---

## Notes

- This is an API project for learning Spring Boot + JPA.
- Make sure order JSON is complete — missing `]}` at the end gave me 500 errors during testing.
- Hibernate creates/updates tables automatically (`ddl-auto=update`).

---

If something doesn't work, check Swagger first or look at the console logs — SQL queries print there when `show-sql=true`.
