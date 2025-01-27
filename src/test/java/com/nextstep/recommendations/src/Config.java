package com.nextstep.recommendations.src;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class Config {
    // Education levels
    public static final Map<String, Integer> EDUCATION_LEVELS = new HashMap<>();
    // OL subjects and their IDs
    public static final Map<String, Integer> OL_SUBJECTS = new HashMap<>();
    // AL streams and their IDs
    public static final Map<String, Integer> AL_STREAMS = new HashMap<>();
    // AL subjects and their IDs
    public static final Map<String, Integer> AL_SUBJECTS = new HashMap<>();
    // Careers and their IDs
    public static final Map<String, Integer> CAREERS = new HashMap<>();
    // Distribution of education levels
    public static final Map<Integer, Double> EDUCATION_LEVEL_DIST = new HashMap<>();
    // AL subjects by stream
    public static final Map<Integer, List<Integer>> AL_SUBJECTS_BY_STREAM = new HashMap<>();
    // Career success ranges by career ID
    public static final Map<Integer, double[]> CAREER_SUCCESS_RANGES = new HashMap<>();
    // Careers by education level
    public static final Map<Integer, List<Integer>> CAREERS_BY_EDUCATION_LEVEL = new HashMap<>();

    // GPA range
    public static final double[] GPA_RANGE = {1.0, 4.0};
    // Number of students to generate
    public static final int NUM_STUDENTS = 10000;
    // Directory to save models
    public static final String MODEL_DIR = "./models";

    static {
        // Initialize education levels
        EDUCATION_LEVELS.put("OL", 1); // Ordinary Level
        EDUCATION_LEVELS.put("AL", 2); // Advanced Level
        EDUCATION_LEVELS.put("UNI", 3); // University

        // Initialize OL subjects
        OL_SUBJECTS.put("Maths", 1);
        OL_SUBJECTS.put("Science", 2);
        OL_SUBJECTS.put("English", 3);
        OL_SUBJECTS.put("Sinhala", 4);
        OL_SUBJECTS.put("History", 5);
        OL_SUBJECTS.put("Religion", 6);

        // Initialize AL streams
        AL_STREAMS.put("Physical Science", 1);
        AL_STREAMS.put("Biological Science", 2);
        AL_STREAMS.put("Commerce", 3);
        AL_STREAMS.put("Arts", 4);
        AL_STREAMS.put("Technology", 5);

        // Initialize AL subjects
        AL_SUBJECTS.put("Physics", 1);
        AL_SUBJECTS.put("Chemistry", 2);
        AL_SUBJECTS.put("Combined_Maths", 3);
        AL_SUBJECTS.put("Biology", 4);
        AL_SUBJECTS.put("Accounting", 5);
        AL_SUBJECTS.put("Business_Studies", 6);
        AL_SUBJECTS.put("Economics", 7);
        AL_SUBJECTS.put("History", 8);
        AL_SUBJECTS.put("Geography", 9);
        AL_SUBJECTS.put("Politics", 10);
        AL_SUBJECTS.put("Engineering_Tech", 11);
        AL_SUBJECTS.put("Science_Tech", 12);
        AL_SUBJECTS.put("ICT", 13);

        // Initialize careers
        CAREERS.put("Engineering", 1);
        CAREERS.put("Medicine", 2);
        CAREERS.put("IT", 3);
        CAREERS.put("Business", 4);
        CAREERS.put("Teaching", 5);
        CAREERS.put("Research", 6);

        // Initialize education level distribution
        EDUCATION_LEVEL_DIST.put(EDUCATION_LEVELS.get("OL"), 0.4);
        EDUCATION_LEVEL_DIST.put(EDUCATION_LEVELS.get("AL"), 0.5);
        EDUCATION_LEVEL_DIST.put(EDUCATION_LEVELS.get("UNI"), 0.1);

        // Initialize AL subjects by stream
        AL_SUBJECTS_BY_STREAM.put(AL_STREAMS.get("Physical Science"), List.of(AL_SUBJECTS.get("Physics"), AL_SUBJECTS.get("Chemistry"), AL_SUBJECTS.get("Combined_Maths")));
        AL_SUBJECTS_BY_STREAM.put(AL_STREAMS.get("Biological Science"), List.of(AL_SUBJECTS.get("Biology"), AL_SUBJECTS.get("Chemistry"), AL_SUBJECTS.get("Physics")));
        AL_SUBJECTS_BY_STREAM.put(AL_STREAMS.get("Commerce"), List.of(AL_SUBJECTS.get("Accounting"), AL_SUBJECTS.get("Business_Studies"), AL_SUBJECTS.get("Economics")));
        AL_SUBJECTS_BY_STREAM.put(AL_STREAMS.get("Arts"), List.of(AL_SUBJECTS.get("History"), AL_SUBJECTS.get("Geography"), AL_SUBJECTS.get("Politics")));
        AL_SUBJECTS_BY_STREAM.put(AL_STREAMS.get("Technology"), List.of(AL_SUBJECTS.get("Engineering_Tech"), AL_SUBJECTS.get("Science_Tech"), AL_SUBJECTS.get("ICT")));

        // Initialize career success ranges
        CAREER_SUCCESS_RANGES.put(CAREERS.get("Engineering"), new double[]{0.55, 0.95});
        CAREER_SUCCESS_RANGES.put(CAREERS.get("Medicine"), new double[]{0.60, 0.92});
        CAREER_SUCCESS_RANGES.put(CAREERS.get("IT"), new double[]{0.50, 0.90});
        CAREER_SUCCESS_RANGES.put(CAREERS.get("Business"), new double[]{0.45, 0.85});
        CAREER_SUCCESS_RANGES.put(CAREERS.get("Teaching"), new double[]{0.40, 0.80});
        CAREER_SUCCESS_RANGES.put(CAREERS.get("Research"), new double[]{0.55, 0.88});

        // Initialize careers by education level
        CAREERS_BY_EDUCATION_LEVEL.put(EDUCATION_LEVELS.get("OL"), List.of(CAREERS.get("Teaching"), CAREERS.get("Business")));
        CAREERS_BY_EDUCATION_LEVEL.put(EDUCATION_LEVELS.get("AL"), List.of(CAREERS.get("Engineering"), CAREERS.get("Medicine"), CAREERS.get("IT")));
        CAREERS_BY_EDUCATION_LEVEL.put(EDUCATION_LEVELS.get("UNI"), List.of(CAREERS.get("Engineering"), CAREERS.get("Medicine"), CAREERS.get("IT"), CAREERS.get("Research")));
    }
}