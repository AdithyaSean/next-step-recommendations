package com.nextstep.recommendations.src;

import jakarta.persistence.*;

import java.util.Map;

@Entity(name = "StudentProfileTest")
@Table(name = "generated_student_profiles")
public class StudentProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "education_level")
    private int educationLevel;

    @ElementCollection
    @CollectionTable(name = "generated_ol_results", joinColumns = @JoinColumn(name = "profile_id"))
    @MapKeyColumn(name = "subject")
    @Column(name = "grade")
    private Map<String, Double> olResults;

    @Column(name = "al_stream")
    private Integer alStream;

    @ElementCollection
    @CollectionTable(name = "generated_al_results", joinColumns = @JoinColumn(name = "profile_id"))
    @MapKeyColumn(name = "subject")
    @Column(name = "grade")
    private Map<String, Double> alResults;

    @Column(name = "z_score")
    private Double zScore;

    @Column(name = "gpa")
    private Double gpa;

    @ElementCollection
    @CollectionTable(name = "generated_career_probabilities", joinColumns = @JoinColumn(name = "profile_id"))
    @MapKeyColumn(name = "career")
    @Column(name = "probability")
    private Map<String, Double> careerProbabilities;

    @Column(name = "created_at", nullable = false, updatable = false)
    private java.time.LocalDateTime createdAt;

    @Column(name = "updated_at")
    private java.time.LocalDateTime updatedAt;

    public StudentProfile() {
    }

    public StudentProfile(int educationLevel, Map<String, Double> olResults) {
        this.educationLevel = educationLevel;
        this.olResults = olResults;
        this.careerProbabilities = null;
    }

    public StudentProfile(int educationLevel, Map<String, Double> olResults, Integer alStream, Map<String, Double> alResults, double zScore) {
        this.educationLevel = educationLevel;
        this.olResults = olResults;
        this.alStream = alStream;
        this.alResults = alResults;
        this.zScore = zScore;
        this.careerProbabilities = null;
    }

    public StudentProfile(int educationLevel, Map<String, Double> olResults, Integer alStream, Map<String, Double> alResults, double zScore, Double gpa) {
        this.educationLevel = educationLevel;
        this.olResults = olResults;
        this.alStream = alStream;
        this.alResults = alResults;
        this.zScore = zScore;
        this.gpa = gpa;
        this.careerProbabilities = null;
    }

    public StudentProfile(int educationLevel, Map<String, Double> olResults, Integer alStream, Map<String, Double> alResults, double zScore, Double gpa, Map<String, Double> careerProbabilities, java.time.LocalDateTime createdAt, java.time.LocalDateTime updatedAt) {
        this.educationLevel = educationLevel;
        this.olResults = olResults;
        this.alStream = alStream;
        this.alResults = alResults;
        this.zScore = zScore;
        this.gpa = gpa;
        this.careerProbabilities = careerProbabilities;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return this.id;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = java.time.LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = java.time.LocalDateTime.now();
    }

    public void setCareerProbabilities(Map<String, Double> careerProbabilities) {
        this.careerProbabilities = careerProbabilities;
    }
}