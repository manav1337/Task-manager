


# **Task Management System - Backend Documentation**

## **Project Overview**

A scalable REST API for a Task Management System with JWT authentication and role-based access control.\
This system provides secure user management and task operations with comprehensive API documentation.

***


## **Table of Contents**

1. [Technology Stack]

2. [System Architecture]

3. [Database Schema]

4. [Authentication & Security]

5. [API Documentation]

6. [Setup & Installation]

7. [Deployment & Scalability]

***


## **Technology Stack**

### **Backend**

- **Framework**: Spring Boot 3.x

- **Language**: Java 17

- **Security**: Spring Security 6.x with JWT

- **Database**: MySql

- **Documentation**: Swagger/OpenAPI 3.0

- **Build Tool**: Maven


### **Frontend**

- **Framework**: React.js 18

- **State Management**: React Context API

- **HTTP Client**: Axios

- **Styling**: CSS3 with modern layout


### **Development**

- **API Testing**: Postman Collection

- **Version Control**: Git

***


## **System Architecture**

### **High-Level Architecture**

    Client (React Frontend)
        ↓ HTTPS
    API Gateway (Spring Boot)
        ↓
    Authentication Filter (JWT)
        ↓
    Security Context
        ↓
    Controller Layer → Service Layer → Repository Layer → Database


### **Package Structure**

    src/main/java/com/taskmanager/
    ├── config/          # Security & Application Config
    ├── controller/      # REST Controllers
    ├── service/         # Business Logic
    ├── repository/      # Data Access Layer
    ├── entity/          # JPA Entities
    ├── dto/             # Data Transfer Objects
    ├── security/        # JWT & Security Config
    └── exception/       # Global Exception Handling

***


## **Database Schema**

### **Users Table**

```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'ROLE_USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```


### **Tasks Table**

```sql
CREATE TABLE tasks (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    completed BOOLEAN DEFAULT FALSE,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```


### **Entity Relationships**

- **User 1:N Task** – One user can have multiple tasks

- **Cascade Delete** – User deletion automatically removes their tasks

***


## **Authentication & Security**

### **JWT Authentication Flow**

1. **User Registration**

   - Password hashing using BCrypt (strength: 12)

   - Input validation and sanitization

   - Unique constraint enforcement for username/email

2. **User Login**

   - Credential validation against hashed passwords

   - JWT token generation with user details and roles

   - Token expiration configuration (24 hours)

3. **Request Authentication**

   - JWT token extraction from Authorization header

   - Token validation and signature verification

   - Security context establishment with user roles

***


### **Security Implementation**

#### **Password Hashing**

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12);
}
```


#### **JWT Token Configuration**

```properties
# JWT Configuration
jwt.secret=your-512-bit-secret-key-here-must-be-at-least-32-characters-long
jwt.expiration=86400000 # 24 hours in milliseconds
```


#### **Role-Based Access Control**

```java
public enum Role {
    ROLE_USER,
    ROLE_ADMIN
}
```


#### **Security Filter Chain**

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/tasks/**").hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
```

***


## **API Documentation**

### **Base Information**

- **Base URL**: `http://localhost:8080/api`

- **API Version**: v1 (implicit)

- **Content Type**: `application/json`

- **Authentication**: Bearer Token (JWT)

***


### **Authentication Endpoints**

#### **1. User Registration**

**Endpoint**: `POST /api/auth/register`

**Request Body**:

```json
{
    "username": "john_doe",
    "email": "john@example.com",
    "password": "SecurePassword123"
}
```

**Response**:

```json
{
    "message": "User registered successfully",
    "user": {
        "id": 1,
        "username": "john_doe",
        "email": "john@example.com",
        "role": "ROLE_USER"
    }
}
```

**Status Codes**:

- `201 Created` – User registered successfully

- `400 Bad Request` – Validation errors or duplicate user

- `500 Internal Server Error` – Server error during registration

***


#### **2. User Login**

