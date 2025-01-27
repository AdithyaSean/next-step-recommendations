package com.nextstep.recommendations.src;

import java.util.List;
import java.util.Map;

class Test {
    public static void main(String[] args) throws Exception {
        Generator generator = new Generator();
        Map<String, List<Map<String, Object>>> data = generator.generate();
        generator.saveToARFF(data);

        Preprocessor preprocessor = new Preprocessor();
        preprocessor.preprocess();

        Trainer trainer = new Trainer();
        trainer.train();

        TestPredictor testPredictor = new TestPredictor();

        Map<String, Integer> olResults = Map.of(
                "0", 1,
                "1", 1,
                "2", 2,
                "3", 1,
                "4", 3,
                "5", 1
        );

        System.out.println("\nOL Student Profile:");
        System.out.println("==================");
        System.out.println("\nPredicted Career Probabilities:");
        System.out.println(testPredictor.predict(0, olResults, null, null, null));

        Map<String, Integer> alResults = Map.of(
                "0", 1,
                "1", 2,
                "2", 1
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