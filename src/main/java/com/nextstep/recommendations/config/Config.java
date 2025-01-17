package com.nextstep.recommendations.config;

import java.util.List;
import java.util.Map;

public class Config {
    public static final Map<String, Integer> EDUCATION_LEVELS = Map.of(
        "OL", 0,
        "AL", 1,
        "UNI", 2
    );

    public static final Map<String, Integer> OL_SUBJECTS = Map.of(
        "Maths", 0,
        "Science", 1,
        "English", 2,
        "Sinhala", 3,
        "History", 4,
        "Religion", 5
    );

    public static final Map<String, Integer> AL_STREAMS = Map.of(
        "Physical Science", 0,
        "Biological Science", 1,
        "Commerce", 2,
        "Arts", 3,
        "Technology", 4
    );

    public static final Map<String, Integer> AL_SUBJECTS = Map.of(
        "Physics", 0,
        "Chemistry", 1,
        "Combined_Maths", 2,
        "Biology", 3,
        "Accounting", 4,
        "Business_Studies", 5,
        "Economics", 6,
        "History", 7,
        "Geography", 8,
        "Politics", 9,
        "Engineering_Tech", 10,
        "Science_Tech", 11,
        "ICT", 12
    );

    public static final Map<String, Integer> CAREERS = Map.of(
        "Engineering", 0,
        "Medicine", 1,
        "IT", 2,
        "Business", 3,
        "Teaching", 4,
        "Research", 5
    );

    public static final Map<Integer, Double> EDUCATION_LEVEL_DIST = Map.of(
        EDUCATION_LEVELS.get("OL"), 0.4,
        EDUCATION_LEVELS.get("AL"), 0.5,
        EDUCATION_LEVELS.get("UNI"), 0.1
    );

    public static final Map<Integer, List<Integer>> AL_SUBJECTS_BY_STREAM = Map.of(
        AL_STREAMS.get("Physical Science"), List.of(AL_SUBJECTS.get("Physics"), AL_SUBJECTS.get("Chemistry"), AL_SUBJECTS.get("Combined_Maths")),
        AL_STREAMS.get("Biological Science"), List.of(AL_SUBJECTS.get("Biology"), AL_SUBJECTS.get("Chemistry"), AL_SUBJECTS.get("Physics")),
        AL_STREAMS.get("Commerce"), List.of(AL_SUBJECTS.get("Accounting"), AL_SUBJECTS.get("Business_Studies"), AL_SUBJECTS.get("Economics")),
        AL_STREAMS.get("Arts"), List.of(AL_SUBJECTS.get("History"), AL_SUBJECTS.get("Geography"), AL_SUBJECTS.get("Politics")),
        AL_STREAMS.get("Technology"), List.of(AL_SUBJECTS.get("Engineering_Tech"), AL_SUBJECTS.get("Science_Tech"), AL_SUBJECTS.get("ICT"))
    );

    public static final Map<Integer, double[]> CAREER_SUCCESS_RANGES = Map.of(
        CAREERS.get("Engineering"), new double[]{0.55, 0.95},
        CAREERS.get("Medicine"), new double[]{0.60, 0.92},
        CAREERS.get("IT"), new double[]{0.50, 0.90},
        CAREERS.get("Business"), new double[]{0.45, 0.85},
        CAREERS.get("Teaching"), new double[]{0.40, 0.80},
        CAREERS.get("Research"), new double[]{0.55, 0.88}
    );

    public static final double[] GPA_RANGE = {2.0, 4.0};

    public static final int NUM_STUDENTS = 5000;
    public static final String DATA_DIR = "./data/raw";
    public static final String PROCESSED_DIR = "./data/processed";
    public static final String MODEL_DIR = "./models";
}
