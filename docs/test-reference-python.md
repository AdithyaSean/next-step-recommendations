# Current model details and references based on python codes

## config.py

```python
"""Configuration for data generation and preprocessing."""

# Education level mapping
EDUCATION_LEVELS = {"OL": 0, "AL": 1, "UNI": 2}

# Subject mappings
OL_SUBJECTS = {
    "Maths": 0,
    "Science": 1,
    "English": 2,
    "Sinhala": 3,
    "History": 4,
    "Religion": 5,
}

# AL stream mappings
AL_STREAMS = {
    "Physical Science": 0,
    "Biological Science": 1,
    "Commerce": 2,
    "Arts": 3,
    "Technology": 4,
}

# AL subject mappings
AL_SUBJECTS = {
    "Physics": 0,
    "Chemistry": 1,
    "Combined_Maths": 2,
    "Biology": 3,
    "Accounting": 4,
    "Business_Studies": 5,
    "Economics": 6,
    "History": 7,
    "Geography": 8,
    "Politics": 9,
    "Engineering_Tech": 10,
    "Science_Tech": 11,
    "ICT": 12,
}

# Career mappings
CAREERS = {
    "Engineering": 0,
    "Medicine": 1,
    "IT": 2,
    "Business": 3,
    "Teaching": 4,
    "Research": 5,
}

config = {
    "num_students": 5000,
    "data_dir": "./data/raw",
    "processed_dir": "./data/processed",
    "model_dir": "./models",
    "education_levels": EDUCATION_LEVELS,
    "education_level_dist": {
        EDUCATION_LEVELS["OL"]: 0.4,
        EDUCATION_LEVELS["AL"]: 0.5,
        EDUCATION_LEVELS["UNI"]: 0.1,
    },
    "ol_subjects": OL_SUBJECTS,
    "al_streams": AL_STREAMS,
    "al_subjects": {
        AL_STREAMS["Physical Science"]: [
            AL_SUBJECTS["Physics"],
            AL_SUBJECTS["Chemistry"],
            AL_SUBJECTS["Combined_Maths"],
        ],
        AL_STREAMS["Biological Science"]: [
            AL_SUBJECTS["Biology"],
            AL_SUBJECTS["Chemistry"],
            AL_SUBJECTS["Physics"],
        ],
        AL_STREAMS["Commerce"]: [
            AL_SUBJECTS["Accounting"],
            AL_SUBJECTS["Business_Studies"],
            AL_SUBJECTS["Economics"],
        ],
        AL_STREAMS["Arts"]: [
            AL_SUBJECTS["History"],
            AL_SUBJECTS["Geography"],
            AL_SUBJECTS["Politics"],
        ],
        AL_STREAMS["Technology"]: [
            AL_SUBJECTS["Engineering_Tech"],
            AL_SUBJECTS["Science_Tech"],
            AL_SUBJECTS["ICT"],
        ],
    },
    "careers": CAREERS,
    "career_success_ranges": {
        CAREERS["Engineering"]: (0.55, 0.95),
        CAREERS["Medicine"]: (0.60, 0.92),
        CAREERS["IT"]: (0.50, 0.90),
        CAREERS["Business"]: (0.45, 0.85),
        CAREERS["Teaching"]: (0.40, 0.80),
        CAREERS["Research"]: (0.55, 0.88),
    },
    "gpa_range": {"min": 2.0, "max": 4.0},  # Minimum passing GPA  # Maximum GPA
}
```

## predictor.py

```python
"""Predictor module for career prediction."""

from typing import Dict, Optional

import joblib
import numpy as np
import pandas as pd

from ..config.config import config


def get_base_features() -> pd.DataFrame:
    """Create DataFrame with training features structure."""
    try:
        # Load feature order from training
        feature_order = joblib.load(f"{config['model_dir']}/feature_order.joblib")
        feature_names = feature_order["feature_names"]

        # Create DataFrame with correct columns
        features = pd.DataFrame(columns=feature_names)
        features.loc[0] = -1.0  # Initialize with -1
        return features
    except Exception as e:
        raise RuntimeError(f"Failed to initialize features: {e}")


def predict(
    education_level: int,
    ol_results: Dict[str, float],
    al_stream: Optional[int] = None,
    al_results: Optional[Dict[str, float]] = None,
    gpa: Optional[float] = None,
) -> Dict[str, float]:
    """Predict career probabilities based on educational profile."""
    try:
        # Load model artifacts
        model = joblib.load(f"{config['model_dir']}/career_predictor.joblib")
        metadata = joblib.load(f"{config['model_dir']}/model_metadata.joblib")

        # Initialize features
        features = get_base_features()

        # Set basic features
        features.loc[0, "education_level"] = education_level
        features.loc[0, "AL_stream"] = al_stream if al_stream is not None else -1
        features.loc[0, "gpa"] = gpa if gpa is not None else -1

        # Set OL subject scores
        for subject_id, score in ol_results.items():
            col_name = f"OL_subject_{subject_id}_score"
            if col_name in features.columns:
                features.loc[0, col_name] = float(score)

        # Set AL subject scores
        if al_results:
            for subject_id, score in al_results.items():
                col_name = f"AL_subject_{subject_id}_score"
                if col_name in features.columns:
                    features.loc[0, col_name] = float(score)

        # Select features used by model
        selected_features = metadata["feature_names"]
        features_selected = features[selected_features]

        # Make prediction
        predictions = model.predict(features_selected)[0]

        # Return career probabilities
        return {
            career: float(np.clip(prob * 100, 0, 100))
            for career, prob in zip(metadata["career_names"], predictions)
        }

    except Exception as e:
        print(f"Prediction error: {e}")
        return {}


def predictor():
    """Run sample predictions."""
    # Example OL student
    ol_results = {
        "0": 85,  # Math
        "1": 78,  # Science
        "2": 72,  # English
        "3": 65,  # Sinhala
        "4": 70,  # History
        "5": 75,  # Religion
    }

    print("\nOL Student Profile:")
    print("==================")
    print("\nPredicted Career Probabilities:")
    print(predict(education_level=0, ol_results=ol_results))

    # Example AL Science student
    al_results = {
        "0": 88,  # Physics
        "1": 82,  # Chemistry
        "2": 90,  # Combined Maths
    }

    print("\nAL Science Student Profile:")
    print("=========================")
    print("\nPredicted Career Probabilities:")
    print(
        predict(
            education_level=1, ol_results=ol_results, al_stream=0, al_results=al_results
        )
    )

    # Example University student
    print("\nUniversity Student Profile:")
    print("=========================")
    print("\nPredicted Career Probabilities:")
    print(
        predict(
            education_level=2,
            ol_results=ol_results,
            al_stream=0,
            al_results=al_results,
            gpa=3.75,
        )
    )


if __name__ == "__main__":
    predictor()
```