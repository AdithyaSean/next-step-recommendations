package com.nextstep.recommendations.src;

import weka.core.Instances;
import weka.classifiers.Classifier;
import weka.core.DenseInstance;

import java.util.HashMap;
import java.util.Map;

public class TestPredictor {

    private final Map<String, Classifier> models;
    private final Map<String, Map<String, Integer>> featureOrders;
    private final Map<String, Instances> featureNames;

    public TestPredictor() throws Exception {
        models = new HashMap<>();
        featureOrders = new HashMap<>();
        featureNames = new HashMap<>();

        loadModel("OL_career_predictor.model", "OL");
        loadModel("AL_career_predictor.model", "AL");
        loadModel("UNI_career_predictor.model", "UNI");
    }

    private void loadModel(String modelFile, String level) throws Exception {
        models.put(level, (Classifier) weka.core.SerializationHelper.read(Config.MODEL_DIR + "/" + modelFile));
        //noinspection unchecked
        featureOrders.put(level, (Map<String, Integer>) weka.core.SerializationHelper.read(Config.MODEL_DIR + "/" + level + "_feature_order.model"));
        featureNames.put(level, (Instances) weka.core.SerializationHelper.read(Config.MODEL_DIR + "/" + level + "_feature_names.model"));
    }

    public Map<String, Double> predict(int educationLevel, Map<String, Integer> olResults, Integer alStream, Map<String, Integer> alResults, Double gpa) throws Exception {
        String level = getEducationLevel(educationLevel);
        Instances dataset = new Instances(featureNames.get(level));
        dataset.setClassIndex(dataset.numAttributes() - 1);

        DenseInstance instance = new DenseInstance(dataset.numAttributes());
        instance.setDataset(dataset);

        instance.setValue(dataset.attribute("education_level"), educationLevel);
        instance.setValue(dataset.attribute("AL_stream"), alStream != null ? alStream : -1);
        instance.setValue(dataset.attribute("gpa"), gpa != null ? gpa : -1);

        for (Map.Entry<String, Integer> entry : olResults.entrySet()) {
            String colName = "OL_subject_" + entry.getKey() + "_score";
            if (featureOrders.get(level).containsKey(colName)) {
                instance.setValue(featureOrders.get(level).get(colName), entry.getValue());
            }
        }

        if (alResults != null) {
            for (Map.Entry<String, Integer> entry : alResults.entrySet()) {
                String colName = "AL_subject_" + entry.getKey() + "_score";
                if (featureOrders.get(level).containsKey(colName)) {
                    instance.setValue(featureOrders.get(level).get(colName), entry.getValue());
                }
            }
        }

        double[] predictions = models.get(level).distributionForInstance(instance);

        Map<String, Double> careerProbabilities = new HashMap<>();
        for (int i = 0; i < predictions.length; i++) {
            String career = dataset.classAttribute().value(i);
            careerProbabilities.put(career, predictions[i] * 100);
        }

        return careerProbabilities;
    }

    private String getEducationLevel(int educationLevel) {
        if (educationLevel == Config.EDUCATION_LEVELS.get("OL")) {
            return "OL";
        } else if (educationLevel == Config.EDUCATION_LEVELS.get("AL")) {
            return "AL";
        } else {
            return "UNI";
        }
    }
}