**Endpoint**: `POST /api/auth/login`

**Request Body**:

```json
{
    "username": "john_doe",
    "password": "SecurePassword123"
}
```

**Response**:

```json
{
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "type": "Bearer",
    "id": 1,
    "username": "john_doe",
    "email": "john@example.com",
    "role": "ROLE_USER"
}
```

**Status Codes**:

- `200 OK` – Login successful

- `401 Unauthorized` – Invalid credentials

- `400 Bad Request` – Missing required fields

***


### **Task Management Endpoints**

#### **3. Get User Tasks**

**Endpoint**: `GET /api/tasks`

**Headers**:

    Authorization: Bearer <jwt-token>

**Response**:

```json
[
    {
        "id": 1,
        "title": "Complete project documentation",
        "description": "Write comprehensive API documentation",
        "completed": false,
        "createdAt": "2024-01-15T10:30:00Z",
        "updatedAt": "2024-01-15T10:30:00Z"
    }
]
```

***


#### **4. Create Task**

**Endpoint**: `POST /api/tasks`

**Headers**:

    Authorization: Bearer <jwt-token>
    Content-Type: application/json

**Request Body**:

```json
{
    "title": "New Task",
    "description": "Task description here"
}
```

**Response**:

```json
{
    "id": 2,
    "title": "New Task",
    "description": "Task description here",
    "completed": false,
    "createdAt": "2024-01-15T11:00:00Z",
    "updatedAt": "2024-01-15T11:00:00Z"
}
```

***


#### **5. Update Task**

**Endpoint**: `PUT /api/tasks/{id}`

**Headers**:

    Authorization: Bearer <jwt-token>
    Content-Type: application/json

**Request Body**:

```json
{
    "title": "Updated Task Title",
    "description": "Updated description",
    "completed": true
}
```

***


#### **6. Delete Task**

**Endpoint**: `DELETE /api/tasks/{id}`

**Headers**:

    Authorization: Bearer <jwt-token>

**Response**: `204 No Content`

***


### **Admin Endpoints**

#### **7. Get All Users (Admin Only)**

**Endpoint**: `GET /api/admin/users`

**Headers**:

    Authorization: Bearer <jwt-token>

**Response**:

```json
[
    {
        "id": 1,
        "username": "john_doe",
        "email": "john@example.com",
        "role": "ROLE_USER",
        "taskCount": 5,
        "createdAt": "2024-01-10T08:00:00Z"
    },
    {
        "id": 2,
        "username": "admin_user",
        "email": "admin@example.com",
        "role": "ROLE_ADMIN",
        "taskCount": 2,
        "createdAt": "2024-01-09T09:00:00Z"
    }
]
```

***


#### **8. Get All Tasks (Admin Only)**

**Endpoint**: `GET /api/admin/tasks`

**Headers**:

    Authorization: Bearer <jwt-token>

**Response**:

```json
[
    {
        "id": 1,
        "title": "User Task",
        "description": "Task description",
        "completed": false,
        "user": {
            "id": 1,
            "username": "john_doe",
            "email": "john@example.com"
        },
        "createdAt": "2024-01-15T10:30:00Z",
        "updatedAt": "2024-01-15T10:30:00Z"
    }
]
```

***


#### **9. Get Dashboard Statistics (Admin Only)**

**Endpoint**: `GET /api/admin/stats`

**Headers**:

    Authorization: Bearer <jwt-token>

**Response**:

```json
{
    "totalUsers": 150,
    "totalTasks": 450,
    "activeUsers": 120,
    "completedTasks": 280
}
```

***


#### **10. Delete User (Admin Only)**

**Endpoint**: `DELETE /api/admin/users/{id}`

**Headers**:

    Authorization: Bearer <jwt-token>

**Response**: `204 No Content`

***


## **Error Handling**

### **Standard Error Response Format**

