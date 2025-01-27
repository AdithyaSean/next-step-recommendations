package com.nextstep.recommendations.src;

import org.springframework.beans.factory.annotation.Value;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.classifiers.Classifier;
import weka.core.DenseInstance;

import java.util.HashMap;
import java.util.Map;

public class TestPredictor {

    private final Classifier model;

    public TestPredictor(@Value("${model.path}") String modelPath) throws Exception {
        this.model = (Classifier) weka.core.SerializationHelper.read(modelPath);
    }

    public Map<String, Double> predict(int educationLevel, Map<String, Double> olResults, Integer alStream, Map<String, Double> alResults, Double gpa) throws Exception {
        Instances dataset = DataSource.read(Config.MODEL_DIR + "/features.arff");
        dataset.setClassIndex(dataset.numAttributes() - 1);

        DenseInstance instance = new DenseInstance(dataset.numAttributes());
        instance.setDataset(dataset);

        instance.setValue(dataset.attribute("education_level"), educationLevel);
        instance.setValue(dataset.attribute("AL_stream"), alStream != null ? alStream : -1);
        instance.setValue(dataset.attribute("gpa"), gpa != null ? gpa : -1);

        for (Map.Entry<String, Double> entry : olResults.entrySet()) {
            String colName = "OL_subject_" + entry.getKey() + "_score";
            if (dataset.attribute(colName) != null) {
                instance.setValue(dataset.attribute(colName), entry.getValue());
            }
        }

        if (alResults != null) {
            for (Map.Entry<String, Double> entry : alResults.entrySet()) {
                String colName = "AL_subject_" + entry.getKey() + "_score";
                if (dataset.attribute(colName) != null) {
                    instance.setValue(dataset.attribute(colName), entry.getValue());
                }
            }
        }

        double[] predictions = model.distributionForInstance(instance);

        Map<String, Double> careerProbabilities = new HashMap<>();
        for (int i = 0; i < predictions.length; i++) {
            String career = dataset.classAttribute().value(i);
            careerProbabilities.put(career, predictions[i] * 100);
        }

        return careerProbabilities;
    }
}
