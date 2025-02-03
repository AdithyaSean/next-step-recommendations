package com.nextstep.recommendations.src;

public class Trainer {
    private final StudentProfileRepository studentProfileRepository;
    private final ModelRegistryRepository modelRegistryRepository;

    public Trainer(StudentProfileRepository studentProfileRepository, ModelRegistryRepository modelRegistryRepository) {
        this.studentProfileRepository = studentProfileRepository;
        this.modelRegistryRepository = modelRegistryRepository;
    }

    // Stream model training implementation
    public List<Integer> discoverStreams() {
        return studentProfileRepository.findDistinctAlStreams()
            .stream()
            .filter(stream -> stream != null)
            .collect(Collectors.toList());
    }

    public void trainStreamModel(int streamId) {
        List<StudentProfile> profiles = studentProfileRepository.findByAlStream(streamId);
        
        // Dynamic feature mapping
        Map<String, Double> featureWeights = profiles.stream()
            .flatMap(p -> p.getAlResults().keySet().stream())
            .distinct()
            .collect(Collectors.toMap(
                subject -> "al_" + subject,
                subject -> 1.0, // Initial weight
                (existing, replacement) -> existing
            ));
        
        // Model versioning
        int newVersion = getNextModelVersion(streamId);
        String modelName = String.format("stream_%d_v%d.model", streamId, newVersion);
        
        trainAndSaveModel(profiles, featureWeights, modelName);
        updateModelRegistry(streamId, newVersion, LocalDateTime.now());
    }

    private int getNextModelVersion(int streamId) {
        return modelRegistryRepository.findFirstByStreamIdOrderByVersionDesc(streamId)
            .map(ModelRegistry::getVersion)
            .orElse(0) + 1;
    }

    private void updateModelRegistry(Integer streamId, int version, LocalDateTime timestamp) {
        String modelPath = String.format("models/stream_models/stream_%d_v%d.model", streamId, version);
        modelRegistryRepository.save(new ModelRegistry(streamId, version, timestamp, modelPath));
    }

    // Updated prediction method
    public CareerPrediction predict(StudentProfile profile) {
        if (profile.getEducationLevel() == Config.EDUCATION_LEVELS.get("UNI")) {
            return universityModel.predict(profile);
        }
        
        Integer streamId = profile.getAlStream();
        if (streamId == null) {
            throw new IllegalArgumentException("AL stream required for prediction");
        }
        
        int modelVersion = modelRegistryRepository.findFirstByStreamIdOrderByVersionDesc(streamId)
            .orElseThrow(() -> new ModelNotFoundException(streamId))
            .getVersion();
            
        String modelName = String.format("stream_%d_v%d.model", streamId, modelVersion);
        ModelWrapper model = loadModel(modelName);
        
        return model.predict(profile);
    }
    public void trainOLCareerPredictionModel() {
        // TODO: Implement training logic for OL students career prediction model
        // This method should train the model using the data from the ordinary level students processed dataset
        // The model should be saved to disk after training
    }

    public void trainALCareerPredictionModel() {
        // TODO: Implement training logic for AL students career prediction model
        // This method should train the model using the data from the advanced level students processed dataset
        // The model should be saved to disk after training
    }

    public void trainUNICareerPredictionModel() {
        // TODO: Implement training logic for UNI students career prediction model
        // This method should train the model using the data from the university students processed dataset
        // The model should be saved to disk after training
    }
}
