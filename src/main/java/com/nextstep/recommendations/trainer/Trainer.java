package com.nextstep.recommendations.trainer;

import com.nextstep.recommendations.config.Config;
import weka.classifiers.Classifier;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.SerializationHelper;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.meta.CVParameterSelection;

import java.util.Random;

public class Trainer {

    public void train() throws Exception {
        DataSource source = new DataSource(Config.PROCESSED_DIR + "/features.arff");
        Instances data = source.getDataSet();
        data.setClassIndex(data.numAttributes() - 1);

        RandomForest forest = new RandomForest();

        CVParameterSelection gridSearch = new CVParameterSelection();
        gridSearch.setClassifier(forest);
        gridSearch.setNumFolds(5);
        gridSearch.setSeed(new Random().nextInt());

        gridSearch.addCVParameter("P 100 100 1");
        gridSearch.addCVParameter("M 1.0 1.0 1");
        gridSearch.addCVParameter("K 0 0 1");

        gridSearch.buildClassifier(data);

        SerializationHelper.write(Config.MODEL_DIR + "/career_predictor.model", gridSearch);

        Evaluation eval = new Evaluation(data);
        eval.crossValidateModel(gridSearch, data, 5, new Random(1));
        System.out.println(eval.toSummaryString());
        System.out.println(eval.toClassDetailsString());
        System.out.println(eval.toMatrixString());
    }

    public static void main(String[] args) throws Exception {
        Trainer trainer = new Trainer();
        trainer.train();
    }
}
