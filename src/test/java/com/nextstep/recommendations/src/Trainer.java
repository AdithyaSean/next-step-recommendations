package com.nextstep.recommendations.src;

import weka.classifiers.meta.CostSensitiveClassifier;
import weka.classifiers.CostMatrix;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils.DataSource;
import weka.classifiers.Evaluation;

import java.io.IOException;
import java.util.Random;
import java.io.File;

public class Trainer {

    public void train() throws Exception {
        trainModel("OL_student_profiles.arff", "OL_career_predictor.model");
        trainModel("AL_student_profiles.arff", "AL_career_predictor.model");
        trainModel("UNI_student_profiles.arff", "UNI_career_predictor.model");
    }

    private void trainModel(String arffFile, String modelFile) throws Exception {
        DataSource source = new DataSource(Config.MODEL_DIR + "/" + arffFile);
        Instances data = source.getDataSet();
        data.setClassIndex(data.numAttributes() - 1);

        RandomForest forest = new RandomForest();
        forest.setNumIterations(100);

        CostMatrix costMatrix = new CostMatrix(data.numClasses());
        for (int i = 0; i < data.numClasses(); i++) {
            for (int j = 0; j < data.numClasses(); j++) {
                if (i != j) {
                    costMatrix.setElement(i, j, 1.0);
                }
            }
        }

        CostSensitiveClassifier costSensitiveClassifier = new CostSensitiveClassifier();
        costSensitiveClassifier.setClassifier(forest);
        costSensitiveClassifier.setCostMatrix(costMatrix);
        costSensitiveClassifier.setMinimizeExpectedCost(true);

        costSensitiveClassifier.buildClassifier(data);

        File modelDir = new File(Config.MODEL_DIR);
        if (!modelDir.exists() && !modelDir.mkdirs()) {
            throw new IOException("Failed to create directory: " + Config.MODEL_DIR);
        }

        SerializationHelper.write(Config.MODEL_DIR + "/" + modelFile, costSensitiveClassifier);

        Evaluation eval = new Evaluation(data);
        eval.crossValidateModel(costSensitiveClassifier, data, 5, new Random(1));
        System.out.println(eval.toSummaryString());
        System.out.println(eval.toMatrixString());

        if (data.classAttribute().isNominal() && data.numClasses() > 1) {
            System.out.println(eval.toClassDetailsString());
        }
    }
}