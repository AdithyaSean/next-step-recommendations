# Development Documentation: Next Step Recommendations Microservice

## Overview

The **Next Step Recommendations** microservice is responsible for providing career predictions based on student profiles. It integrates with the **Next Step Users** microservice to fetch student data and uses pre-trained machine learning models hosted in a Python service to generate career predictions. The microservice exposes REST endpoints for real-time predictions and batch predictions.

## Features

- **Real-time Career Predictions**: Predict career probabilities for a given student profile.
- **Batch Predictions**: Predict career probabilities for multiple student profiles in a single request.
- **Model Versioning**: Support for multiple versions of the trained model.
- **Caching**: Cache frequently requested predictions to improve performance.

## Tech Stack

- **Spring Boot**: For building the microservice.
- **Python ML Service**: Hosts pre-trained models (`career_predictor.joblib`, `model_metadata.joblib`, etc.) for predictions.
- **REST API**: Exposes endpoints for predictions.
- **Caching**: Spring Cache for caching predictions.

## Prerequisites

1. **Python Environment**: Ensure Python 3.12+ is installed with the required dependencies (`scikit-learn`, `pandas`, `numpy`, `joblib`).
2. **Spring Boot Application**: A Spring Boot application with REST API endpoints for handling predictions.
3. **Python ML Service**: A Python service (e.g., Flask or FastAPI) that hosts the pre-trained models and exposes prediction endpoints.

## Steps to Integrate ML Models with Spring Boot

### 1. Train and Export the Model

Run the following commands to generate the model files:

```bash
# Generate synthetic data
python -m main generate

# Preprocess the data
python -m main process

# Train the model
python -m main train
```

This will generate the following files in the `models` directory:
- `career_predictor.joblib`: The trained RandomForestRegressor model.
- `model_metadata.joblib`: Metadata containing feature names, career names, and best parameters.
- `feature_selector.joblib`: The feature selector used during training.
- `scaler.joblib`: The scaler used for preprocessing.

### 2. Set Up Python ML Service

Create a Python service (e.g., using Flask or FastAPI) to host the pre-trained models and expose prediction endpoints.

#### Example: `app.py`

```python
from flask import Flask, request, jsonify
import joblib
import numpy as np

app = Flask(__name__)

# Load the trained model and metadata
model = joblib.load("models/career_predictor.joblib")
metadata = joblib.load("models/model_metadata.joblib")
scaler = joblib.load("models/scaler.joblib")

@app.route('/predict', methods=['POST'])
def predict():
    data = request.json
    education_level = data['educationLevel']
    ol_results = data['olResults']
    al_stream = data.get('alStream', -1)
    al_results = data.get('alResults', {})
    gpa = data.get('gpa', -1)

    # Create feature vector
    features = np.full(len(metadata['feature_names']), -1.0)
    features[metadata['feature_order']['education_level']] = education_level
    features[metadata['feature_order']['AL_stream']] = al_stream
    features[metadata['feature_order']['gpa']] = gpa

    for subject_id, score in ol_results.items():
        col_name = f"OL_subject_{subject_id}_score"
        if col_name in metadata['feature_order']:
            features[metadata['feature_order'][col_name]] = score

    for subject_id, score in al_results.items():
        col_name = f"AL_subject_{subject_id}_score"
        if col_name in metadata['feature_order']:
            features[metadata['feature_order'][col_name]] = score

    # Scale features
    scaled_features = scaler.transform([features])

    # Make prediction
    predictions = model.predict(scaled_features)[0]

    # Return career probabilities
    career_probabilities = {
        career: float(np.clip(prob * 100, 0, 100))
        for career, prob in zip(metadata['career_names'], predictions)
    }

    return jsonify(career_probabilities)

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
```

### 3. Create a Spring Boot Service for Predictions

Create a Spring Boot service that communicates with the Python ML service to get predictions.

#### Example: `PredictionService.java`

```java
@Service
public class PredictionService {

    private final RestTemplate restTemplate;

    @Autowired
    public PredictionService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Map<String, Double> predictCareerProbabilities(StudentProfile profile) {
        String url = "http://localhost:5000/predict";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("educationLevel", profile.getEducationLevel());
        requestBody.put("olResults", profile.getOlResults());
        requestBody.put("alStream", profile.getAlStream());
        requestBody.put("alResults", profile.getAlResults());
        requestBody.put("gpa", profile.getGpa());

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        return restTemplate.postForObject(url, request, Map.class);
    }
}
```

### 4. Create a REST Controller for Predictions

Create a REST controller to expose the prediction functionality.

#### Example: `PredictionController.java`

```java
@RestController
@RequestMapping("/predictions")
public class PredictionController {

    private final PredictionService predictionService;

    @Autowired
    public PredictionController(PredictionService predictionService) {
        this.predictionService = predictionService;
    }

    @PostMapping("/career")
    public Map<String, Double> predictCareer(@RequestBody StudentProfile profile) {
        return predictionService.predictCareerProbabilities(profile);
    }
}
```

### 5. Define the Student Profile Model

Define a `StudentProfile` class to represent the input data for predictions.

#### Example: `StudentProfile.java`

```java
public class StudentProfile {
    private int educationLevel;
    private int alStream;
    private double gpa;
    private Map<String, Double> olResults;
    private Map<String, Double> alResults;

    // Getters and setters
}
```

### 6. Test the Integration

Run the Spring Boot application and test the prediction endpoint using a tool like Postman or cURL.

#### Example Request:

```json
{
  "educationLevel": 1,
  "alStream": 0,
  "gpa": 3.75,
  "olResults": {
    "0": 85,
    "1": 78,
    "2": 72,
    "3": 65,
    "4": 70,
    "5": 75
  },
  "alResults": {
    "0": 88,
    "1": 82,
    "2": 90
  }
}
```

#### Example Response:

```json
{
  "Engineering": 85.3,
  "Medicine": 78.5,
  "IT": 72.1,
  "Business": 65.4,
  "Teaching": 70.2,
  "Research": 75.8
}
```

## Conclusion

By following these steps, you can integrate the Python-trained ML models into a Spring Boot application via a Python ML service. This setup allows the Spring Boot application to leverage the power of the trained RandomForestRegressor model while maintaining a clean separation between the ML pipeline and the application logic.

For further improvements, consider:
- Adding model versioning and monitoring.
- Implementing caching for frequently requested predictions.
- Enhancing the API with additional features like batch predictions.