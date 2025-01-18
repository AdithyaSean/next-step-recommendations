# API Documentation for Next-Step Users Microservice

This document provides detailed information about the REST API endpoints exposed by the Next-Step Users microservice. The API allows users to manage user profiles, including students and institutions, and interact with the **Recommendations Microservice** for career predictions.

---

## **Base URL**
The base URL for all API endpoints is:
```
http://localhost:8080/users
```

---

## **Endpoints**

### **1. Create a User**
Creates a new user (student or institution).

- **Endpoint**: `POST /users`
- **Request Body**:
  ```json
  {
    "name": "John Doe",
    "email": "john.doe@example.com",
    "password": "password123",
    "role": "STUDENT"
  }
  ```
    - **Fields**:
        - **`name`**: The name of the user.
        - **`email`**: The email address of the user (must be unique).
        - **`password`**: The password for the user.
        - **`role`**: The role of the user (`STUDENT` or `INSTITUTION`).

- **Response**:
    - **Status Code**: `201 Created`
    - **Body**:
      ```json
      {
        "id": "550e8400-e29b-41d4-a716-446655440000",
        "name": "John Doe",
        "email": "john.doe@example.com",
        "role": "STUDENT",
        "active": true
      }
      ```

#### **Example Request:**
```bash
curl -X POST http://localhost:8080/users \
-H "Content-Type: application/json" \
-d '{
  "name": "John Doe",
  "email": "john.doe@example.com",
  "password": "password123",
  "role": "STUDENT"
}'
```

---

### **2. Get User by ID**
Retrieves a user by their unique ID.

- **Endpoint**: `GET /users/{id}`
- **Response**:
    - **Status Code**: `200 OK`
    - **Body**:
      ```json
      {
        "id": "550e8400-e29b-41d4-a716-446655440000",
        "name": "John Doe",
        "email": "john.doe@example.com",
        "role": "STUDENT",
        "active": true
      }
      ```

#### **Example Request:**
```bash
curl -X GET http://localhost:8080/users/550e8400-e29b-41d4-a716-446655440000
```

---

### **3. Get All Users**
Retrieves a list of all users.

- **Endpoint**: `GET /users`
- **Response**:
    - **Status Code**: `200 OK`
    - **Body**:
      ```json
      [
        {
          "id": "550e8400-e29b-41d4-a716-446655440000",
          "name": "John Doe",
          "email": "john.doe@example.com",
          "role": "STUDENT",
          "active": true
        },
        {
          "id": "550e8400-e29b-41d4-a716-446655440001",
          "name": "Jane Smith",
          "email": "jane.smith@example.com",
          "role": "INSTITUTION",
          "active": true
        }
      ]
      ```

#### **Example Request:**
```bash
curl -X GET http://localhost:8080/users
```

---

### **4. Delete a User**
Deactivates a user by their unique ID.

- **Endpoint**: `DELETE /users/{id}`
- **Response**:
    - **Status Code**: `204 No Content`

#### **Example Request:**
```bash
curl -X DELETE http://localhost:8080/users/550e8400-e29b-41d4-a716-446655440000
```

---

### **5. Predict Career Probabilities**
Predicts career probabilities for a student based on their educational profile. This endpoint interacts with the **Recommendations Microservice**.

