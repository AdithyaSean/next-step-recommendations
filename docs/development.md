# Development Documentation: Next Step Recommendations Microservice

## Overview

The **Next Step Recommendations** microservice is responsible for hosting the pre-trained career prediction model and providing real-time career recommendations based on student profiles. The microservice integrates with the **Next Step Users** microservice to fetch student data and uses the pre-trained machine learning model to generate career predictions. The microservice exposes REST endpoints for real-time predictions and batch predictions.

## Features

- **Real-time Career Predictions**: Predict career probabilities for a given student profile.
- **Batch Predictions**: Predict career probabilities for multiple student profiles in a single request.
- **Model Versioning**: Support for multiple versions of the trained model.
- **Caching**: Cache frequently requested predictions to improve performance.

## Tech Stack

- **Spring Boot**: For building the microservice.
- **Java ML Libraries**: Use libraries like `DJL` (Deep Java Library) or `Tribuo` to load and use the pre-trained models.
- **REST API**: Exposes endpoints for predictions.
- **Caching**: Spring Cache for caching predictions.

## Prerequisites

1. **Java Environment**: Ensure Java 17+ is installed.
2. **Spring Boot Application**: A Spring Boot application with REST API endpoints for handling predictions.
3. **Pre-trained Model Files**: The trained model files (`career_predictor.joblib`, `model_metadata.joblib`, `feature_selector.joblib`, `scaler.joblib`) should be available in the `models` directory.

## Steps to Integrate ML Models with Spring Boot

### 1. Load the Pre-trained Model in Java

Use a Java library like `DJL` or `Tribuo` to load the pre-trained model files. Below is an example using `DJL` to load the model and make predictions.

#### Example: `PredictionService.java`

```java
import ai.djl.Model;
import ai.djl.inference.Predictor;
import ai.djl.modality.Classifications;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.translate.TranslateException;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Service
public class PredictionService {

    private Model model;
    private Predictor<NDList, Classifications> predictor;

    @PostConstruct
    public void init() {
        try {
            // Load the pre-trained model
            model = Model.newInstance("career_predictor");
            model.load(new File("models/career_predictor.joblib"));

            // Create a predictor
            predictor = model.newPredictor(new CareerTranslator());
        } catch (Exception e) {
            throw new RuntimeException("Failed to load model", e);
        }
    }

    public Map<String, Double> predictCareerProbabilities(StudentProfile profile) {
        try (NDManager manager = NDManager.newBaseManager()) {
            // Convert student profile to NDArray
            NDArray features = convertProfileToNDArray(manager, profile);

            // Make prediction
            NDList input = new NDList(features);
            Classifications classifications = predictor.predict(input);

            // Convert predictions to a map
            Map<String, Double> careerProbabilities = new HashMap<>();
            classifications.forEach(c -> careerProbabilities.put(c.getClassName(), c.getProbability() * 100));

            return careerProbabilities;
        } catch (TranslateException e) {
            throw new RuntimeException("Prediction failed", e);
        }
    }

    private NDArray convertProfileToNDArray(NDManager manager, StudentProfile profile) {
        // Convert student profile to a feature vector
        double[] features = new double[featureNames.size()];
        Arrays.fill(features, -1.0);  // Initialize with -1

        // Set basic features
        features[featureOrder.get("education_level")] = profile.getEducationLevel();
        features[featureOrder.get("AL_stream")] = profile.getAlStream();
        features[featureOrder.get("gpa")] = profile.getGpa();

        // Set OL subject scores
        for (Map.Entry<String, Double> entry : profile.getOlResults().entrySet()) {
            String colName = "OL_subject_" + entry.getKey() + "_score";
            if (featureOrder.containsKey(colName)) {
                features[featureOrder.get(colName)] = entry.getValue();
            }
        }

        // Set AL subject scores
        if (profile.getAlResults() != null) {
            for (Map.Entry<String, Double> entry : profile.getAlResults().entrySet()) {
                String colName = "AL_subject_" + entry.getKey() + "_score";
                if (featureOrder.containsKey(colName)) {
                    features[featureOrder.get(colName)] = entry.getValue();
                }
            }
        }

        // Scale the features (if needed)
        double[] scaledFeatures = scaler.transform(new double[][]{features})[0];

        return manager.create(scaledFeatures);
    }

    private static class CareerTranslator implements Translator<NDList, Classifications> {
        @Override
        public NDList processInput(TranslatorContext ctx, NDList input) {
            return input;
        }

        @Override
        public Classifications processOutput(TranslatorContext ctx, NDList output) {
            return new Classifications(output);
        }
    }
}
```

### 2. Create a REST Controller for Predictions

Create a REST controller to expose the prediction functionality.

#### Example: `PredictionController.java`

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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

### 3. Define the Student Profile Model

Define a `StudentProfile` class to represent the input data for predictions.

#### Example: `StudentProfile.java`

```java
import java.util.Map;

public class StudentProfile {
    private int educationLevel;
    private int alStream;
    private double gpa;
    private Map<String, Double> olResults;
    private Map<String, Double> alResults;

    // Getters and setters
}
```

### 4. Test the Integration

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

By following these steps, you can integrate the pre-trained ML models into a Spring Boot application using Java libraries like `DJL` or `Tribuo`. This setup allows the Spring Boot application to leverage the power of the trained model while maintaining a clean separation between the ML pipeline and the application logic.

For further improvements, consider:
- Adding model versioning and monitoring.
- Implementing caching for frequently requested predictions.
- Enhancing the API with additional features like batch predictions.