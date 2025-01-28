package com.nextstep.recommendations.src;

import java.util.List;
import java.util.Map;

class Test {
    public static void main(String[] args) throws Exception {
        Generator generator = new Generator();
        List<Map<String, Object>> datasets = generator.generateDatasets();

        Trainer trainer = new Trainer();
        trainer.train(datasets);

        // Common OL results for all students
        Map<Integer, Integer> olResults = Map.of(
                Config.OL_SUBJECTS.get("Maths"), Config.GRADES.get("A"),
                Config.OL_SUBJECTS.get("Science"), Config.GRADES.get("A"),
                Config.OL_SUBJECTS.get("English"), Config.GRADES.get("B"),
                Config.OL_SUBJECTS.get("Sinhala"), Config.GRADES.get("C"),
                Config.OL_SUBJECTS.get("History"), Config.GRADES.get("B"),
                Config.OL_SUBJECTS.get("Religion"), Config.GRADES.get("A")
        );

        printOLPredictions(olResults);
        printALPredictions(olResults);
        printUniversityPredictions(olResults);
    }

    private static void printOLPredictions(Map<Integer, Integer> olResults) {
        CareerPrediction olPrediction = new CareerPrediction(olResults);
        System.out.println("\nOL Student Profile:");
        System.out.println("===================");
        olPrediction.printPredictions(olPrediction.predictOL());
    }

    private static void printALPredictions(Map<Integer, Integer> olResults) {
        // Physical Science Stream
        Map<Integer, Integer> physicalScienceResults = Map.of(
                Config.AL_SUBJECTS.get("Physics"), Config.GRADES.get("A"),
                Config.AL_SUBJECTS.get("Chemistry"), Config.GRADES.get("B"),
                Config.AL_SUBJECTS.get("Combined_Maths"), Config.GRADES.get("A")
        );
        printALProfile("Physical Science", physicalScienceResults, 2.9, olResults);

        // Physical Science with ICT Stream
        Map<Integer, Integer> physicalScienceICTResults = Map.of(
                Config.AL_SUBJECTS.get("Physics"), Config.GRADES.get("B"),
                Config.AL_SUBJECTS.get("ICT"), Config.GRADES.get("A"),
                Config.AL_SUBJECTS.get("Combined_Maths"), Config.GRADES.get("B")
        );
        printALProfile("Physical Science with ICT", physicalScienceICTResults, 2.6, olResults);

        // Biological Science Stream
        Map<Integer, Integer> bioScienceResults = Map.of(
                Config.AL_SUBJECTS.get("Biology"), Config.GRADES.get("A"),
                Config.AL_SUBJECTS.get("Chemistry"), Config.GRADES.get("A"),
                Config.AL_SUBJECTS.get("Physics"), Config.GRADES.get("B")
        );
        printALProfile("Biological Science", bioScienceResults, 2.7, olResults);

        // Bio Science with Agriculture Stream
        Map<Integer, Integer> agricultureResults = Map.of(
                Config.AL_SUBJECTS.get("Biology"), Config.GRADES.get("B"),
                Config.AL_SUBJECTS.get("Chemistry"), Config.GRADES.get("B"),
                Config.AL_SUBJECTS.get("Agriculture"), Config.GRADES.get("A")
        );
        printALProfile("Bio Science with Agriculture", agricultureResults, 2.4, olResults);
    }

    private static void printUniversityPredictions(Map<Integer, Integer> olResults) {
        // University predictions with GPA
        printUniProfile("Physical Science", 2.9, 3.8, olResults);
        printUniProfile("Physical Science with ICT", 2.6, 3.6, olResults);
        printUniProfile("Biological Science", 2.7, 3.7, olResults);
        printUniProfile("Bio Science with Agriculture", 2.4, 3.4, olResults);
    }

    private static void printALProfile(String streamName, Map<Integer, Integer> alResults,
                                       double zScore, Map<Integer, Integer> olResults) {
        CareerPrediction prediction = new CareerPrediction(olResults, alResults, zScore);
        System.out.printf("\nAL %s Student Profile (Z-score: %.1f):", streamName, zScore);
        System.out.println("\n==========================================");
        prediction.printPredictions(prediction.predictAL());
    }

    private static void printUniProfile(String streamName, double zScore, double gpa,
                                        Map<Integer, Integer> olResults) {
        // Create dummy AL results based on stream
        Map<Integer, Integer> alResults = Map.of(); // Should be implemented based on actual stream
        CareerPrediction prediction = new CareerPrediction(olResults, alResults, zScore, gpa);
        System.out.printf("\nUniversity %s Graduate Profile (GPA: %.2f):", streamName, gpa);
        System.out.println("\n==============================================");
        prediction.printPredictions(prediction.predictUNI());
    }
}