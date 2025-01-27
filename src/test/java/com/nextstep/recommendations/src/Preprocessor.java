package com.nextstep.recommendations.src;

import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.supervised.instance.Resample;
import weka.filters.unsupervised.attribute.Standardize;
import weka.filters.supervised.instance.SMOTE;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Preprocessor {

    public void preprocess() throws Exception {
        preprocessLevel("OL_student_profiles.arff", "OL");
        preprocessLevel("AL_student_profiles.arff", "AL");
        preprocessLevel("UNI_student_profiles.arff", "UNI");
    }

    private void preprocessLevel(String arffFile, String level) throws Exception {
        DataSource source = new DataSource(Config.MODEL_DIR + "/" + arffFile);
        Instances data = source.getDataSet();

        if (data == null) {
            throw new IOException("Failed to load dataset from " + Config.MODEL_DIR + "/" + arffFile);
        }

        data.setClassIndex(data.numAttributes() - 1);

        // Standardize the data
        Standardize standardize = new Standardize();
        standardize.setInputFormat(data);
        Instances scaledFeatures = Filter.useFilter(data, standardize);

        // Handle class imbalance using SMOTE
        SMOTE smote = new SMOTE();
        smote.setPercentage(100); // Adjust the percentage as needed
        smote.setInputFormat(scaledFeatures);
        Instances balancedDataUsingSMOTE = Filter.useFilter(scaledFeatures, smote);

        // Check class distribution
        int[] classCountsSMOTE = balancedDataUsingSMOTE.attributeStats(balancedDataUsingSMOTE.classIndex()).nominalCounts;
        System.out.println("Class distribution after SMOTE:");
        for (int i = 0; i < classCountsSMOTE.length; i++) {
            System.out.println(balancedDataUsingSMOTE.classAttribute().value(i) + ": " + classCountsSMOTE[i]);
        }

        // Save the preprocessed data
        SerializationHelper.write(Config.MODEL_DIR + "/" + level + "_preprocessed.model", balancedDataUsingSMOTE);

        // Save the feature order
        Map<String, Integer> featureOrder = new HashMap<>();
        for (int i = 0; i < balancedDataUsingSMOTE.numAttributes(); i++) {
            featureOrder.put(balancedDataUsingSMOTE.attribute(i).name(), i);
        }
        SerializationHelper.write(Config.MODEL_DIR + "/" + level + "_feature_order.model", featureOrder);

        // Save the feature names
        SerializationHelper.write(Config.MODEL_DIR + "/" + level + "_feature_names.model", balancedDataUsingSMOTE);
    }
}