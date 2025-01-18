### Documentation for the Next-Step Recommendations Microservice

This documentation provides an overview of the key components in the Next-Step Recommendations microservice. The microservice is designed to predict career probabilities based on a student's educational background, including their Ordinary Level (OL) and Advanced Level (AL) results, as well as their GPA (if applicable).

---

### 1. **Predictor.java**

#### **Purpose:**
The `Predictor` class is responsible for loading a pre-trained machine learning model and using it to predict career probabilities based on a student's educational profile.

#### **Key Methods:**
- **`Predictor(String modelPath)`**: Constructor that loads the pre-trained model from the specified path.
- **`predict(int educationLevel, Map<String, Double> olResults, Integer alStream, Map<String, Double> alResults, Double gpa)`**: Predicts career probabilities based on the provided student profile.

#### **Dependencies:**
- **Weka**: Used for loading the model and making predictions.
- **Spring Framework**: Used for dependency injection and service management.

#### **Example Usage:**
```java
Predictor predictor = new Predictor("path/to/model");
Map<String, Double> predictions = predictor.predict(1, olResults, 0, alResults, 3.75);
```

---

### 2. **Config.java**

#### **Purpose:**
The `Config` class contains static configurations and mappings for education levels, subjects, streams, and careers. It also defines constants such as file paths and numerical ranges used throughout the application.

#### **Key Constants:**
- **`EDUCATION_LEVELS`**: Maps education levels (OL, AL, UNI) to integer values.
- **`OL_SUBJECTS`**: Maps Ordinary Level subjects to integer values.
- **`AL_STREAMS`**: Maps Advanced Level streams to integer values.
- **`AL_SUBJECTS`**: Maps Advanced Level subjects to integer values.
- **`CAREERS`**: Maps career options to integer values.
- **`MODEL_DIR`**: Directory where trained models and preprocessed data are stored.

#### **Example Usage:**
```java
int eduType = Config.EDUCATION_LEVELS.get("OL");
String modelPath = Config.MODEL_DIR + "/career_predictor.model";
```

---

### 3. **Preprocessor.java**

#### **Purpose:**
The `Preprocessor` class is responsible for preprocessing the raw student data before it is used for training or prediction. This includes standardizing the features and saving the preprocessed data to a file.

#### **Key Methods:**
- **`preprocess()`**: Reads the raw data, standardizes the features, and saves the preprocessed data and scaler model to the specified directory.

#### **Dependencies:**
- **Weka**: Used for data standardization and serialization.

#### **Example Usage:**
```java
Preprocessor preprocessor = new Preprocessor();
preprocessor.preprocess();
```

---

### 4. **Generator.java**

#### **Purpose:**
The `Generator` class generates synthetic student profiles for training and testing purposes. It creates a dataset with random values for education levels, subject scores, and GPAs.

#### **Key Methods:**
- **`generate()`**: Generates a list of synthetic student profiles.
- **`saveToARFF(List<Map<String, Object>> data)`**: Saves the generated profiles to an ARFF file format, which is compatible with Weka.

#### **Example Usage:**
```java
Generator generator = new Generator();
List<Map<String, Object>> data = generator.generate();
generator.saveToARFF(data);
```

---

### 5. **Trainer.java**

#### **Purpose:**
The `Trainer` class is responsible for training a machine learning model using the preprocessed data. It uses a Random Forest classifier with cross-validation for hyperparameter tuning.

#### **Key Methods:**
- **`train()`**: Trains the model using the preprocessed data and saves the trained model to a file.

#### **Dependencies:**
- **Weka**: Used for model training and evaluation.
- **Spring Framework**: Used for service management.

#### **Example Usage:**
```java
Trainer trainer = new Trainer();
trainer.train();
```

---

### 6. **RecommendationController.java**

#### **Purpose:**
The `RecommendationController` class is a Spring REST controller that exposes endpoints for generating data, training the model, and making predictions.

#### **Key Endpoints:**
- **`POST /recommendations/predict`**: Accepts a `PredictionRequest` and returns predicted career probabilities.
- **`POST /recommendations/train`**: Triggers the training of the machine learning model.
- **`POST /recommendations/generate`**: Generates synthetic student profiles.

#### **Example Usage:**
```java
// Example request to predict career probabilities
POST /recommendations/predict
{
    "educationLevel": 1,
    "olResults": {"0": 85.0, "1": 78.0, "2": 72.0},
    "alStream": 0,
    "alResults": {"0": 88.0, "1": 82.0, "2": 90.0},
    "gpa": 3.75
}
```

---

### 7. **PredictionRequest.java**

#### **Purpose:**
The `PredictionRequest` class is a data transfer object (DTO) used to encapsulate the input data required for making a prediction.

#### **Fields:**
- **`educationLevel`**: The education level of the student (OL, AL, UNI).
- **`olResults`**: A map of Ordinary Level subject scores.
- **`alStream`**: The Advanced Level stream (if applicable).
- **`alResults`**: A map of Advanced Level subject scores (if applicable).
- **`gpa`**: The student's GPA (if applicable).

#### **Example Usage:**
```java
PredictionRequest request = new PredictionRequest();
request.setEducationLevel(1);
request.setOlResults(Map.of("0", 85.0, "1", 78.0, "2", 72.0));
request.setAlStream(0);
request.setAlResults(Map.of("0", 88.0, "1", 82.0, "2", 90.0));
request.setGpa(3.75);
```

---

### Summary

The Next-Step Recommendations microservice is designed to predict career probabilities based on a student's educational background. The key components include:

- **`Predictor`**: Makes predictions using a pre-trained model.
- **`Config`**: Contains static configurations and mappings.
- **`Preprocessor`**: Preprocesses raw data for training and prediction.
- **`Generator`**: Generates synthetic student profiles.
- **`Trainer`**: Trains the machine learning model.
- **`RecommendationController`**: Exposes REST endpoints for predictions, training, and data generation.
- **`PredictionRequest`**: Encapsulates the input data for predictions.

This documentation should help other developers understand the structure and functionality of the microservice, enabling them to extend or modify it as needed.