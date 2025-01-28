package com.nextstep.recommendations.src;

import java.util.List;
import java.util.Map;

class Test {
    public static void main(String[] args) throws Exception {
        Generator generator = new Generator();
        List<Map<String, Object>> datasets = generator.generateDatasets();

        Trainer trainer = new Trainer();
        trainer.train(datasets);

        // OL Student Profile
        Map<Integer, Integer> olResults = Map.of(
                Config.OL_SUBJECTS.get("Maths"), Config.GRADES.get("A"),
                Config.OL_SUBJECTS.get("Science"), Config.GRADES.get("A"),
                Config.OL_SUBJECTS.get("English"), Config.GRADES.get("B"),
                Config.OL_SUBJECTS.get("Sinhala"), Config.GRADES.get("C"),
                Config.OL_SUBJECTS.get("History"), Config.GRADES.get("B"),
                Config.OL_SUBJECTS.get("Religion"), Config.GRADES.get("A")
        );

        CareerPrediction olPrediction = new CareerPrediction(olResults);
        System.out.println("\nOL Student Profile:");
        System.out.println("=========================");
        System.out.println("\nPredicted Career Probabilities:");
        olPrediction.printPredictions(olPrediction.predictOL());

        // AL Physical Science - Chemistry Student Profile
        Map<Integer, Integer> alResultsChemistry = Map.of(
                Config.AL_SUBJECTS.get("Physics"), Config.GRADES.get("A"),
                Config.AL_SUBJECTS.get("Chemistry"), Config.GRADES.get("B"),
                Config.AL_SUBJECTS.get("Combined_Maths"), Config.GRADES.get("A")
        );

        CareerPrediction alPredictionChemistry = new CareerPrediction(olResults, alResultsChemistry, 2.8);
        System.out.println("\nAL Physical Science - Chemistry Student Profile:");
        System.out.println("=========================");
        System.out.println("\nPredicted Career Probabilities:");
        alPredictionChemistry.printPredictions(alPredictionChemistry.predictAL());

        // AL Physical Science - ICT Student Profile
        Map<Integer, Integer> alResultsICT = Map.of(
                Config.AL_SUBJECTS.get("Physics"), Config.GRADES.get("B"),
                Config.AL_SUBJECTS.get("ICT"), Config.GRADES.get("A"),
                Config.AL_SUBJECTS.get("Combined_Maths"), Config.GRADES.get("B")
        );

        CareerPrediction alPredictionICT = new CareerPrediction(olResults, alResultsICT, 2.4);
        System.out.println("\nAL Physical Science - ICT Student Profile:");
        System.out.println("=========================");
        System.out.println("\nPredicted Career Probabilities:");
        alPredictionICT.printPredictions(alPredictionICT.predictAL());

        // AL Biological Science - Physics Student Profile
        Map<Integer, Integer> alResultsBiology = Map.of(
                Config.AL_SUBJECTS.get("Biology"), Config.GRADES.get("A"),
                Config.AL_SUBJECTS.get("Chemistry"), Config.GRADES.get("A"),
                Config.AL_SUBJECTS.get("Physics"), Config.GRADES.get("B")
        );

        CareerPrediction alPredictionBiology = new CareerPrediction(olResults, alResultsBiology, 2.6);
        System.out.println("\nAL Biological Science - Physics Student Profile:");
        System.out.println("=========================");
        System.out.println("\nPredicted Career Probabilities:");
        alPredictionBiology.printPredictions(alPredictionBiology.predictAL());

        // AL Biological Science - Agriculture Student Profile
        Map<Integer, Integer> alResultsAgriculture = Map.of(
                Config.AL_SUBJECTS.get("Biology"), Config.GRADES.get("B"),
                Config.AL_SUBJECTS.get("Chemistry"), Config.GRADES.get("B"),
                Config.AL_SUBJECTS.get("Agriculture"), Config.GRADES.get("A")
        );

        CareerPrediction alPredictionAgriculture = new CareerPrediction(olResults, alResultsAgriculture, 2.3);
        System.out.println("\nAL Biological Science - Agriculture Student Profile:");
        System.out.println("=========================");
        System.out.println("\nPredicted Career Probabilities:");
        alPredictionAgriculture.printPredictions(alPredictionAgriculture.predictAL());

        // University - AL Physical Science - Chemistry Student Profile
        CareerPrediction uniPredictionChemistry = new CareerPrediction(olResults, alResultsChemistry, 2.8, 3.75);
        System.out.println("\nUniversity Student Profile (Physical Science - Chemistry):");
        System.out.println("=========================");
        System.out.println("\nPredicted Career Probabilities:");
        uniPredictionChemistry.printPredictions(uniPredictionChemistry.predictUNI());

        // University - AL Physical Science - ICT Student Profile
        CareerPrediction uniPredictionICT = new CareerPrediction(olResults, alResultsICT, 2.4, 3.5);
        System.out.println("\nUniversity Student Profile (Physical Science - ICT):");
        System.out.println("=========================");
        System.out.println("\nPredicted Career Probabilities:");
        uniPredictionICT.printPredictions(uniPredictionICT.predictUNI());

        // University - AL Biological Science - Physics Student Profile
        CareerPrediction uniPredictionBiology = new CareerPrediction(olResults, alResultsBiology, 2.6, 3.6);
        System.out.println("\nUniversity Student Profile (Biological Science - Physics):");
        System.out.println("=========================");
        System.out.println("\nPredicted Career Probabilities:");
        uniPredictionBiology.printPredictions(uniPredictionBiology.predictUNI());

        // University - AL Biological Science - Agriculture Student Profile
        CareerPrediction uniPredictionAgriculture = new CareerPrediction(olResults, alResultsAgriculture, 2.3, 3.3);
        System.out.println("\nUniversity Student Profile (Biological Science - Agriculture):");
        System.out.println("=========================");
        System.out.println("\nPredicted Career Probabilities:");
        uniPredictionAgriculture.printPredictions(uniPredictionAgriculture.predictUNI());
    }
}