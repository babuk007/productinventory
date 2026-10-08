# Enterprise Product Inventory System

Enterprise Spring Boot legacy web application with embedded H2 in-memory database and traditional HTML5/Bootstrap 3/jQuery dashboard for customer demonstration of Git-based application modernization flows.

## Architecture Overview

- **Backend:** Spring Boot 2.7.18 (Java 8/17 compatible)
- **Persistence:** Spring Data JPA + H2 In-Memory Database
- **API Capabilities:** RESTful APIs for Products, Categories, Stock Adjustments, and Dashboard Analytics
- **Health & Monitoring:** Spring Boot Actuator (`/actuator/health`, `/actuator/metrics`)
- **Frontend / UI:** Embedded Classic Enterprise Monolith (HTML5 + Bootstrap 3.4 + jQuery 3.6)
- **Modernization Assessment:** Candidate for containerization, microservice decoupling, and modern frontend re-platforming (e.g., React/Angular/Vue).

## Requirements

- Java 8 or Java 17+
- Maven 3.8+

## How to Run

From the `productinventory` directory:

```bash
mvn spring-boot:run
```

Once started, access the application in your browser:

- **Web Dashboard:** [http://localhost:8080/](http://localhost:8080/)
- **H2 Database Console:** [http://localhost:8080/h2-console](http://localhost:8080/h2-console) (JDBC URL: `jdbc:h2:mem:inventorydb`, User: `sa`, Password: *(empty)*)
- **Actuator Health:** [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)

## REST API Endpoints

### 1. Products
- `GET /api/products` - List products (optional params: `search`, `categoryId`, `status`)
- `GET /api/products/{id}` - Get product by ID
- `POST /api/products` - Register a new product
- `PUT /api/products/{id}` - Update product details
- `DELETE /api/products/{id}` - Delete product
- `POST /api/products/{id}/adjust-stock` - Inbound restock, outbound dispatch, or audit count adjustments

### 2. Categories
- `GET /api/categories` - List all product categories
- `POST /api/categories` - Create new category

### 3. Dashboard Metrics
- `GET /api/dashboard/stats` - Total SKUs, total units, valuation, low-stock & out-of-stock counts
- `GET /api/dashboard/transactions` - Recent stock movement audit trail

## Running Tests

```bash
mvn clean test
```
