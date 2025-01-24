package com.nextstep.recommendations.model;

import java.util.Map;

public class PredictionRequest {
    private int educationLevel;
    private Map<String, Double> olResults;
    private Integer alStream;
    private Map<String, Double> alResults;
    private Double gpa;

    public int getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(int educationLevel) {
        this.educationLevel = educationLevel;
    }

    public Map<String, Double> getOlResults() {
        return olResults;
    }

    public void setOlResults(Map<String, Double> olResults) {
        this.olResults = olResults;
    }

    public Integer getAlStream() {
        return alStream;
    }

    public void setAlStream(Integer alStream) {
        this.alStream = alStream;
    }

    public Map<String, Double> getAlResults() {
        return alResults;
    }

    public void setAlResults(Map<String, Double> alResults) {
        this.alResults = alResults;
    }

    public Double getGpa() {
        return gpa;
    }

    public void setGpa(Double gpa) {
        this.gpa = gpa;
    }
}