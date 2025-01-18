package com.nextstep.recommendations.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
@Entity
public class CareerPrediction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int educationLevel;
    private Integer alStream;
    private Double gpa;

    @ElementCollection
    @CollectionTable(name = "ol_results", joinColumns = @JoinColumn(name = "prediction_id"))
    @MapKeyColumn(name = "subject")
    @Column(name = "score")
    private Map<String, Double> olResults;

    @ElementCollection
    @CollectionTable(name = "al_results", joinColumns = @JoinColumn(name = "prediction_id"))
    @MapKeyColumn(name = "subject")
    @Column(name = "score")
    private Map<String, Double> alResults;

    @ElementCollection
    @CollectionTable(name = "career_probabilities", joinColumns = @JoinColumn(name = "prediction_id"))
    @MapKeyColumn(name = "career")
    @Column(name = "probability")
    private Map<String, Double> careerProbabilities;

    @Temporal(TemporalType.TIMESTAMP)
    private Date predictionDate;

    @PrePersist
    protected void onCreate() {
        predictionDate = new Date();
    }
}