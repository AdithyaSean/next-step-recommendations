# API Documentation for Next-Step Recommendations Microservice

This document provides detailed information about the REST API endpoints exposed by the Next-Step Recommendations microservice. The API allows users to generate synthetic student data, train machine learning models, and predict career probabilities based on student profiles.

---

## **Base URL**
The base URL for all API endpoints is:
```
http://localhost:8080/recommendations
```

---

## **Endpoints**

### **1. Generate Synthetic Student Data**
Generates synthetic student profiles for training and testing purposes.

- **Endpoint**: `POST /generate`
- **Request Body**: None
- **Response**:
    - **Status Code**: `200 OK`
    - **Body**:
      ```json
      {
        "message": "Data generated successfully!"
      }
      ```

#### **Example Request:**
```bash
curl -X POST http://localhost:8080/recommendations/generate
```

---

### **2. Train Machine Learning Model**
Trains the machine learning model using the preprocessed data.

- **Endpoint**: `POST /train`
- **Request Body**: None
- **Response**:
    - **Status Code**: `200 OK`
    - **Body**:
      ```json
      {
        "message": "Model trained successfully!"
      }
      ```

#### **Example Request:**
```bash
curl -X POST http://localhost:8080/recommendations/train
```

---

### **3. Predict Career Probabilities**
Predicts career probabilities based on a student's educational profile.

- **Endpoint**: `POST /predict`
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
    - **Fields**:
        - **`educationLevel`**: The education level of the student (0: OL, 1: AL, 2: UNI).
        - **`olResults`**: A map of Ordinary Level subject scores, where the key is the subject ID and the value is the score.
        - **`alStream`**: The Advanced Level stream (if applicable). Use `null` if not applicable.
        - **`alResults`**: A map of Advanced Level subject scores (if applicable). Use `null` if not applicable.
        - **`gpa`**: The student's GPA (if applicable). Use `null` if not applicable.

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
curl -X POST http://localhost:8080/recommendations/predict \
-H "Content-Type: application/json" \
-d '{
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
}'
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
    "path": "/recommendations/predict"
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
    "path": "/recommendations/predict"
  }
  ```

---

## **Data Models**

### **PredictionRequest**
Represents the input data required for making a prediction.

- **Fields**:
    - **`educationLevel`**: `int` (0: OL, 1: AL, 2: UNI)
    - **`olResults`**: `Map<String, Double>` (Key: Subject ID, Value: Score)
    - **`alStream`**: `Integer` (Nullable)
    - **`alResults`**: `Map<String, Double>` (Nullable)
    - **`gpa`**: `Double` (Nullable)

#### **Example:**
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

---

## **Additional Notes**
- The API is built using **Spring Boot** and follows RESTful principles.
- The machine learning model is trained using **Weka** and uses a **Random Forest** classifier.
- The generated data is stored in ARFF format (`student_profiles.arff`), and the preprocessed data is saved in `features.arff`.

For more details on the machine learning models, refer to [docs/ml-models.md](docs/ml-models.md).

---

This documentation should help developers integrate with the Next-Step Recommendations microservice and understand its functionality. For further assistance, refer to the [development guide](docs/development.md).