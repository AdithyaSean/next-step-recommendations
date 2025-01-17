package com.nextstep.recommendations.trainer;

import com.nextstep.recommendations.config.Config;
import weka.classifiers.trees.RandomForest;
import weka.classifiers.meta.CVParameterSelection;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.SerializationHelper;
import weka.classifiers.evaluation.Evaluation;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class Trainer {

    public void train() throws Exception {
        DataSource source = new DataSource(Config.MODEL_DIR + "/features.arff");
        Instances data = source.getDataSet();
        data.setClassIndex(data.numAttributes() - 1); // Set career as the class attribute

        RandomForest forest = new RandomForest();
        CVParameterSelection gridSearch = new CVParameterSelection();
        gridSearch.setClassifier(forest);
        gridSearch.setNumFolds(5);
        gridSearch.setSeed(new Random().nextInt());

        gridSearch.addCVParameter("P 100 100 1");
        gridSearch.addCVParameter("M 1.0 1.0 1");
        gridSearch.addCVParameter("K 0 0 1");

        gridSearch.buildClassifier(data);

        File modelDir = new File(Config.MODEL_DIR);
        if (!modelDir.exists() && !modelDir.mkdirs()) {
            throw new IOException("Failed to create directory: " + Config.MODEL_DIR);
        }

        SerializationHelper.write(Config.MODEL_DIR + "/career_predictor.model", gridSearch);

        Evaluation eval = new Evaluation(data);
        eval.crossValidateModel(gridSearch, data, 5, new Random(1));
        System.out.println(eval.toSummaryString());
        System.out.println(eval.toMatrixString());

        if (data.classAttribute().isNominal() && data.numClasses() > 1) {
            System.out.println(eval.toClassDetailsString());
        }
    }

    public static void main(String[] args) throws Exception {
        Trainer trainer = new Trainer();
        trainer.train();
    }
}