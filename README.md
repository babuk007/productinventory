# Product Inventory - Spring Boot Application

Simple Spring Boot REST API project with one endpoint to return sample product details.

## Requirements

- Java 8
- Maven 3.9+

## Run the Application

```bash
mvn spring-boot:run
```

## API Endpoint

GET request:

```bash
http://localhost:8080/api/products
```

## Sample Response

```json
{
  "id": 1,
  "name": "Apple iPhone 15",
  "category": "Electronics",
  "price": 79999.0,
  "quantity": 25
}
```
