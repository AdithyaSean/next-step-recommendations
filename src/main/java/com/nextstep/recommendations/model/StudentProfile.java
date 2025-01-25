package com.nextstep.recommendations.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "student_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentProfile {
    @Id
    @Column(name = "profile_id")
    private UUID id;

    @Column(name = "education_level")
    private int educationLevel;

    @ElementCollection
    @CollectionTable(name = "ol_results", joinColumns = @JoinColumn(name = "profile_id"))
    @MapKeyColumn(name = "subject")
    @Column(name = "grade")
    private Map<String, Double> olResults;

    @Column(name = "al_stream")
    private Integer alStream;

    @ElementCollection
    @CollectionTable(name = "al_results", joinColumns = @JoinColumn(name = "profile_id"))
    @MapKeyColumn(name = "subject")
    @Column(name = "grade")
    private Map<String, Double> alResults;

    @ElementCollection
    @CollectionTable(name = "career_probabilities", joinColumns = @JoinColumn(name = "profile_id"))
    @MapKeyColumn(name = "career")
    @Column(name = "probability")
    private Map<String, Double> careerProbabilities;

    @Column(name = "gpa")
    private Double gpa;

    @Column(name = "created_at", nullable = false, updatable = false)
    private java.time.LocalDateTime createdAt;

    @Column(name = "updated_at")
    private java.time.LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = java.time.LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = java.time.LocalDateTime.now();
    }
}