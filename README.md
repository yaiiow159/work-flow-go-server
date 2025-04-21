# WorkFlowGo Interview Tracking System

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.10-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue.svg)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Supported-blue.svg)](https://www.docker.com/)

WorkFlowGo is a comprehensive interview tracking system designed to help job seekers manage their interview process efficiently. This repository contains the backend server implementation built with Spring Boot.

## Features

- **Interview Management**: Create, update, and track interviews with detailed information
- **Document Storage**: Upload and manage documents like resumes and cover letters using Cloudinary
- **Email Notifications**: Receive timely reminders and updates about your interviews
- **Statistics Dashboard**: Get insights into your interview process with comprehensive statistics
- **User Settings**: Customize your experience with personalized settings
- **Security**: OAuth2 and JWT authentication to protect your data
- **Caching**: Improved performance with intelligent caching
- **Rate Limiting**: Protection against API abuse
- **Request Logging**: Comprehensive logging for debugging and monitoring
- **Docker Support**: Easy deployment with Docker and Docker Compose
- **Google OAuth2 Authentication**: Secure authentication with Google OAuth2
- **JWT Token-Based Authentication**: Token-based authentication for secure API access
- **RESTful API**: Well-designed API following REST principles

## Technology Stack

- **Java 21**: Core programming language
- **Spring Boot 3.3.10**: Application framework
- **Spring Data JPA**: Data access layer
- **PostgreSQL 16**: Relational database for data persistence
- **Cloudinary**: Cloud-based document storage
- **JavaMail API**: Email service integration
- **Swagger/OpenAPI**: API documentation
- **Spring Security with OAuth2**: Authentication and authorization
- **JWT**: Token-based authentication
- **Spring Cache (Caffeine)**: High-performance caching
- **Docker & Docker Compose**: Containerization and deployment
- **Maven**: Dependency management and build tool

## Code Quality & Best Practices

- **Constructor Injection**: Dependency injection is implemented using constructor injection instead of field injection for better testability and immutability
- **Lombok**: Reduces boilerplate code with annotations like @Data, @Getter, @Setter
- **Exception Handling**: Centralized exception handling with custom exceptions
- **DTO Pattern**: Data Transfer Objects for clean API contracts
- **Repository Pattern**: Separation of data access logic
- **Service Layer**: Business logic encapsulation
- **Caching**: Optimized performance with strategic caching
- **Unit & Integration Tests**: Comprehensive test coverage
- **Type Converters**: Custom type converters for complex data types
- **Validation**: Input validation using Bean Validation API

## Getting Started

### Prerequisites

- Java 21 or higher
- Maven 3.6 or higher
- PostgreSQL 16 or higher
- Docker (optional, for containerized deployment)

### Installation and Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/yaiiow159/work-flow-go-server.git
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
4. The application will be available at `http://localhost:8081/api`
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
   docker run -p 8081:8081 \
     -e SPRING_DATASOURCE_URL=jdbc:postgresql://<postgres-host>:5432/workflowgodb \
     -e SPRING_DATASOURCE_USERNAME=postgres \
     -e SPRING_DATASOURCE_PASSWORD=postgres \
     workflowgo-server
   ```

### Deployment on Render

This application is configured for deployment on Render using Docker. Follow these steps to deploy:

1. **Create a Render account**: Sign up at [render.com](https://render.com) if you don't have an account.

2. **Connect your GitHub repository**: Connect your GitHub account and select this repository.

3. **Create a new Web Service**:
   - Choose "Deploy from GitHub repo"
   - Select the repository
   - Select "Docker" as the runtime
   - The service will automatically use the `render.yaml` configuration

4. **Configure environment variables**: 
   - Render will automatically create and connect the PostgreSQL database
   - You'll need to manually add these secret environment variables:
     - `JWT_SECRET`: A secure random string for JWT token signing
     - `GOOGLE_CLIENT_ID`: Your Google OAuth client ID
     - `GOOGLE_CLIENT_SECRET`: Your Google OAuth client secret
     - `CLOUDINARY_CLOUD_NAME`: Your Cloudinary cloud name
     - `CLOUDINARY_API_KEY`: Your Cloudinary API key
     - `CLOUDINARY_API_SECRET`: Your Cloudinary API secret
     - `FRONTEND_URL`: URL of your frontend application
     - `ALLOWED_ORIGINS`: Comma-separated list of allowed origins for CORS
     - `MAIL_USERNAME`: Email service username
     - `MAIL_PASSWORD`: Email service password

5. **Deploy**: Click "Create Web Service" and Render will build and deploy your application.

6. **Update OAuth2 redirect URIs**: After deployment, update your Google OAuth2 configuration to include the new Render URL:
   - Add `https://your-render-service-name.onrender.com/api/oauth2/callback/google` to the authorized redirect URIs

The application will be available at `https://your-render-service-name.onrender.com/api`.

## API Documentation

Once the application is running, you can access the Swagger UI at:
```
http://localhost:8001/api/swagger-ui.html
```

This provides a comprehensive documentation of all available endpoints.

## Configuration

### Google OAuth2 Configuration

To enable Google OAuth2 authentication, you need to configure the following environment variables:

```
GOOGLE_CLIENT_ID=your-google-client-id
GOOGLE_CLIENT_SECRET=your-google-client-secret
```

You can obtain these credentials by creating a project in the [Google Developer Console](https://console.developers.google.com/):

1. Create a new project
2. Navigate to "Credentials" and create OAuth client ID
3. Configure the OAuth consent screen
4. Add authorized redirect URIs:
   - `http://localhost:8081/api/oauth2/callback/google` (for development)
   - `https://your-production-domain.com/api/oauth2/callback/google` (for production)

### Cloudinary Configuration

For document storage, you need to configure Cloudinary:

```
CLOUDINARY_CLOUD_NAME=your-cloud-name
CLOUDINARY_API_KEY=your-api-key
CLOUDINARY_API_SECRET=your-api-secret
```

You can obtain these credentials by creating an account on [Cloudinary](https://cloudinary.com/).

### Email Configuration

For email notifications, configure the following environment variables:

```
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
MAIL_PROPERTIES_MAIL_SMTP_AUTH=true
MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_ENABLE=true
```

For Gmail, you'll need to generate an App Password if you have 2FA enabled.

### JWT Configuration

For JWT token generation and validation:

```
JWT_SECRET=your-jwt-secret
JWT_EXPIRATION=864000000
```

The JWT_EXPIRATION is in milliseconds (default: 10 days).

## Project Structure

```
src/main/java/com/workflowgo/workflowgoserver/
├── config/                  # Configuration classes
├── controller/              # REST controllers
├── dto/                     # Data Transfer Objects
├── exception/               # Custom exceptions and handlers
├── model/                   # Entity classes
├── repository/              # JPA repositories
├── security/                # Security configurations
├── service/                 # Business logic services
└── WorkflowGoServerApplication.java  # Main application class
```

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contact

For any questions or suggestions, please open an issue in the GitHub repository.

---

Last updated: April 21, 2025
