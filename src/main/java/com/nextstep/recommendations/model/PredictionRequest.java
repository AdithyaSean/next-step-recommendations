package com.nextstep.recommendations.model;

import lombok.Data;

import java.util.Map;

@Data
public class PredictionRequest {
    private int educationLevel;
    private Map<String, Double> olResults;
    private Integer alStream;
    private Map<String, Double> alResults;
    private Double gpa;
}
