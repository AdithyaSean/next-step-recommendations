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

    public Map<String, Double> getOlResults() {
        return olResults;
    }

    public Integer getAlStream() {
        return alStream;
    }

    public Map<String, Double> getAlResults() {
        return alResults;
    }

    public Double getGpa() {
        return gpa;
    }
}
