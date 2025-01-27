package com.nextstep.recommendations.src;

import java.util.List;
import java.util.Map;

class Test {
    public static void main(String[] args) throws Exception {
        Generator generator = new Generator();
        List<Map<String, Object>> data = generator.generate();
        generator.saveToARFF(data);

        Preprocessor preprocessor = new Preprocessor();
        preprocessor.preprocess();

        Trainer trainer = new Trainer();
        trainer.train();

        TestPredictor testPredictor = new TestPredictor(Config.MODEL_DIR + "/career_predictor.model");

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
        System.out.println(testPredictor.predict(0, olResults, null, null, null));

        Map<String, Double> alResults = Map.of(
                "0", 88.0,
                "1", 82.0,
                "2", 90.0
        );

        System.out.println("\nAL Science Student Profile:");
        System.out.println("=========================");
        System.out.println("\nPredicted Career Probabilities:");
        System.out.println(testPredictor.predict(1, olResults, 0, alResults, null));

        System.out.println("\nUniversity Student Profile:");
        System.out.println("=========================");
        System.out.println("\nPredicted Career Probabilities:");
        System.out.println(testPredictor.predict(2, olResults, 0, alResults, 3.75));
    }
}
