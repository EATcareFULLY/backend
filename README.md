# EATcareFULLY Backend

A Spring Boot 3 backend service that processes food product data. Features include:
- Integration with OpenFoodFacts database for product information
- Image preprocessing using OpenCV and Tesseract OCR for text extraction
- Large Language Model integration for advanced data analysis
- Keycloak for security and authentication
- RESTful API with Swagger documentation
- PostgreSQL database for data persistence

## Quick Start

### Prerequisites
- Java 17+
- Maven
- Docker & Docker Compose


### Starting and stopping Docker containers
```bash
# Make sure to remove outdated containers and start the containers as a background process
docker compose down --volumes; docker compose up -d
```

```bash
# Stop and remove all containers
docker compose down
```

### Running App locally
```bash
mvn spring-boot:run
```

### Running without security (development mode)
```
It is not possible to run this from IntelliJ's RunMarkdown. 
To run development mode execute the following command from the project directory:

- Windows Powershell: $env:SECURITY_ENABLED='false'; mvn spring-boot:run
- Command Prompt: set SECURITY_ENABLED=false && mvn spring-boot:run
- Git Bash: SECURITY_ENABLED=false mvn spring-boot:run
```

**Note:** Disabling security should only be done in development environments.

### Stopping the app
Press `Ctrl + C` and confirm with `y` to stop the Spring Boot application

## Documentation
API documentation is available via Swagger UI at:
http://localhost:8081/api/swagger-ui/index.html

## Database Model
![Database ERD](diagram_generated_by_IDE_.png)