- **Endpoint**: `POST /users/{id}/predict`
- **Request Body**: None (uses the student's profile data stored in the database).
- **Response**:
    - **Status Code**: `200 OK`
    - **Body**:
      ```json
      {
        "Engineering": 85.3,
        "Medicine": 72.1,
        "IT": 68.5,
        "Business": 45.2,
        "Teaching": 30.7,
        "Research": 55.8
      }
      ```

#### **Example Request:**
```bash
curl -X POST http://localhost:8081/users/550e8400-e29b-41d4-a716-446655440000/predict
```

---

## **Error Responses**

### **400 Bad Request**
- **Cause**: Invalid input data (e.g., missing required fields, invalid values).
- **Response**:
  ```json
  {
    "timestamp": "2023-10-01T12:00:00Z",
    "status": 400,
    "error": "Bad Request",
    "message": "Invalid input data",
    "path": "/users"
  }
  ```

### **404 Not Found**
- **Cause**: User not found.
- **Response**:
  ```json
  {
    "timestamp": "2023-10-01T12:00:00Z",
    "status": 404,
    "error": "Not Found",
    "message": "User not found",
    "path": "/users/550e8400-e29b-41d4-a716-446655440000"
  }
  ```

### **500 Internal Server Error**
- **Cause**: An unexpected error occurred during processing.
- **Response**:
  ```json
  {
    "timestamp": "2023-10-01T12:00:00Z",
    "status": 500,
    "error": "Internal Server Error",
    "message": "An unexpected error occurred",
    "path": "/users"
  }
  ```

---

## **Data Models**

### **User**
Represents a user in the system.

- **Fields**:
    - **`id`**: `UUID` (Unique identifier for the user)
    - **`name`**: `String` (Name of the user)
    - **`email`**: `String` (Email address of the user)
    - **`password`**: `String` (Password for the user)
    - **`role`**: `UserRole` (Role of the user: `STUDENT` or `INSTITUTION`)
    - **`active`**: `boolean` (Whether the user is active)
    - **`createdAt`**: `LocalDateTime` (Timestamp when the user was created)
    - **`updatedAt`**: `LocalDateTime` (Timestamp when the user was last updated)

#### **Example:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "John Doe",
  "email": "john.doe@example.com",
  "password": "password123",
  "role": "STUDENT",
  "active": true,
  "createdAt": "2023-10-01T12:00:00Z",
  "updatedAt": "2023-10-01T12:00:00Z"
}
```

### **CORS Configuration**
CORS (Cross-Origin Resource Sharing) is configured to allow requests from any origin. This is handled by the `CorsConfig` class.

#### **Example:**
```java
@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true)
                        .maxAge(3600);
            }
        };
    }
}
```

### **Global Exception Handling**
Global exception handling is configured to handle common exceptions such as `EntityNotFoundException`, `IllegalArgumentException`, and validation errors.

#### **Example:**
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleEntityNotFoundException(EntityNotFoundException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage())
        );
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
```

### **Conclusion**
These updates ensure that the documentation reflects the current state of the system, including the integration with Firebase, CORS configuration, and security settings. For further details, refer to the [development guide](docs/development.md).

---

## **Additional Notes**
- The API is built using **Spring Boot** and follows RESTful principles.
- The **Users Microservice** interacts with the **Recommendations Microservice** via HTTP requests for career predictions.
- The **Recommendations Microservice** must be running and accessible at `http://localhost:8080/recommendations`.

For more details on the **Recommendations Microservice**, refer to [docs/recommendation-microservice-api.md](docs/recommendation-microservice-api.md).

---

This documentation should help developers integrate with the Next-Step Users microservice and understand its functionality. For further assistance, refer to the [development guide](docs/development.md).

---

### **Integration with Recommendations Microservice**

To enable the **Users Microservice** to interact with the **Recommendations Microservice**, you need to add a `RestTemplate` or `WebClient` in the `UserService` class to make HTTP requests. Below is an example of how to implement the `predictCareerProbabilities` method in the `UserService` class:

```java
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    @Transactional(readOnly = true)
    public Map<String, Double> predictCareerProbabilities(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (user.getRole() != UserRole.STUDENT) {
            throw new IllegalArgumentException("Only students can have career predictions");
        }

        Student student = (Student) user;

        PredictionRequest request = new PredictionRequest();
        request.setEducationLevel(student.getEducationLevel());
        request.setOlResults(student.getOlResults());
        request.setAlStream(student.getStream());
        request.setAlResults(student.getAlResults());
        request.setGpa(student.getGpa());

        String url = "http://localhost:8080/recommendations/predict";
        return restTemplate.postForObject(url, request, Map.class);
    }
}
```

This method retrieves the student's profile, constructs a `PredictionRequest`, and sends it to the **Recommendations Microservice** for career probability predictions.


