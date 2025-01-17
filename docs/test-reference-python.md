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

## preprossesor.py

```python
"""Enhanced preprocessor for scikit-learn compatibility."""

import os

import joblib
import pandas as pd
from sklearn.preprocessing import StandardScaler

from ..config.config import CAREERS, config


def preprocessor():
    """Preprocess data for model training."""
    df = pd.read_csv(f"{config['data_dir']}/student_profiles.csv")

    # Separate features and target probabilities
    career_columns = [f"career_{career_id}" for career_id in CAREERS.values()]
    career_probs = df[career_columns]
    features = df.drop(columns=career_columns + ["profile_id"])

    # Store original feature names
    feature_names = features.columns.tolist()

    # Scale numerical features
    scaler = StandardScaler()
    features_scaled = pd.DataFrame(
        scaler.fit_transform(features),
        columns=feature_names,  # Explicitly set column names
    )

    # Save scaler and feature names
    os.makedirs(config["model_dir"], exist_ok=True)
    joblib.dump(scaler, f"{config['model_dir']}/scaler.joblib")
    joblib.dump(feature_names, f"{config['model_dir']}/feature_names.joblib")

    # Save feature order with scaler
    feature_order = {
        "feature_names": features.columns.tolist(),
        "feature_order": {name: idx for idx, name in enumerate(features.columns)},
    }
    joblib.dump(feature_order, f"{config['model_dir']}/feature_order.joblib")

    os.makedirs(config["processed_dir"], exist_ok=True)
    features_scaled.to_csv(f"{config['processed_dir']}/features.csv", index=False)
    career_probs.to_csv(f"{config['processed_dir']}/career_probs.csv", index=False)

    print("\nDataset Summary:")
    print(f"Total profiles: {len(df)}")
    print(f"Feature columns: {len(features_scaled.columns)}")
    print(f"Target careers: {len(career_probs.columns)}")

    print("\nEducation Level Distribution:")
    for level_name, level_id in config["education_levels"].items():
        count = (df["education_level"] == level_id).sum()
        print(f"{level_name}: {count}")

    return features_scaled, career_probs


if __name__ == "__main__":
    preprocessor()
```

## trainer.py

