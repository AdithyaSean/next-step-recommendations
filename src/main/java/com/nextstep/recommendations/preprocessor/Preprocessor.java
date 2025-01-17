package com.nextstep.recommendations.preprocessor;

import com.nextstep.recommendations.config.Config;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Standardize;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils.DataSink;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Preprocessor {

    public void preprocess() throws Exception {
        // Load data
        DataSource source = new DataSource(Config.DATA_DIR + "/student_profiles.arff");
        Instances data = source.getDataSet();

        // Separate features and target
        Instances features = new Instances(data);
        features.setClassIndex(-1);  // No class index for features

        // Standardize numerical features
        Standardize standardize = new Standardize();
        standardize.setInputFormat(features);
        Instances scaledFeatures = Filter.useFilter(features, standardize);

        // Ensure model directory exists
        File modelDir = new File(Config.MODEL_DIR);
        if (!modelDir.exists() && !modelDir.mkdirs()) {
            throw new IOException("Failed to create directory: " + Config.MODEL_DIR);
        }

        // Save scaler and feature names
        SerializationHelper.write(Config.MODEL_DIR + "/scaler.model", standardize);
        SerializationHelper.write(Config.MODEL_DIR + "/feature_names.model", scaledFeatures);
        // Save feature order
        Map<String, Integer> featureOrder = new HashMap<>();
        for (int i = 0; i < scaledFeatures.numAttributes(); i++) {
            featureOrder.put(scaledFeatures.attribute(i).name(), i);
        }
        SerializationHelper.write(Config.MODEL_DIR + "/feature_order.model", featureOrder);

        // Save processed data
        File processedDir = new File(Config.PROCESSED_DIR);
        if (!processedDir.exists() && !processedDir.mkdirs()) {
            throw new IOException("Failed to create directory: " + Config.PROCESSED_DIR);
        }
        DataSink.write(Config.PROCESSED_DIR + "/features.arff", scaledFeatures);
    }

    public static void main(String[] args) throws Exception {
        Preprocessor preprocessor = new Preprocessor();
        preprocessor.preprocess();
    }
}
