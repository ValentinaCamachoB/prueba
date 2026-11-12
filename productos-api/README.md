# productos-api

API REST de productos (Spring Boot 3 + Java 17) con pruebas unitarias (JUnit 5 + Mockito + MockMvc).

## Requisitos
- Java 17
- Maven 3.8+ (o abrir el proyecto en IntelliJ / VS Code / Eclipse)

## Correr las pruebas
    mvn test

Solo una clase:

    mvn -Dtest=ProductoServiceTest test
    mvn -Dtest=ProductoControllerTest test

## Levantar la API
    mvn spring-boot:run

Queda en http://localhost:8080/api/productos

## Pruebas rápidas con curl
    curl -X POST http://localhost:8080/api/productos -H "Content-Type: application/json" -d '{"nombre":"Mouse","precio":25000,"stock":10}'
    curl http://localhost:8080/api/productos
    curl -i http://localhost:8080/api/productos/99        # 404
    curl -i -X POST http://localhost:8080/api/productos -H "Content-Type: application/json" -d '{"nombre":"","precio":25000,"stock":10}'   # 400