```python
"""Train and evaluate the career prediction model."""

import os

import joblib
import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
from sklearn.ensemble import RandomForestRegressor
from sklearn.feature_selection import SelectFromModel
from sklearn.metrics import mean_squared_error, r2_score
from sklearn.model_selection import (
    GridSearchCV,
    cross_val_score,
    learning_curve,
    train_test_split,
)

from ..config.config import CAREERS, config
from ..preprocessors.preprocessor import preprocessor


def plot_learning_curves(model, X, y):
    """Plot learning curves to detect overfitting."""
    try:
        train_sizes, train_scores, val_scores = learning_curve(
            model,
            X,
            y,
            cv=5,
            n_jobs=-1,
            train_sizes=np.linspace(0.1, 1.0, 10),
            scoring="r2",
        )

        plt.figure(figsize=(10, 6))
        plt.plot(
            train_sizes, np.mean(train_scores, axis=1), "o-", label="Training score"
        )
        plt.plot(
            train_sizes,
            np.mean(val_scores, axis=1),
            "o-",
            label="Cross-validation score",
        )
        plt.fill_between(
            train_sizes,
            np.mean(train_scores, axis=1) - np.std(train_scores, axis=1),
            np.mean(train_scores, axis=1) + np.std(train_scores, axis=1),
            alpha=0.1,
        )
        plt.fill_between(
            train_sizes,
            np.mean(val_scores, axis=1) - np.std(val_scores, axis=1),
            np.mean(val_scores, axis=1) + np.std(val_scores, axis=1),
            alpha=0.1,
        )
        plt.xlabel("Training Examples")
        plt.ylabel("R² Score")
        plt.title("Learning Curves")
        plt.legend(loc="best")
        plt.grid(True)

        os.makedirs(config["model_dir"], exist_ok=True)
        plt.savefig(
            f"{config['model_dir']}/learning_curves.png", dpi=300, bbox_inches="tight"
        )
        plt.close()
    except Exception as e:
        print(f"Error plotting learning curves: {e}")


def trainer():
    """Train and evaluate the career prediction model."""
    # Get preprocessed data
    features, targets = preprocessor()
    if features is None:
        return

    # Split train/test first
    X_train, X_test, y_train, y_test = train_test_split(
        features, targets, test_size=0.2, random_state=42
    )

    # Initialize feature selector with proper feature names
    selector = RandomForestRegressor(n_estimators=50, max_depth=8, random_state=42)
    feature_selector = SelectFromModel(selector)

    # Fit and transform while maintaining feature names
    X_train_selected = pd.DataFrame(
        feature_selector.fit_transform(X_train, y_train),
        columns=X_train.columns[feature_selector.get_support()],
    )

    X_test_selected = pd.DataFrame(
        feature_selector.transform(X_test),
        columns=X_train.columns[feature_selector.get_support()],
    )

    # Store selected feature names
    selected_features = X_train_selected.columns.tolist()

    # Define model and parameters
    model = RandomForestRegressor(random_state=42)
    param_grid = {
        "n_estimators": [50, 100],
        "max_depth": [5, 8, 10],
        "min_samples_split": [5, 10],
        "min_samples_leaf": [2, 4],
        "max_features": ["sqrt"],
    }

    # Grid search
    grid_search = GridSearchCV(
        model, param_grid, cv=5, n_jobs=-1, scoring="r2", verbose=2
    )
    grid_search.fit(X_train_selected, y_train)

    # Get best model
    best_model = grid_search.best_estimator_

    # Now do cross-validation
    cv_scores = cross_val_score(best_model, X_train_selected, y_train, cv=5)
    print(f"\nCross-validation scores: {cv_scores}")
    print(f"Mean CV score: {cv_scores.mean():.4f} (+/- {cv_scores.std() * 2:.4f})")

    # Print best parameters
    print("\nBest parameters:", grid_search.best_params_)

    # Plot learning curves
    plot_learning_curves(best_model, X_train_selected, y_train)

    # Fix feature importance analysis
    feature_importance = pd.DataFrame(
        {"feature": selected_features, "importance": best_model.feature_importances_}
    ).sort_values("importance", ascending=False)
    print("\nTop 10 most important features:")
    print(feature_importance.head(10))

    os.makedirs(config["model_dir"], exist_ok=True)

    # Save metadata with proper feature names
    metadata = {
        "feature_names": selected_features,
        "career_names": list(CAREERS.keys()),
        "best_params": grid_search.best_params_,
        "cv_scores": {"mean": float(cv_scores.mean()), "std": float(cv_scores.std())},
    }

    # Save model and metadata
    joblib.dump(best_model, f"{config['model_dir']}/career_predictor.joblib")
    joblib.dump(metadata, f"{config['model_dir']}/model_metadata.joblib")
    joblib.dump(feature_selector, f"{config['model_dir']}/feature_selector.joblib")

    # Save feature importance
    feature_importance.to_csv(
        f"{config['model_dir']}/feature_importance.csv", index=False
    )

    # Evaluate on test set
    y_pred = best_model.predict(X_test_selected)

    print("\nModel Performance:\n")
    for i, career in enumerate(CAREERS.keys()):
        mse = mean_squared_error(y_test.iloc[:, i], y_pred[:, i])
        r2 = r2_score(y_test.iloc[:, i], y_pred[:, i])
        print(f"{career}:")
        print(f"MSE: {mse:.4f}")
        print(f"R2 Score: {r2:.4f}\n")

    return best_model


if __name__ == "__main__":
    trainer()
```

## generator.py

