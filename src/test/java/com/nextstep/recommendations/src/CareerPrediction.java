package com.nextstep.recommendations.src;

import java.util.HashMap;
import java.util.Map;

public class CareerPrediction {
    private Map<Integer, Double> careerPrediction;
    private Map<Integer, Integer> olResults;
    private Map<Integer, Integer> alResults;
    private double zScore;
    private double gpa;

    public CareerPrediction(Map<Integer, Integer> olResults, Map<Integer, Integer> alResults, double zScore, double gpa) {
        this.olResults = olResults;
        this.alResults = alResults;
        this.zScore = zScore;
        this.gpa = gpa;
        this.careerPrediction = new HashMap<>();
    }

    public CareerPrediction(Map<Integer, Integer> olResults, Map<Integer, Integer> alResults, double zScore) {
        this.olResults = olResults;
        this.alResults = alResults;
        this.zScore = zScore;
        this.careerPrediction = new HashMap<>();
    }

    public CareerPrediction(Map<Integer, Integer> olResults) {
        this.olResults = olResults;
        this.careerPrediction = new HashMap<>();
    }

    public Map<Integer, Double> predictOL() {
        // Simulate sending request to the model and getting predictions for OL students
        // TODO: Replace with actual model prediction logic
        for (Integer careerId : Config.CAREERS.values()) {
            careerPrediction.put(careerId, Math.random() * 100);
        }
        return careerPrediction;
    }

    public Map<Integer, Double> predictAL() {
        // Simulate sending request to the model and getting predictions for AL students
        // TODO: Replace with actual model prediction logic
        for (Integer careerId : Config.CAREERS.values()) {
            careerPrediction.put(careerId, Math.random() * 100);
        }
        return careerPrediction;
    }

    public Map<Integer, Double> predictUNI() {
        // Simulate sending request to the model and getting predictions for UNI students
        // TODO: Replace with actual model prediction logic
        for (Integer careerId : Config.CAREERS.values()) {
            careerPrediction.put(careerId, Math.random() * 100);
        }
        return careerPrediction;
    }

    public void printPredictions(Map<Integer, Double> predictions) {
        System.out.println("Input Features:");
        if (olResults != null) {
            System.out.println("OL Results:");
            for (Map.Entry<Integer, Integer> entry : olResults.entrySet()) {
                System.out.println(getSubjectName(entry.getKey()) + ": " + getGradeName(entry.getValue()));
            }
        }
        if (alResults != null) {
            System.out.println("AL Results:");
            for (Map.Entry<Integer, Integer> entry : alResults.entrySet()) {
                System.out.println(getSubjectName(entry.getKey()) + ": " + getGradeName(entry.getValue()));
            }
        }
        if (zScore != 0) {
            System.out.println("Z-Score: " + zScore);
        }
        if (gpa != 0) {
            System.out.println("GPA: " + gpa);
        }

        System.out.println("\nPredicted Career Probabilities:");
        for (Map.Entry<Integer, Double> entry : predictions.entrySet()) {
            String careerName = getCareerName(entry.getKey());
            System.out.println(careerName + ": " + entry.getValue() + "%");
        }
    }

    private String getSubjectName(int subjectId) {
        for (Map.Entry<String, Integer> entry : Config.OL_SUBJECTS.entrySet()) {
            if (entry.getValue() == subjectId) {
                return entry.getKey();
            }
        }
        for (Map.Entry<String, Integer> entry : Config.AL_SUBJECTS.entrySet()) {
            if (entry.getValue() == subjectId) {
                return entry.getKey();
            }
        }
        return "Unknown Subject";
    }

    private String getGradeName(int gradeId) {
        for (Map.Entry<String, Integer> entry : Config.GRADES.entrySet()) {
            if (entry.getValue() == gradeId) {
                return entry.getKey();
            }
        }
        return "Unknown Grade";
    }

    private String getCareerName(int careerId) {
        for (Map.Entry<String, Integer> entry : Config.CAREERS.entrySet()) {
            if (entry.getValue() == careerId) {
                return entry.getKey();
            }
        }
        return "Unknown Career";
    }
}