```json
{
    "timestamp": "2024-01-15T10:30:00Z",
    "status": 400,
    "error": "Bad Request",
    "message": "Validation failed",
    "path": "/api/auth/register",
    "details": [
        {
            "field": "email",
            "message": "Email must be valid"
        }
    ]
}
```


### **Common HTTP Status Codes**

- `200 OK` – Successful request

- `201 Created` – Resource created successfully

- `400 Bad Request` – Validation errors

- `401 Unauthorized` – Invalid or missing authentication

- `403 Forbidden` – Insufficient permissions

- `404 Not Found` – Resource not found

- `500 Internal Server Error` – Server-side error

***


## **Setup & Installation**

### **Prerequisites**

- Java 17 or higher

- MySQL 7 or higher

- Maven 3.6 or higher

- Node.js 16 or higher (for frontend)

***


### **Backend Setup**

1. **Clone Repository**

   ```bash
   git clone <repository-url>
   cd task-management-backend
   ```

2. **Database Configuration**

   ```properties
   # application.properties
   spring.datasource.url=jdbc:jdbc:mysql://localhost:3306/taskdb
   spring.datasource.username=MySql
   spring.datasource.password=yourpassword
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.show-sql=true
   ```

3. **Build and Run**

   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

***


### **Frontend Setup**

1. **Navigate to Frontend Directory**

   ```bash
   cd frontend
   ```

2. **Install Dependencies**

   ```bash
   npm install
   ```

3. **Configure API Base URL**

   ```javascript
   // src/services/api.js
   const API = axios.create({
     baseURL: 'http://localhost:8080/api',
   });
   ```

4. **Start Development Server**

   ```bash
   npm start
   ```

***


## **Deployment & Scalability**

### **Scalability Features**

1. **Stateless Architecture**

   - JWT-based authentication enables horizontal scaling

   - No server-side session storage

2. **Database Optimization**

   - Indexed foreign keys for faster joins

   - Connection pooling with HikariCP

   - Query optimization with JPA hints

3. **Caching Strategy**

   ```java
   @Configuration
   @EnableCaching
   public class CacheConfig {
       @Bean
       public CacheManager cacheManager() {
           return new ConcurrentMapCacheManager("users", "tasks");
       }
   }
   ```

4. **Microservices Readiness**

   - Modular package structure

   - Independent business domains

   - API versioning support

***


### **Production Considerations**

1. **Security Enhancements**

   - Environment-based JWT secret management

   - CORS configuration for production domains

   - Rate limiting implementation

   - HTTPS enforcement

2. **Monitoring & Logging**

   - Structured JSON logging

   - Health check endpoints

   - Metrics collection with Micrometer

3. **Performance Optimization**

   - Database connection pooling

   - Static content CDN delivery

   - API response compression

***


### **Environment Configuration**

```properties
# application-prod.properties
spring.profiles.active=prod
spring.datasource.url=${DATABASE_URL}
jwt.secret=${JWT_SECRET}
logging.level.com.taskmanager=INFO
server.compression.enabled=true
```

***


## **API Testing**

### **Using Swagger UI**

Access API documentation at:\
`http://localhost:8080/swagger-ui.html`


### **Postman Collection**

Import the provided Postman collection for comprehensive API testing:

- Authentication flows

- CRUD operations

- Error scenarios

- Admin functionalities

***


### **Sample Test Cases**

1. **User Registration Validation**

   - Test duplicate username/email

   - Test password strength validation

   - Test required field validation

2. **Authentication Flow**

   - Test successful login/logout

   - Test token expiration

   - Test protected endpoint access

3. **Role-Based Access**

   - Test user vs admin permissions

   - Test unauthorized access attempts

   - Test resource ownership validation

***


## **Conclusion**

This Task Management System demonstrates modern backend development practices with Spring Boot — including secure authentication, role-based access control, and scalable architecture.

The API follows REST principles with comprehensive error handling and documentation.\
The system is production-ready, secure, optimized, and modular for future scalability and microservice adaptation.

