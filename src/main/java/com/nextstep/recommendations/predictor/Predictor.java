package com.nextstep.recommendations.predictor;

import com.nextstep.recommendations.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.classifiers.Classifier;
import weka.core.DenseInstance;

import java.util.Map;
import java.util.HashMap;

@Service
public class Predictor {

    private final Classifier model;

    @Autowired
    public Predictor(@Value("${model.path}") String modelPath) throws Exception {
        this.model = (Classifier) weka.core.SerializationHelper.read(modelPath);
    }

    public Map<String, Double> predict(int educationLevel, Map<String, Double> olResults, Integer alStream, Map<String, Double> alResults, Double gpa) throws Exception {
        Instances dataset = DataSource.read(Config.MODEL_DIR + "/feature_order.arff");
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
            String career = Config.CAREERS.keySet().toArray()[i].toString();
            careerProbabilities.put(career, predictions[i] * 100);
        }

        return careerProbabilities;
    }

    public static void main(String[] args) throws Exception {
        Predictor predictor = new Predictor(Config.MODEL_DIR + "/career_predictor.model");

        Map<String, Double> olResults = Map.of(
            "0", 85.0,
            "1", 78.0,
            "2", 72.0,
            "3", 65.0,
            "4", 70.0,
            "5", 75.0
        );

        System.out.println("\nOL Student Profile:");
        System.out.println("==================");
        System.out.println("\nPredicted Career Probabilities:");
        System.out.println(predictor.predict(0, olResults, null, null, null));

        Map<String, Double> alResults = Map.of(
            "0", 88.0,
            "1", 82.0,
            "2", 90.0
        );

        System.out.println("\nAL Science Student Profile:");
        System.out.println("=========================");
        System.out.println("\nPredicted Career Probabilities:");
        System.out.println(predictor.predict(1, olResults, 0, alResults, null));

        System.out.println("\nUniversity Student Profile:");
        System.out.println("=========================");
        System.out.println("\nPredicted Career Probabilities:");
        System.out.println(predictor.predict(2, olResults, 0, alResults, 3.75));
    }
}
