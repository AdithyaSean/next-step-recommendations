package com.nextstep.recommendations.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
public class StudentProfileDTO {
    private UUID id;
    private int educationLevel;
    private Map<String, Double> olResults;
    private Integer alStream;
    private Map<String, Double> alResults;
    private Map<String, Double> careerProbabilities;
    private Double gpa;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}