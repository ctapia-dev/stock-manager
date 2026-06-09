# Stock Manager API

Sistema backend de gestión de inventario desarrollado con Spring Boot, diseñado con arquitectura empresarial real.

## Tecnologías

- **Java 17** + **Spring Boot 3.5**
- **Spring Security** + **JWT** — autenticación stateless con roles
- **Spring Data JPA** + **PostgreSQL** — persistencia relacional
- **WebSockets (STOMP)** — alertas de stock en tiempo real
- **Redis** — caché de listados con TTL
- **Docker + docker-compose** — entorno reproducible en un comando
- **Swagger / OpenAPI 3** — documentación interactiva
- **JUnit 5 + Mockito** — tests unitarios

## Features

- Autenticación JWT con tres roles: `ADMIN`, `WAREHOUSE`, `SELLER`
- CRUD completo de Categorías, Proveedores y Productos
- Control de movimientos de stock (entrada, salida, ajuste)
- **Alertas en tiempo real** vía WebSocket cuando el stock baja del mínimo
- Caché Redis en listado de productos (TTL 10 min)
- Soft delete — los registros nunca se borran físicamente
- Paginación y ordenamiento en listados
- Reporte automático diario con `@Scheduled`
- Documentación completa en Swagger UI

## Requisitos

- Java 17+
- Docker Desktop

## ⚡ Levantar el proyecto

```bash
# 1. Clonar el repositorio
git clone https://github.com/ctapia-dev/stock-manager.git
cd stock-manager

# 2. Levantar PostgreSQL y Redis
docker-compose up -d

# 3. Correr la aplicación
./mvnw spring-boot:run
```

## Documentación

Una vez levantado, accede a Swagger UI:

```
http://localhost:8080/api/swagger-ui/index.html
```

## Endpoints principales

### Auth (público)
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/auth/register` | Registrar usuario |
| POST | `/api/auth/login` | Login → retorna JWT |

### Productos (requiere JWT)
| Método | Endpoint | Rol requerido |
|--------|----------|---------------|
| GET | `/api/products` | Todos |
| POST | `/api/products` | ADMIN, WAREHOUSE |
| PUT | `/api/products/{id}` | ADMIN, WAREHOUSE |
| DELETE | `/api/products/{id}` | ADMIN |

### Stock Movements (requiere JWT)
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/stock-movements` | Registrar movimiento IN/OUT/ADJUSTMENT |
| GET | `/api/stock-movements` | Historial completo |
| GET | `/api/stock-movements/product/{id}` | Historial por producto |

##  Arquitectura

```
controller/     → recibe requests HTTP, valida roles
service/        → lógica de negocio
repository/     → acceso a datos (Spring Data JPA)
entity/         → modelos JPA (tablas)
dto/            → objetos de transferencia (request/response)
security/       → JWT filter y configuración
config/         → beans de configuración
scheduler/      → tareas automáticas
websocket/      → configuración STOMP
```

## Tests

```bash
./mvnw test
```

12 tests unitarios cubriendo servicios principales con Mockito.

##  Autora

**Constanza Tapia** — [ctapia-dev.github.io](https://ctapia-dev.github.io)