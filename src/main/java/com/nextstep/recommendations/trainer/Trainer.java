package com.nextstep.recommendations.trainer;

import com.nextstep.recommendations.config.Config;
import weka.classifiers.Classifier;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.SerializationHelper;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.meta.GridSearch;

import java.util.Random;

public class Trainer {

    public void train() throws Exception {
        DataSource source = new DataSource(Config.PROCESSED_DIR + "/features.arff");
        Instances data = source.getDataSet();
        data.setClassIndex(data.numAttributes() - 1);

        RandomForest forest = new RandomForest();

        GridSearch gridSearch = new GridSearch();
        gridSearch.setClassifier(forest);
        gridSearch.setEvaluation(new Evaluation(data));
        gridSearch.setSeed(new Random().nextInt());
        gridSearch.setOptions(new String[]{
            "-E", "r2",
            "-X", "5",
            "-S", "1",
            "-W", "weka.classifiers.trees.RandomForest",
            "--", "-P", "100", "-I", "100", "-K", "0", "-M", "1.0", "-V", "0.001", "-S", "1"
        });

        gridSearch.buildClassifier(data);

        SerializationHelper.write(Config.MODEL_DIR + "/career_predictor.model", gridSearch.getBestClassifier());

        Evaluation eval = new Evaluation(data);
        eval.crossValidateModel(gridSearch.getBestClassifier(), data, 5, new Random(1));
        System.out.println(eval.toSummaryString());
    }

    public static void main(String[] args) throws Exception {
        Trainer trainer = new Trainer();
        trainer.train();
    }
}
