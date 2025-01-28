package com.nextstep.recommendations.src;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class Config {
    // Education levels
    public static final Map<String, Integer> EDUCATION_LEVELS = new HashMap<>();
    // Grades and Results
    public static final Map<String, Integer> GRADES = new HashMap<>();
    // OL subjects and their IDs
    public static final Map<String, Integer> OL_SUBJECTS = new HashMap<>();
    // AL streams and their IDs
    public static final Map<String, Integer> AL_STREAMS = new HashMap<>();
    // AL subjects and their IDs
    public static final Map<String, Integer> AL_SUBJECTS = new HashMap<>();
    // Distribution of education levels
    public static final Map<Integer, Double> EDUCATION_LEVEL_DIST = new HashMap<>();
    // AL subjects by stream
    public static final Map<Integer, List<Integer>> AL_SUBJECTS_BY_STREAM = new HashMap<>();
    // Careers and their IDs
    public static final Map<String, Integer> CAREERS = new HashMap<>();
    // Career success ranges by career ID
    public static final Map<Integer, Double> CAREER_SUCCESS_PROBABILITY = new HashMap<>();
    // Career success probability range
    public static final double[] CAREER_SUCCESS_PROBABILITY_RANGE = {0.0, 100.0};
    // Z-score range
    public static final double[] Z_SCORE_RANGE = {-3.0, 3.0};
    // GPA range
    public static final double[] GPA_RANGE = {0.0, 4.0};
    // Number of students to generate
    public static final int NUM_STUDENTS = 5000;
    // Directory to save models
    public static final String MODEL_DIR = "./models";
    // Grade distributions for subjects
    public static final Map<Integer, Map<String, Double>> GRADE_DISTRIBUTIONS = new HashMap<>();
    // Subject difficulty levels
    public static final Map<Integer, Double> SUBJECT_DIFFICULTY = new HashMap<>();
    // Career preferences based on subjects and grades
    public static final Map<Integer, Map<Integer, Double>> CAREER_PREFERENCES = new HashMap<>();
    // Z-score distributions based on AL performance
    public static final Map<Integer, Double> Z_SCORE_DISTRIBUTIONS = new HashMap<>();
    // GPA distributions based on AL performance
    public static final Map<Integer, Double> GPA_DISTRIBUTIONS = new HashMap<>();
    // Career success probability adjustments based on Z-score, career preference, GPA, and grades
    public static final Map<Integer, Map<String, Double>> CAREER_SUCCESS_ADJUSTMENTS = new HashMap<>();
    // Career probability penalties based on poor performance
    public static final Map<Integer, Map<String, Double>> CAREER_PROBABILITY_PENALTIES = new HashMap<>();

    static {
        // Initialize education levels
        EDUCATION_LEVELS.put("OL", 0); // Ordinary Level
        EDUCATION_LEVELS.put("AL", 1); // Advanced Level
        EDUCATION_LEVELS.put("UNI", 2); // University

        // Initialize Grades
        GRADES.put("A", 0);
        GRADES.put("B", 1);
        GRADES.put("C", 2);
        GRADES.put("S", 3);
        GRADES.put("F", 4);

        // Initialize OL subjects
        OL_SUBJECTS.put("Maths", 0);
        OL_SUBJECTS.put("Science", 1);
        OL_SUBJECTS.put("English", 2);
        OL_SUBJECTS.put("Sinhala", 3);
        OL_SUBJECTS.put("History", 4);
        OL_SUBJECTS.put("Religion", 5);

        // Initialize AL streams
        AL_STREAMS.put("Physical Science - Chemistry", 0);
        AL_STREAMS.put("Physical Science - ICT", 1);
        AL_STREAMS.put("Biological Science - Physics", 2);
        AL_STREAMS.put("Biological Science - Agriculture", 3);

        // Initialize AL subjects
        AL_SUBJECTS.put("Physics", 0);
        AL_SUBJECTS.put("Chemistry", 1);
        AL_SUBJECTS.put("Combined_Maths", 2);
        AL_SUBJECTS.put("Biology", 3);
        AL_SUBJECTS.put("ICT", 4);
        AL_SUBJECTS.put("Agriculture", 5);

        // Initialize education level distribution
        EDUCATION_LEVEL_DIST.put(EDUCATION_LEVELS.get("OL"), 0.6);
        EDUCATION_LEVEL_DIST.put(EDUCATION_LEVELS.get("AL"), 0.3);
        EDUCATION_LEVEL_DIST.put(EDUCATION_LEVELS.get("UNI"), 0.1);

        // Initialize AL subjects by stream
        AL_SUBJECTS_BY_STREAM.put(AL_STREAMS.get("Physical Science - Chemistry"), List.of(
                AL_SUBJECTS.get("Combined_Maths"),
                AL_SUBJECTS.get("Physics"),
                AL_SUBJECTS.get("Chemistry")
        ));
        AL_SUBJECTS_BY_STREAM.put(AL_STREAMS.get("Physical Science - ICT"), List.of(
                AL_SUBJECTS.get("Combined_Maths"),
                AL_SUBJECTS.get("Physics"),
                AL_SUBJECTS.get("ICT")
        ));
        AL_SUBJECTS_BY_STREAM.put(AL_STREAMS.get("Biological Science - Physics"), List.of(
                AL_SUBJECTS.get("Biology"),
                AL_SUBJECTS.get("Chemistry"),
                AL_SUBJECTS.get("Physics")
        ));
        AL_SUBJECTS_BY_STREAM.put(AL_STREAMS.get("Biological Science - Agriculture"), List.of(
                AL_SUBJECTS.get("Biology"),
                AL_SUBJECTS.get("Chemistry"),
                AL_SUBJECTS.get("Agriculture")
        ));

        // Initialize grade distributions for AL subjects
        GRADE_DISTRIBUTIONS.put(AL_SUBJECTS.get("Physics"), Map.of(
                "A", 0.2,
                "B", 0.3,
                "C", 0.3,
                "S", 0.1,
                "F", 0.1
        ));
        GRADE_DISTRIBUTIONS.put(AL_SUBJECTS.get("Chemistry"), Map.of(
                "A", 0.25,
                "B", 0.25,
                "C", 0.3,
                "S", 0.1,
                "F", 0.1
        ));
        GRADE_DISTRIBUTIONS.put(AL_SUBJECTS.get("Combined_Maths"), Map.of(
                "A", 0.3,
                "B", 0.3,
                "C", 0.2,
                "S", 0.1,
                "F", 0.1
        ));
        GRADE_DISTRIBUTIONS.put(AL_SUBJECTS.get("Biology"), Map.of(
                "A", 0.2,
                "B", 0.3,
                "C", 0.3,
                "S", 0.1,
                "F", 0.1
        ));
        GRADE_DISTRIBUTIONS.put(AL_SUBJECTS.get("ICT"), Map.of(
                "A", 0.3,
                "B", 0.3,
                "C", 0.2,
                "S", 0.1,
                "F", 0.1
        ));
        GRADE_DISTRIBUTIONS.put(AL_SUBJECTS.get("Agriculture"), Map.of(
                "A", 0.25,
                "B", 0.25,
                "C", 0.3,
                "S", 0.1,
                "F", 0.1
        ));

        // Initialize OL subject difficulty levels
        SUBJECT_DIFFICULTY.put(OL_SUBJECTS.get("Maths"), 1.0);
        SUBJECT_DIFFICULTY.put(OL_SUBJECTS.get("Science"), 0.7);
        SUBJECT_DIFFICULTY.put(OL_SUBJECTS.get("English"), 0.6);
        SUBJECT_DIFFICULTY.put(OL_SUBJECTS.get("Sinhala"), 0.4);
        SUBJECT_DIFFICULTY.put(OL_SUBJECTS.get("History"), 0.3);
        SUBJECT_DIFFICULTY.put(OL_SUBJECTS.get("Religion"), 0.01);

        // Initialize AL subject difficulty levels
        SUBJECT_DIFFICULTY.put(AL_SUBJECTS.get("Physics"), 0.9);
        SUBJECT_DIFFICULTY.put(AL_SUBJECTS.get("Chemistry"), 0.8);
        SUBJECT_DIFFICULTY.put(AL_SUBJECTS.get("Combined_Maths"), 0.8);
        SUBJECT_DIFFICULTY.put(AL_SUBJECTS.get("Biology"), 0.7);
        SUBJECT_DIFFICULTY.put(AL_SUBJECTS.get("ICT"), 0.6);
        SUBJECT_DIFFICULTY.put(AL_SUBJECTS.get("Agriculture"), 0.5);

        // Initialize career preferences based on subjects and grades
        CAREER_PREFERENCES.put(CAREERS.get("Engineering"), Map.of(
                OL_SUBJECTS.get("Maths"), 0.8,
                OL_SUBJECTS.get("Science"), 0.7,
                AL_SUBJECTS.get("Physics"), 0.9,
                AL_SUBJECTS.get("Combined_Maths"), 0.8
        ));
        CAREER_PREFERENCES.put(CAREERS.get("Medicine"), Map.of(
                OL_SUBJECTS.get("Science"), 0.9,
                OL_SUBJECTS.get("Biology"), 0.8,
                AL_SUBJECTS.get("Biology"), 0.9,
                AL_SUBJECTS.get("Chemistry"), 0.8
        ));
        CAREER_PREFERENCES.put(CAREERS.get("IT"), Map.of(
                OL_SUBJECTS.get("Maths"), 0.8,
                OL_SUBJECTS.get("Science"), 0.7,
                AL_SUBJECTS.get("ICT"), 0.9,
                AL_SUBJECTS.get("Combined_Maths"), 0.8
        ));
        CAREER_PREFERENCES.put(CAREERS.get("Business"), Map.of(
                OL_SUBJECTS.get("Maths"), 0.7,
                OL_SUBJECTS.get("English"), 0.6,
                AL_SUBJECTS.get("Economics"), 0.8,
                AL_SUBJECTS.get("Business_Studies"), 0.7
        ));
        CAREER_PREFERENCES.put(CAREERS.get("Teaching"), Map.of(
                OL_SUBJECTS.get("English"), 0.8,
                OL_SUBJECTS.get("History"), 0.7,
                AL_SUBJECTS.get("Education"), 0.9,
                AL_SUBJECTS.get("Psychology"), 0.8
        ));
        CAREER_PREFERENCES.put(CAREERS.get("Research"), Map.of(
                OL_SUBJECTS.get("Science"), 0.9,
                OL_SUBJECTS.get("Maths"), 0.8,
                AL_SUBJECTS.get("Physics"), 0.9,
                AL_SUBJECTS.get("Chemistry"), 0.8
        ));

        // Initialize Z-score distributions based on AL performance
        Z_SCORE_DISTRIBUTIONS.put(AL_SUBJECTS.get("Physics"), 2.5);
        Z_SCORE_DISTRIBUTIONS.put(AL_SUBJECTS.get("Chemistry"), 2.7);
        Z_SCORE_DISTRIBUTIONS.put(AL_SUBJECTS.get("Combined_Maths"), 2.8);
        Z_SCORE_DISTRIBUTIONS.put(AL_SUBJECTS.get("Biology"), 2.6);
        Z_SCORE_DISTRIBUTIONS.put(AL_SUBJECTS.get("ICT"), 2.4);
        Z_SCORE_DISTRIBUTIONS.put(AL_SUBJECTS.get("Agriculture"), 2.3);

        // Initialize GPA distributions based on AL performance
        GPA_DISTRIBUTIONS.put(AL_SUBJECTS.get("Physics"), 3.5);
        GPA_DISTRIBUTIONS.put(AL_SUBJECTS.get("Chemistry"), 3.7);
        GPA_DISTRIBUTIONS.put(AL_SUBJECTS.get("Combined_Maths"), 3.8);
        GPA_DISTRIBUTIONS.put(AL_SUBJECTS.get("Biology"), 3.6);
        GPA_DISTRIBUTIONS.put(AL_SUBJECTS.get("ICT"), 3.4);
        GPA_DISTRIBUTIONS.put(AL_SUBJECTS.get("Agriculture"), 3.3);

        // Initialize careers
        CAREERS.put("Engineering", 0);
        CAREERS.put("Medicine", 1);
        CAREERS.put("IT", 2);
        CAREERS.put("Business", 3);
        CAREERS.put("Teaching", 4);
        CAREERS.put("Research", 5);

        // Initialize career success probabilities
        CAREER_SUCCESS_PROBABILITY.put(CAREERS.get("Engineering"), 50.0);
        CAREER_SUCCESS_PROBABILITY.put(CAREERS.get("Medicine"), 50.0);
        CAREER_SUCCESS_PROBABILITY.put(CAREERS.get("IT"), 50.0);
        CAREER_SUCCESS_PROBABILITY.put(CAREERS.get("Business"), 50.0);
        CAREER_SUCCESS_PROBABILITY.put(CAREERS.get("Teaching"), 50.0);
        CAREER_SUCCESS_PROBABILITY.put(CAREERS.get("Research"), 50.0);

        // Initialize career success probability adjustments based on Z-score, career preference, GPA, and grades
        CAREER_SUCCESS_ADJUSTMENTS.put(CAREERS.get("Engineering"), Map.of(
                "Z_SCORE", 0.2,
                "CAREER_PREFERENCE", 0.5,
                "GPA", 0.3,
                "GRADES", 0.4
        ));
        CAREER_SUCCESS_ADJUSTMENTS.put(CAREERS.get("Medicine"), Map.of(
                "Z_SCORE", 0.3,
                "CAREER_PREFERENCE", 0.6,
                "GPA", 0.4,
                "GRADES", 0.5
        ));
        CAREER_SUCCESS_ADJUSTMENTS.put(CAREERS.get("IT"), Map.of(
                "Z_SCORE", 0.25,
                "CAREER_PREFERENCE", 0.55,
                "GPA", 0.35,
                "GRADES", 0.45
        ));
        CAREER_SUCCESS_ADJUSTMENTS.put(CAREERS.get("Business"), Map.of(
                "Z_SCORE", 0.15,
                "CAREER_PREFERENCE", 0.45,
                "GPA", 0.25,
                "GRADES", 0.35
        ));
        CAREER_SUCCESS_ADJUSTMENTS.put(CAREERS.get("Teaching"), Map.of(
                "Z_SCORE", 0.1,
                "CAREER_PREFERENCE", 0.4,
                "GPA", 0.2,
                "GRADES", 0.3
        ));
        CAREER_SUCCESS_ADJUSTMENTS.put(CAREERS.get("Research"), Map.of(
                "Z_SCORE", 0.05,
                "CAREER_PREFERENCE", 0.35,
                "GPA", 0.15,
                "GRADES", 0.25
        ));

        // Initialize career probability penalties based on poor performance
        CAREER_PROBABILITY_PENALTIES.put(CAREERS.get("Engineering"), Map.of(
                "Z_SCORE", -0.2,
                "CAREER_PREFERENCE", -0.5,
                "GPA", -0.3,
                "GRADES", -0.4
        ));
        CAREER_PROBABILITY_PENALTIES.put(CAREERS.get("Medicine"), Map.of(
                "Z_SCORE", -0.3,
                "CAREER_PREFERENCE", -0.6,
                "GPA", -0.4,
                "GRADES", -0.5
        ));
        CAREER_PROBABILITY_PENALTIES.put(CAREERS.get("IT"), Map.of(
                "Z_SCORE", -0.25,
                "CAREER_PREFERENCE", -0.55,
                "GPA", -0.35,
                "GRADES", -0.45
        ));
        CAREER_PROBABILITY_PENALTIES.put(CAREERS.get("Business"), Map.of(
                "Z_SCORE", -0.15,
                "CAREER_PREFERENCE", -0.45,
                "GPA", -0.25,
                "GRADES", -0.35
        ));
        CAREER_PROBABILITY_PENALTIES.put(CAREERS.get("Teaching"), Map.of(
                "Z_SCORE", -0.1,
                "CAREER_PREFERENCE", -0.4,
                "GPA", -0.2,
                "GRADES", -0.3
        ));
        CAREER_PROBABILITY_PENALTIES.put(CAREERS.get("Research"), Map.of(
                "Z_SCORE", -0.05,
                "CAREER_PREFERENCE", -0.35,
                "GPA", -0.15,
                "GRADES", -0.25
        ));
    }
}