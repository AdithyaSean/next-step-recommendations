package com.nextstep.recommendations.config;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class Config {
    public static final Map<String, Integer> EDUCATION_LEVELS = new HashMap<>();
    public static final Map<String, Integer> OL_SUBJECTS = new HashMap<>();
    public static final Map<String, Integer> AL_STREAMS = new HashMap<>();
    public static final Map<String, Integer> AL_SUBJECTS = new HashMap<>();
    public static final Map<String, Integer> CAREERS = new HashMap<>();
    public static final Map<Integer, Double> EDUCATION_LEVEL_DIST = new HashMap<>();
    public static final Map<Integer, List<Integer>> AL_SUBJECTS_BY_STREAM = new HashMap<>();
    public static final Map<Integer, double[]> CAREER_SUCCESS_RANGES = new HashMap<>();

    static {
        EDUCATION_LEVELS.put("OL", 0);
        EDUCATION_LEVELS.put("AL", 1);
        EDUCATION_LEVELS.put("UNI", 2);

        OL_SUBJECTS.put("Maths", 0);
        OL_SUBJECTS.put("Science", 1);
        OL_SUBJECTS.put("English", 2);
        OL_SUBJECTS.put("Sinhala", 3);
        OL_SUBJECTS.put("History", 4);
        OL_SUBJECTS.put("Religion", 5);

        AL_STREAMS.put("Physical Science", 0);
        AL_STREAMS.put("Biological Science", 1);
        AL_STREAMS.put("Commerce", 2);
        AL_STREAMS.put("Arts", 3);
        AL_STREAMS.put("Technology", 4);

        AL_SUBJECTS.put("Physics", 0);
        AL_SUBJECTS.put("Chemistry", 1);
        AL_SUBJECTS.put("Combined_Maths", 2);
        AL_SUBJECTS.put("Biology", 3);
        AL_SUBJECTS.put("Accounting", 4);
        AL_SUBJECTS.put("Business_Studies", 5);
        AL_SUBJECTS.put("Economics", 6);
        AL_SUBJECTS.put("History", 7);
        AL_SUBJECTS.put("Geography", 8);
        AL_SUBJECTS.put("Politics", 9);
        AL_SUBJECTS.put("Engineering_Tech", 10);
        AL_SUBJECTS.put("Science_Tech", 11);
        AL_SUBJECTS.put("ICT", 12);

        CAREERS.put("Engineering", 0);
        CAREERS.put("Medicine", 1);
        CAREERS.put("IT", 2);
        CAREERS.put("Business", 3);
        CAREERS.put("Teaching", 4);
        CAREERS.put("Research", 5);

        EDUCATION_LEVEL_DIST.put(EDUCATION_LEVELS.get("OL"), 0.4);
        EDUCATION_LEVEL_DIST.put(EDUCATION_LEVELS.get("AL"), 0.5);
        EDUCATION_LEVEL_DIST.put(EDUCATION_LEVELS.get("UNI"), 0.1);

        AL_SUBJECTS_BY_STREAM.put(AL_STREAMS.get("Physical Science"), List.of(AL_SUBJECTS.get("Physics"), AL_SUBJECTS.get("Chemistry"), AL_SUBJECTS.get("Combined_Maths")));
        AL_SUBJECTS_BY_STREAM.put(AL_STREAMS.get("Biological Science"), List.of(AL_SUBJECTS.get("Biology"), AL_SUBJECTS.get("Chemistry"), AL_SUBJECTS.get("Physics")));
        AL_SUBJECTS_BY_STREAM.put(AL_STREAMS.get("Commerce"), List.of(AL_SUBJECTS.get("Accounting"), AL_SUBJECTS.get("Business_Studies"), AL_SUBJECTS.get("Economics")));
        AL_SUBJECTS_BY_STREAM.put(AL_STREAMS.get("Arts"), List.of(AL_SUBJECTS.get("History"), AL_SUBJECTS.get("Geography"), AL_SUBJECTS.get("Politics")));
        AL_SUBJECTS_BY_STREAM.put(AL_STREAMS.get("Technology"), List.of(AL_SUBJECTS.get("Engineering_Tech"), AL_SUBJECTS.get("Science_Tech"), AL_SUBJECTS.get("ICT")));

        CAREER_SUCCESS_RANGES.put(CAREERS.get("Engineering"), new double[]{0.55, 0.95});
        CAREER_SUCCESS_RANGES.put(CAREERS.get("Medicine"), new double[]{0.60, 0.92});
        CAREER_SUCCESS_RANGES.put(CAREERS.get("IT"), new double[]{0.50, 0.90});
        CAREER_SUCCESS_RANGES.put(CAREERS.get("Business"), new double[]{0.45, 0.85});
        CAREER_SUCCESS_RANGES.put(CAREERS.get("Teaching"), new double[]{0.40, 0.80});
        CAREER_SUCCESS_RANGES.put(CAREERS.get("Research"), new double[]{0.55, 0.88});
    }
    public static final double[] GPA_RANGE = {2.0, 4.0};
    public static final int NUM_STUDENTS = 5000;
    public static final String DATA_DIR = "./data";
    public static final String MODEL_DIR = "./models";
}
