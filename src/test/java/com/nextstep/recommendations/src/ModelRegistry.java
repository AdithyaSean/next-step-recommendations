package com.nextstep.recommendations.src;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class ModelRegistry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Integer streamId;
    private int version;
    private LocalDateTime timestamp;
    private String modelPath;

    public ModelRegistry() {}

    public ModelRegistry(Integer streamId, int version, LocalDateTime timestamp, String modelPath) {
        this.streamId = streamId;
        this.version = version;
        this.timestamp = timestamp;
        this.modelPath = modelPath;
    }

    // Getters
    public Long getId() { return id; }
    public Integer getStreamId() { return streamId; }
    public int getVersion() { return version; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getModelPath() { return modelPath; }

    // Setters
    public void setStreamId(Integer streamId) { this.streamId = streamId; }
    public void setVersion(int version) { this.version = version; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public void setModelPath(String modelPath) { this.modelPath = modelPath; }
}