```python
"""Enhanced data generator."""

import os

import numpy as np
import pandas as pd

from ..config.config import AL_STREAMS, CAREERS, EDUCATION_LEVELS, OL_SUBJECTS, config


def generator():
    """Generate comprehensive student profiles with multiple education levels."""
    data = []

    total_students = config["num_students"]
    education_dist = config["education_level_dist"]

    def generate_ol_results():
        return {
            f"OL_subject_{idx}_score": np.random.randint(0, 100)
            for idx in config["ol_subjects"].values()
        }

    def generate_al_results(stream_id):
        return {
            f"AL_subject_{sub_id}_score": np.random.randint(0, 100)
            for sub_id in config["al_subjects"][stream_id]
        }

    def generate_career_probs(profile):
        """Generate career probabilities based on education and performance."""
        probs = {}

        for career_id, (base_low, base_high) in config["career_success_ranges"].items():
            prob = base_low
            edu_level = profile.get("education_level")
            stream = profile.get("AL_stream")
            gpa = profile.get("gpa", 0)

            # Normalize GPA to 0-100 scale for probability calculations
            gpa_normalized = (
                (gpa - config["gpa_range"]["min"])
                / (config["gpa_range"]["max"] - config["gpa_range"]["min"])
            ) * 100

            # Add OL subject contributions
            ol_math = profile.get(f"OL_subject_{OL_SUBJECTS['Maths']}_score", 0)
            ol_science = profile.get(f"OL_subject_{OL_SUBJECTS['Science']}_score", 0)
            ol_english = profile.get(f"OL_subject_{OL_SUBJECTS['English']}_score", 0)

            # Engineering careers
            if career_id == CAREERS["Engineering"]:
                if stream == AL_STREAMS["Physical Science"]:
                    math_score = profile.get("AL_subject_2_score", 0)  # Combined Maths
                    physics_score = profile.get("AL_subject_0_score", 0)  # Physics
                    prob += (math_score / 100) * 0.3 + (physics_score / 100) * 0.2
                # Add small boost for good OL math/science
                prob += (ol_math / 100) * 0.1 + (ol_science / 100) * 0.1

            # Medicine careers
            elif career_id == CAREERS["Medicine"]:
                if stream == AL_STREAMS["Biological Science"]:
                    bio_score = profile.get("AL_subject_0_score", 0)  # Biology
                    chem_score = profile.get("AL_subject_1_score", 0)  # Chemistry
                    prob += (bio_score / 100) * 0.3 + (chem_score / 100) * 0.2
                # Add small boost for good OL science
                prob += (ol_science / 100) * 0.15

            # IT careers
            elif career_id == CAREERS["IT"]:
                if stream in [AL_STREAMS["Physical Science"], AL_STREAMS["Technology"]]:
                    ict_score = profile.get("AL_subject_12_score", 0)  # ICT
                    prob += (ict_score / 100) * 0.4
                # Add small boost for good OL math/science
                prob += (ol_math / 100) * 0.1 + (ol_science / 100) * 0.1

            # Business careers
            elif career_id == CAREERS["Business"]:
                if stream == AL_STREAMS["Commerce"]:
                    accounting = profile.get("AL_subject_4_score", 0)  # Accounting
                    business = profile.get("AL_subject_5_score", 0)  # Business Studies
                    prob += (accounting / 100) * 0.2 + (business / 100) * 0.2

            # Teaching careers
            elif career_id == CAREERS["Teaching"]:
                # All streams can lead to teaching
                if edu_level in [EDUCATION_LEVELS["UNI"]]:
                    prob += (gpa_normalized / 100) * 0.3

            # Research careers
            elif career_id == CAREERS["Research"]:
                if edu_level in [EDUCATION_LEVELS["UNI"]]:
                    prob += (gpa_normalized / 100) * 0.4
                    if stream in [
                        AL_STREAMS["Physical Science"],
                        AL_STREAMS["Biological Science"],
                    ]:
                        prob += 0.2

            # All careers - small boost for good English
            prob += (ol_english / 100) * 0.05

            # Cap probability at base_high
            prob = min(prob, base_high)
            probs[f"career_{career_id}"] = prob

        return probs

    # Generate profiles for each education level
    for _ in range(total_students):
        profile = {"profile_id": _ + 1}
        edu_type = np.random.choice(
            list(education_dist.keys()), p=list(education_dist.values())
        )

        profile["education_level"] = edu_type

        if edu_type in [
            EDUCATION_LEVELS["OL"],
            EDUCATION_LEVELS["AL"],
            EDUCATION_LEVELS["UNI"],
        ]:
            profile.update(generate_ol_results())

        if edu_type in [EDUCATION_LEVELS["AL"], EDUCATION_LEVELS["UNI"]]:
            stream_id = np.random.choice(list(AL_STREAMS.values()))
            profile["AL_stream"] = stream_id
            profile.update(generate_al_results(stream_id))

        if edu_type in [EDUCATION_LEVELS["UNI"]]:
            profile["university_score"] = np.random.randint(60, 100)
            # Generate GPA with most values clustering around 2.8-3.5
            gpa = np.random.normal(3.2, 0.4)
            # Clip to valid GPA range
            gpa = np.clip(gpa, config["gpa_range"]["min"], config["gpa_range"]["max"])
            profile["gpa"] = round(gpa, 2)

        profile.update(generate_career_probs(profile))
        data.append(profile)

    df = pd.DataFrame(data)
    df = df.fillna(-1)

    os.makedirs(config["data_dir"], exist_ok=True)
    df.to_csv(f"{config['data_dir']}/student_profiles.csv", index=False)

    return df


if __name__ == "__main__":
    generator()
```