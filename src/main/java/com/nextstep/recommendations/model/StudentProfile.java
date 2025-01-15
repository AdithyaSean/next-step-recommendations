package com.nextstep.recommendations.model;

import java.util.Map;

public class StudentProfile {
    private int educationLevel;
    private int alStream;
    private double gpa;
    private Map<String, Double> olResults;
    private Map<String, Double> alResults;

    public int getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(int educationLevel) {
        this.educationLevel = educationLevel;
    }

    public int getAlStream() {
        return alStream;
    }

    public void setAlStream(int alStream) {
        this.alStream = alStream;
    }

    public double getGpa() {
        return gpa;
    }

    public void setGpa(double gpa) {
        this.gpa = gpa;
    }

    public Map<String, Double> getOlResults() {
        return olResults;
    }

    public void setOlResults(Map<String, Double> olResults) {
        this.olResults = olResults;
    }

    public Map<String, Double> getAlResults() {
        return alResults;
    }

    public void setAlResults(Map<String, Double> alResults) {
        this.alResults = alResults;
    }
}
