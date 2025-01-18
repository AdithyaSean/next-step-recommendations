# API Documentation for Next-Step Frontend Microservice

This document provides detailed information about the API endpoints that the Next-Step frontend interacts with. The frontend communicates with the **Users Microservice** and **Recommendations Microservice** to manage user profiles and fetch career predictions.

---

## **Base URLs**
- **Users Microservice**: `http://localhost:8081/users`
- **Recommendations Microservice**: `http://localhost:8080/recommendations`

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

---

### **4. Delete a User**
Deactivates a user by their unique ID.

- **Endpoint**: `DELETE /users/{id}`
- **Response**:
    - **Status Code**: `204 No Content`

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

---

### **6. Generate Synthetic Student Data**
Generates synthetic student profiles for training and testing purposes.

- **Endpoint**: `POST /recommendations/generate`
- **Request Body**: None
- **Response**:
    - **Status Code**: `200 OK`
    - **Body**:
      ```json
      {
        "message": "Data generated successfully!"
      }
      ```

---

### **7. Train Machine Learning Model**
Trains the machine learning model using the preprocessed data.

- **Endpoint**: `POST /recommendations/train`
- **Request Body**: None
- **Response**:
    - **Status Code**: `200 OK`
    - **Body**:
      ```json
      {
        "message": "Model trained successfully!"
      }
      ```

---

### **8. Predict Career Probabilities (Recommendations Microservice)**
Predicts career probabilities based on a student's educational profile.

- **Endpoint**: `POST /recommendations/predict`
- **Request Body**:
  ```json
  {
    "educationLevel": 1,
    "olResults": {
      "0": 85.0,
      "1": 78.0,
      "2": 72.0
    },
    "alStream": 0,
    "alResults": {
      "0": 88.0,
      "1": 82.0,
      "2": 90.0
    },
    "gpa": 3.75
  }
  ```

### **Firebase Integration**
The **Users Microservice** integrates with Firebase for user authentication. The Firebase UID and token are stored in the `User` entity for authenticated users.

- **Fields**:
    - **`firebaseUid`**: The Firebase UID of the user.
    - **`firebaseToken`**: The Firebase token for the user.

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
  "updatedAt": "2023-10-01T12:00:00Z",
  "firebaseUid": "firebase-uid-123",
  "firebaseToken": "firebase-token-456"
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

## **Additional Notes**
- The frontend interacts with the **Users Microservice** for user management and the **Recommendations Microservice** for career predictions.
- The **Recommendations Microservice** must be running and accessible at `http://localhost:8080/recommendations`.
- The frontend uses Firebase for authentication and Firestore for storing user profiles.

For more details on the **Users Microservice** and **Recommendations Microservice**, refer to:
- [users-microservice-api.md](docs/users-microservice-api.md)
- [recommendation-microservice-api.md](docs/recommendation-microservice-api.md)

---

This documentation should help developers integrate the frontend with the backend microservices and understand the API interactions. For further assistance, refer to the [development guide](docs/development.md).

### Key Points:
1. **Base URLs**: The frontend interacts with two microservices, so both base URLs are provided.
2. **Endpoints**: The frontend will use endpoints from both the **Users Microservice** and **Recommendations Microservice**.
3. **Error Responses**: Standard error responses are documented for both microservices.
4. **Additional Notes**: The frontend uses Firebase for authentication and Firestore for storing user profiles, which is mentioned in the additional notes.

This documentation should help developers understand how the frontend interacts with the backend services.

