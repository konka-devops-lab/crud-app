# CRUD Application - Spring Boot Version

This is a Spring Boot conversion of the original Node.js CRUD application. It provides the same functionality with MySQL database integration and Redis caching.

## Features

- **CRUD Operations**: Create, Read, Delete entries
- **MySQL Database**: Persistent data storage using JPA/Hibernate
- **Redis Caching**: Performance optimization with Redis cache
- **CORS Support**: Configurable cross-origin resource sharing
- **Health Check**: Application health monitoring endpoint
- **Debug Endpoint**: Redis debugging capabilities

## API Endpoints

- `GET /health` - Health check endpoint
- `GET /api/entries` - Fetch all entries (with Redis caching)
- `POST /api/entries` - Create a new entry
- `DELETE /api/entries/{id}` - Delete an entry by ID
- `GET /debug/redis` - Debug Redis cache contents

## Environment Variables

### Database Configuration
- `DB_HOST` - MySQL database host (default: localhost)
- `DB_USER` - MySQL database username (default: root)
- `DB_PASSWORD` - MySQL database password (default: password)
- `DB_NAME` - MySQL database name (default: crud_db)

### Redis Configuration
- `REDIS_HOST` - Redis server host (default: localhost)
- `REDIS_PORT` - Redis server port (default: 6379)
- `REDIS_PASSWORD` - Redis password (optional)
- `REDIS_SSL` - Enable SSL for Redis (default: false)

### Server Configuration
- `PORT` - Server port (default: 8080)
- `HOST` - Server host (default: 0.0.0.0)
- `ALLOWED_ORIGIN` - CORS allowed origin (optional, allows all if not set)

## Running the Application

### Prerequisites
- Java 17 or higher
- MySQL database
- Redis server

### Local Development
```bash
# Build the application
./mvnw clean package

# Run the application
./mvnw spring-boot:run

# Or run the JAR file
java -jar target/crud-app-1.0.0.jar
```

### Docker
```bash
# Build the Docker image
docker build -t crud-app .

# Run the container
docker run -p 8080:8080 \
  -e DB_HOST=your-mysql-host \
  -e DB_USER=your-username \
  -e DB_PASSWORD=your-password \
  -e DB_NAME=your-database \
  -e REDIS_HOST=your-redis-host \
  crud-app
```

## Database Schema

The application expects a MySQL table named `entries` with the following structure:

```sql
CREATE TABLE entries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    amount DOUBLE NOT NULL,
    description VARCHAR(255) NOT NULL
);
```

The application will automatically create this table using JPA/Hibernate DDL auto-generation.

## Key Differences from Node.js Version

1. **Language**: Java instead of JavaScript
2. **Framework**: Spring Boot instead of Express.js
3. **ORM**: JPA/Hibernate instead of raw MySQL queries
4. **Dependency Injection**: Spring's IoC container
5. **Configuration**: Properties files instead of environment variables only
6. **Build System**: Maven instead of npm
7. **Packaging**: JAR file instead of Node.js modules

## Architecture

- **Controller Layer**: Handles HTTP requests and responses
- **Service Layer**: Contains business logic and caching
- **Repository Layer**: Data access using Spring Data JPA
- **Configuration**: Redis and CORS configuration
- **Model**: JPA entities representing database tables

The application maintains the same API contract as the original Node.js version, making it a drop-in replacement.

## Monitoring and Observability

The application includes comprehensive monitoring and observability features:

### Metrics (Prometheus/Grafana)
- **Application Metrics**: Custom metrics for entries created/deleted, cache hits/misses
- **HTTP Metrics**: Request duration, status codes, throughput
- **JVM Metrics**: Memory usage, garbage collection, thread pools
- **Database Metrics**: Connection pool usage, query performance
- **Redis Metrics**: Operation timing, connection status

### Logging (Fluentbit/ELK Stack)
- **Structured JSON Logging**: All logs in JSON format for easy parsing
- **Request Tracing**: Unique request IDs for tracking requests across services
- **Contextual Information**: Client IP, operation type, timing information
- **Log Levels**: Configurable logging levels for different components

### Health Checks
- **Application Health**: `/actuator/health` endpoint
- **Detailed Health**: Database and Redis connectivity checks
- **Custom Metrics Summary**: `/api/metrics/summary` for quick overview

### Available Endpoints for Monitoring

- `GET /actuator/health` - Application health status
- `GET /actuator/metrics` - All available metrics
- `GET /actuator/prometheus` - Prometheus-formatted metrics
- `GET /api/metrics/summary` - Custom metrics summary
- `GET /debug/redis` - Redis debugging information

### Running with Monitoring Stack

Use the provided Docker Compose file to run the complete monitoring stack:

```bash
# Start the application with monitoring
docker-compose -f docker-compose.monitoring.yml up -d

# Access the services
# Application: http://localhost:8080
# Prometheus: http://localhost:9090
# Grafana: http://localhost:3000 (admin/admin)
# Grafana Alloy: http://localhost:12345
```

### Monitoring Agents Supported

1. **Grafana Alloy**: Complete observability agent for metrics and logs
2. **Prometheus**: Metrics collection and alerting
3. **Fluentbit**: Log collection and forwarding
4. **Custom Metrics**: Built-in application-specific metrics

### Key Metrics to Monitor

- `crud_app_entries_created_total` - Total entries created
- `crud_app_entries_deleted_total` - Total entries deleted  
- `crud_app_cache_hits_total` - Redis cache hits
- `crud_app_cache_misses_total` - Redis cache misses
- `crud_app_database_query_duration` - Database query timing
- `crud_app_redis_operation_duration` - Redis operation timing
- `http_server_requests_seconds` - HTTP request metrics
- `jvm_memory_used_bytes` - JVM memory usage
- `hikaricp_connections_active` - Database connection pool usage

### Alerting Rules

The application includes pre-configured alerting rules for:
- Application downtime
- High error rates
- Slow response times
- Database connection issues
- High memory usage