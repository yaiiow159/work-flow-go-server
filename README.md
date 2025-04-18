# WorkFlowGo Interview Tracking System

WorkFlowGo is a comprehensive interview tracking system designed to help job seekers manage their interview process efficiently. This repository contains the backend server implementation built with Spring Boot.

## Features

- **Interview Management**: Create, update, and track interviews with detailed information
- **Document Storage**: Upload and manage documents like resumes and cover letters using Cloudinary
- **Statistics**: Get insights into your interview process with comprehensive statistics
- **User Settings**: Customize your experience with personalized settings
- **Security**: Basic authentication to protect your data
- **Caching**: Improved performance with intelligent caching
- **Rate Limiting**: Protection against API abuse
- **Request Logging**: Comprehensive logging for debugging and monitoring
- **Docker Support**: Easy deployment with Docker and Docker Compose

## Technology Stack

- **Java 21**: Core programming language
- **Spring Boot 3.3.10**: Application framework
- **Spring Data JPA**: Data access layer
- **PostgreSQL**: Relational database for data persistence
- **Cloudinary**: Cloud-based document storage
- **Swagger/OpenAPI**: API documentation
- **Spring Security**: Authentication and authorization
- **Spring Cache (Caffeine)**: High-performance caching
- **Docker & Docker Compose**: Containerization and deployment

## Getting Started

### Prerequisites

- Java 21 or higher
- Maven 3.6 or higher
- PostgreSQL 12 or higher

### Installation and Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/work-flow-go-server.git
   cd work-flow-go-server
   ```

2. Create PostgreSQL database:
   ```sql
   CREATE DATABASE workflowgodb;
   ```

3. Configure database connection in `src/main/resources/application.yml` if needed:
   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5432/workflowgodb
       username: postgres
       password: postgres
   ```

4. Build the application:
   ```bash
   mvn clean install
   ```

5. Run the application:
   ```bash
   mvn spring-boot:run
   ```

The server will start on port 8001 by default. The API will be available at `http://localhost:8001/api`.

### Docker Deployment

The application supports Docker deployment for easy setup and deployment:

#### Using Docker Compose (Recommended)

1. Make sure Docker and Docker Compose are installed on your system
2. Navigate to the project root directory
3. Run the following command to build and start the containers:
   ```bash
   docker-compose up -d
   ```
4. The application will be available at `http://localhost:8001/api`
5. To stop the containers:
   ```bash
   docker-compose down
   ```

#### Using Docker Directly

1. Build the Docker image:
   ```bash
   docker build -t workflowgo-server .
   ```

2. Run the container (make sure PostgreSQL is available):
   ```bash
   docker run -p 8001:8001 \
     -e SPRING_DATASOURCE_URL=jdbc:postgresql://<postgres-host>:5432/workflowgodb \
     -e SPRING_DATASOURCE_USERNAME=postgres \
     -e SPRING_DATASOURCE_PASSWORD=postgres \
     workflowgo-server
   ```

### API Documentation

Once the application is running, you can access the Swagger UI at:
```
http://localhost:8001/api/swagger-ui.html
```

This provides a comprehensive documentation of all available endpoints.

## API Endpoints

### Interviews

- `GET /interviews`: Get all interviews with optional filtering
- `GET /interviews/{id}`: Get a specific interview by ID
- `POST /interviews`: Create a new interview
- `PUT /interviews/{id}`: Update an existing interview
- `PATCH /interviews/{id}/status`: Update only the status of an interview
- `DELETE /interviews/{id}`: Delete an interview

### Documents

- `GET /documents`: Get all documents
- `GET /documents/{id}`: Get document metadata by ID
- `POST /documents`: Upload a new document
- `DELETE /documents/{id}`: Delete a document

### User Settings

- `GET /user/settings`: Get user settings
- `PUT /user/settings`: Update user settings
- `POST /user/settings/reset`: Reset user settings to defaults
- `GET /user/export`: Export all user data

### Statistics

- `GET /statistics/interviews`: Get interview statistics

## Configuration

The application can be configured through the `application.yml` file. Key configurations include:

- **Database**: Configure database connection details
- **Cloudinary**: Set up your Cloudinary credentials for document storage
- **Server**: Configure server port and context path

## License

This project is licensed under the MIT License - see the LICENSE file for details.
