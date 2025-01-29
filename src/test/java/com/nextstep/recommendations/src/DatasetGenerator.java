package com.nextstep.recommendations.src;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class DatasetGenerator {
    private static final Random random = new Random();

    // Updated CSV headers with OL subjects in all datasets
    private static final String CSV_HEADER_OL = "OLMaths,OLScience,OLEnglish,OLSinhala,OLHistory,OLReligion,EngineeringProb,MedicineProb,ITProb,BusinessProb,TeachingProb,ResearchProb\n";
    private static final String CSV_HEADER_AL = "OLMaths,OLScience,OLEnglish,OLSinhala,OLHistory,OLReligion,ZScore,ALPhysics,ALChemistry,ALCombined_Maths,ALBiology,ALICT,ALAgriculture,ALStream,EngineeringProb,MedicineProb,ITProb,BusinessProb,TeachingProb,ResearchProb\n";
    private static final String CSV_HEADER_UNI = "OLMaths,OLScience,OLEnglish,OLSinhala,OLHistory,OLReligion,ZScore,ALPhysics,ALChemistry,ALCombined_Maths,ALBiology,ALICT,ALAgriculture,ALStream,GPA,EngineeringProb,MedicineProb,ITProb,BusinessProb,TeachingProb,ResearchProb\n";

    public void generateAllDatasets() throws IOException {
        generateOLDataset();
        generateALDatasets();
        generateUNIDatasets();
    }

    public void generateOLDataset() throws IOException {
        Path rawPath = Paths.get(Config.MODEL_DIR, "ol_raw.csv");
        Path processedPath = Paths.get(Config.MODEL_DIR, "ol_processed.csv");
        List<String> lines = new ArrayList<>();
        lines.add(CSV_HEADER_OL);

        Files.createDirectories(Paths.get(Config.MODEL_DIR));

        for (int i = 0; i < Config.NUM_STUDENTS; i++) {
            Map<Integer, Integer> olGrades = generateOLGrades();
            Map<String, Double> probabilities = calculateOLProbabilities(olGrades);

            String line = Config.OL_SUBJECTS.values().stream()
                    .map(subjId -> Config.GRADES.entrySet().stream()
                            .filter(e -> e.getValue().equals(olGrades.get(subjId)))
                            .findFirst().map(Map.Entry::getKey).orElse("NA"))
                    .collect(Collectors.joining(","));

            line += String.format(",%.4f,%.4f,%.4f,%.4f,%.4f,%.4f",
                    probabilities.get("Engineering"), probabilities.get("Medicine"),
                    probabilities.get("IT"), probabilities.get("Business"),
                    probabilities.get("Teaching"), probabilities.get("Research"));

            lines.add(line);
        }

        Files.write(rawPath, lines);
        preprocessDataset(rawPath, processedPath);
    }

    public void generateALDatasets() throws IOException {
        // Generate AL datasets for each stream
        for (int streamId : Config.AL_STREAMS.values()) {
            generateALDatasetForStream(streamId);
        }
    }

    public void generateUNIDatasets() throws IOException {
        // Generate University datasets for each stream
        for (int streamId : Config.AL_STREAMS.values()) {
            generateUNIDatasetForStream(streamId);
        }
    }

    private void generateALDatasetForStream(int streamId) throws IOException {
        String streamName = Config.STREAM_NAMES.get(streamId);
        Path rawPath = Paths.get(Config.MODEL_DIR, "al_" + streamName + "_raw.csv");
        Path processedPath = Paths.get(Config.MODEL_DIR, "al_" + streamName + "_processed.csv");

        List<String> lines = new ArrayList<>();
        lines.add(CSV_HEADER_AL);

        int studentsPerStream = Config.NUM_STUDENTS / Config.AL_STREAMS.size();

        for (int i = 0; i < studentsPerStream; i++) {
            Map<Integer, Integer> olGrades = generateOLGrades();
            Map<Integer, Integer> alGrades = generateALGrades(streamId);
            double zScore = generateZScore(streamId);

            // Build OL grades part
            String line = Config.OL_SUBJECTS.values().stream()
                    .map(subjId -> Config.GRADES.entrySet().stream()
                            .filter(e -> e.getValue().equals(olGrades.get(subjId)))
                            .findFirst().map(Map.Entry::getKey).orElse("NA"))
                    .collect(Collectors.joining(","));

            // Add Z-Score
            line += String.format(",%.2f,", zScore);

            // Add AL subjects
            line += Config.AL_SUBJECTS.values().stream()
                    .map(subjId -> alGrades.containsKey(subjId) ?
                            Config.GRADES.entrySet().stream()
                                    .filter(e -> e.getValue().equals(alGrades.get(subjId)))
                                    .findFirst().map(Map.Entry::getKey).orElse("NA") : "NA")
                    .collect(Collectors.joining(","));

            // Add stream and probabilities
            Map<String, Double> probabilities = calculateALProbabilities(olGrades, alGrades, zScore);
            line += String.format(",%d,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f",
                    streamId,
                    probabilities.get("Engineering"), probabilities.get("Medicine"),
                    probabilities.get("IT"), probabilities.get("Business"),
                    probabilities.get("Teaching"), probabilities.get("Research"));

            lines.add(line);
        }

        Files.write(rawPath, lines);
        preprocessDataset(rawPath, processedPath);
    }

    private void generateUNIDatasetForStream(int streamId) throws IOException {
        String streamName = Config.STREAM_NAMES.get(streamId);
        Path rawPath = Paths.get(Config.MODEL_DIR, "uni_" + streamName + "_raw.csv");
        Path processedPath = Paths.get(Config.MODEL_DIR, "uni_" + streamName + "_processed.csv");

        List<String> lines = new ArrayList<>();
        lines.add(CSV_HEADER_UNI);

        int studentsPerStream = Config.NUM_STUDENTS / Config.AL_STREAMS.size();

        for (int i = 0; i < studentsPerStream; i++) {
            Map<Integer, Integer> olGrades = generateOLGrades();
            Map<Integer, Integer> alGrades = generateALGrades(streamId);
            double zScore = generateZScore(streamId);
            double gpa = generateGPA(zScore);

            // Build OL grades part
            String line = Config.OL_SUBJECTS.values().stream()
                    .map(subjId -> Config.GRADES.entrySet().stream()
                            .filter(e -> e.getValue().equals(olGrades.get(subjId)))
                            .findFirst().map(Map.Entry::getKey).orElse("NA"))
                    .collect(Collectors.joining(","));

            // Add Z-Score
            line += String.format(",%.2f,", zScore);

            // Add AL subjects
            line += Config.AL_SUBJECTS.values().stream()
                    .map(subjId -> alGrades.containsKey(subjId) ?
                            Config.GRADES.entrySet().stream()
                                    .filter(e -> e.getValue().equals(alGrades.get(subjId)))
                                    .findFirst().map(Map.Entry::getKey).orElse("NA") : "NA")
                    .collect(Collectors.joining(","));

            // Add stream, GPA and probabilities
            Map<String, Double> probabilities = calculateUNIProbabilities(olGrades, alGrades, streamId, zScore, gpa);
            line += String.format(",%d,%.2f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f",
                    streamId, gpa,
                    probabilities.get("Engineering"), probabilities.get("Medicine"),
                    probabilities.get("IT"), probabilities.get("Business"),
                    probabilities.get("Teaching"), probabilities.get("Research"));

            lines.add(line);
        }

        Files.write(rawPath, lines);
        preprocessDataset(rawPath, processedPath);
    }

    private Map<Integer, Integer> generateOLGrades() {
        Map<Integer, Integer> grades = new HashMap<>();
        for (Map.Entry<String, Integer> entry : Config.OL_SUBJECTS.entrySet()) {
            int subjId = entry.getValue();
            double difficulty = Config.SUBJECT_DIFFICULTY.get(subjId);
            grades.put(subjId, generateGrade(difficulty));
        }
        return grades;
    }

    private int generateGrade(double difficulty) {
        double rand = random.nextDouble();
        double cumulative = 0.0;
        for (Map.Entry<String, Double> entry : getGradeDistribution(difficulty).entrySet()) {
            cumulative += entry.getValue();
            if (rand <= cumulative) {
                return Config.GRADES.get(entry.getKey());
            }
        }
        return Config.GRADES.get("F");
    }

    private Map<String, Double> getGradeDistribution(double difficulty) {
        // Adjust base grade distribution based on subject difficulty
        Map<String, Double> adjusted = new LinkedHashMap<>();
        adjusted.put("A", 0.25 * (1 - difficulty));
        adjusted.put("B", 0.35 * (1 - difficulty));
        adjusted.put("C", 0.25 * (1 + difficulty));
        adjusted.put("S", 0.1 * (1 + difficulty));
        adjusted.put("F", 0.05 * (1 + difficulty));
        return normalizeDistribution(adjusted);
    }

    private Map<String, Double> normalizeDistribution(Map<String, Double> dist) {
        double total = dist.values().stream().mapToDouble(Double::doubleValue).sum();
        return dist.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue() / total
                ));
    }

    private int selectStream() {
        double rand = random.nextDouble();
        if (rand < 0.4) return Config.AL_STREAMS.get("Physical Science");
        if (rand < 0.7) return Config.AL_STREAMS.get("Biological Science");
        if (rand < 0.85) return Config.AL_STREAMS.get("Physical Science with ICT");
        return Config.AL_STREAMS.get("Bio Science with Agriculture");
    }

    private Map<Integer, Integer> generateALGrades(int streamId) {
        Map<Integer, Integer> grades = new HashMap<>();
        List<Integer> subjects = Config.AL_SUBJECTS_BY_STREAM.get(streamId);
        for (int subjId : subjects) {
            Map<String, Double> dist = Config.GRADE_DISTRIBUTIONS.get(subjId);
            double rand = random.nextDouble();
            double cumulative = 0.0;
            for (Map.Entry<String, Double> entry : dist.entrySet()) {
                cumulative += entry.getValue();
                if (rand <= cumulative) {
                    grades.put(subjId, Config.GRADES.get(entry.getKey()));
                    break;
                }
            }
        }
        return grades;
    }

    private double generateZScore(int streamId) {
        double[] range = Config.Z_SCORE_RANGE.get(streamId);
        return range[0] + (range[1] - range[0]) * random.nextDouble();
    }

    private double generateGPA(double zScore) {
        int tier = zScore > 3.0 ? 0 : zScore > 2.5 ? 1 : 2;
        double[] range = Config.GPA_RANGE.get(tier);
        return range[0] + (range[1] - range[0]) * random.nextDouble();
    }

    private double calculateOLCareerBonus(String career, Map<Integer, Integer> olGrades) {
        double bonus = 0.0;
        Map<Integer, Double> careerBonus = Config.CAREER_COMPATIBILITY_BONUS.get(Config.CAREERS.get(career));
        for (Map.Entry<Integer, Double> entry : careerBonus.entrySet()) {
            if (olGrades.containsKey(entry.getKey())) {
                int grade = olGrades.get(entry.getKey());
                double gradeWeight = 1.0 - (grade * 0.2); // Higher grades contribute more
                bonus += entry.getValue() * gradeWeight;
            }
        }
        return bonus;
    }

    private double calculateALCareerBonus(String career, Map<Integer, Integer> alGrades) {
        double bonus = 0.0;
        Map<Integer, Double> careerBonus = Config.CAREER_COMPATIBILITY_BONUS.get(Config.CAREERS.get(career));
        for (Map.Entry<Integer, Integer> entry : alGrades.entrySet()) {
            if (careerBonus.containsKey(entry.getKey())) {
                int grade = entry.getValue();
                double gradeWeight = 1.0 - (grade * 0.2); // Higher grades contribute more
                bonus += careerBonus.get(entry.getKey()) * gradeWeight;
            }
        }
        return bonus;
    }

    private double calculateZScoreBonus(String career, double zScore) {
        Map<Integer, Double> careerBonus = Config.CAREER_COMPATIBILITY_BONUS.get(Config.CAREERS.get(career));
        double zScoreFactor = careerBonus.getOrDefault(Config.Z_SCORE_FACTOR, 0.0);
        return zScoreFactor * zScore;
    }

    private double calculateGPABonus(String career, double gpa) {
        Map<Integer, Double> careerBonus = Config.CAREER_COMPATIBILITY_BONUS.get(Config.CAREERS.get(career));
        double gpaFactor = careerBonus.getOrDefault(Config.GPA_FACTOR, 0.0);
        return gpaFactor * gpa;
    }

    private double calculateStreamBonus(String career, int streamId) {
        Map<Integer, Double> careerBonus = Config.CAREER_COMPATIBILITY_BONUS.get(Config.CAREERS.get(career));
        return careerBonus.getOrDefault(streamId, 0.0);
    }

    private Map<String, Double> normalizeProbabilities(Map<String, Double> probabilities) {
        double total = probabilities.values().stream().mapToDouble(Double::doubleValue).sum();
        return probabilities.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue() / total
                ));
    }

    private Map<String, Double> calculateOLProbabilities(Map<Integer, Integer> olGrades) {
        Map<String, Double> probabilities = new HashMap<>();
        for (String career : Config.CAREERS.keySet()) {
            double base = Config.CAREER_COMPATIBILITY.get(Config.CAREERS.get(career));
            double bonus = calculateOLCareerBonus(career, olGrades);
            probabilities.put(career, base + bonus);
        }
        return normalizeProbabilities(probabilities);
    }

    private Map<String, Double> calculateALProbabilities(Map<Integer, Integer> olGrades, Map<Integer, Integer> alGrades, double zScore) {
        Map<String, Double> probabilities = new HashMap<>();
        for (String career : Config.CAREERS.keySet()) {
            double base = Config.CAREER_COMPATIBILITY.get(Config.CAREERS.get(career));
            double olBonus = calculateOLCareerBonus(career, olGrades);
            double alBonus = calculateALCareerBonus(career, alGrades);
            double zScoreBonus = calculateZScoreBonus(career, zScore);
            probabilities.put(career, base + olBonus + alBonus + zScoreBonus);
        }
        return normalizeProbabilities(probabilities);
    }

    private Map<String, Double> calculateUNIProbabilities(Map<Integer, Integer> olGrades,
                                                          Map<Integer, Integer> alGrades,
                                                          int streamId,
                                                          double zScore,
                                                          double gpa) {
        Map<String, Double> probabilities = new HashMap<>();
        for (String career : Config.CAREERS.keySet()) {
            double base = Config.CAREER_COMPATIBILITY.get(Config.CAREERS.get(career));
            double olBonus = calculateOLCareerBonus(career, olGrades);
            double alBonus = calculateALCareerBonus(career, alGrades);
            double zScoreBonus = calculateZScoreBonus(career, zScore);
            double gpaBonus = calculateGPABonus(career, gpa);
            double streamBonus = calculateStreamBonus(career, streamId);

            probabilities.put(career, base + olBonus + alBonus + zScoreBonus + gpaBonus + streamBonus);
        }
        return normalizeProbabilities(probabilities);
    }

    private void preprocessDataset(Path inputPath, Path outputPath) throws IOException {
        List<String> lines = Files.readAllLines(inputPath);
        List<String> processedLines = new ArrayList<>();

        if (!lines.isEmpty()) {
            processedLines.add(lines.get(0)); // Keep header
        }

        for (int i = 1; i < lines.size(); i++) {
            String[] parts = lines.get(i).split(",");
            List<String> processedParts = new ArrayList<>();

            for (String part : parts) {
                if (part.matches("[A-F]")) {
                    processedParts.add(String.valueOf(Config.GRADES.get(part)));
                } else if (part.equals("NA")) {
                    processedParts.add("0");
                } else {
                    processedParts.add(part);
                }
            }

            processedLines.add(String.join(",", processedParts));
        }

        Files.write(outputPath, processedLines);
    }

    private static void printOLPredictions(Map<Integer, Integer> olResults) {
        CareerCompatibilityProbabilityPredictor olPrediction = new CareerCompatibilityProbabilityPredictor(olResults);
        System.out.println("\nOL Student Profile:");
        System.out.println("===================");
        olPrediction.printPredictions(olPrediction.predictOL());
    }

    private static void printALPredictions(Map<Integer, Integer> olResults, Map<Integer, Integer> alResults) {
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

    private static void printUniversityPredictions(Map<Integer, Integer> olResults, Map<Integer, Integer> alResults) {
        // University predictions with GPA
        printUniProfile("Physical Science",olResults, alResults, 2.9, 3.9);
        printUniProfile("Physical Science with ICT", olResults, alResults, 2.6, 3.7);
        printUniProfile("Biological Science", olResults, alResults, 2.7, 3.8);
        printUniProfile("Bio Science with Agriculture", olResults, alResults, 2.4, 3.6);
    }

    private static void printALProfile(String streamName, Map<Integer, Integer> alResults,
                                       double zScore, Map<Integer, Integer> olResults) {
        CareerCompatibilityProbabilityPredictor prediction = new CareerCompatibilityProbabilityPredictor(olResults, alResults, zScore);
        System.out.printf("\nAL %s Student Profile (Z-score: %.1f):", streamName, zScore);
        System.out.println("\n==========================================");
        prediction.printPredictions(prediction.predictAL());
    }

    private static void printUniProfile(String streamName,
                                        Map<Integer, Integer> olResults,
                                        Map<Integer, Integer> alResults,
                                        double zScore,
                                        double gpa) {
        CareerCompatibilityProbabilityPredictor prediction =
                new CareerCompatibilityProbabilityPredictor(olResults, alResults, zScore, gpa);
        System.out.printf("\nUniversity %s Graduate Profile (GPA: %.2f):", streamName, gpa);
        System.out.println("\n==============================================");
        prediction.printPredictions(prediction.predictUNI());
    }

    public static void main(String[] args) throws Exception {
        DatasetGenerator generator = new DatasetGenerator();

        // Generate datasets
        generator.generateAllDatasets();

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

        // AL results for all students
        Map<Integer, Integer> alResults = Map.of(
                Config.AL_SUBJECTS.get("Physics"), Config.GRADES.get("A"),
                Config.AL_SUBJECTS.get("Chemistry"), Config.GRADES.get("A"),
                Config.AL_SUBJECTS.get("Combined_Maths"), Config.GRADES.get("A"),
                Config.AL_SUBJECTS.get("Biology"), Config.GRADES.get("A"),
                Config.AL_SUBJECTS.get("ICT"), Config.GRADES.get("A"),
                Config.AL_SUBJECTS.get("Agriculture"), Config.GRADES.get("A")
        );
        printALPredictions(olResults, alResults);
        printUniversityPredictions(olResults, alResults);
    }
}

class CareerCompatibilityProbabilityPredictor {
    private final Map<Integer, Double> careerPrediction;
    private final Map<Integer, Integer> olResults;
    private Map<Integer, Integer> alResults;
    private double zScore;
    private double gpa;

    public CareerCompatibilityProbabilityPredictor(Map<Integer, Integer> olResults, Map<Integer, Integer> alResults, double zScore, double gpa) {
        this.olResults = olResults;
        this.alResults = alResults;
        this.zScore = zScore;
        this.gpa = gpa;
        this.careerPrediction = new HashMap<>();
    }

    public CareerCompatibilityProbabilityPredictor(Map<Integer, Integer> olResults, Map<Integer, Integer> alResults, double zScore) {
        this.olResults = olResults;
        this.alResults = alResults;
        this.zScore = zScore;
        this.careerPrediction = new HashMap<>();
    }

    public CareerCompatibilityProbabilityPredictor(Map<Integer, Integer> olResults) {
        this.olResults = olResults;
        this.careerPrediction = new HashMap<>();
    }

    public Map<Integer, Double> predictOL() {
        for (Integer careerId : Config.CAREERS.values()) {
            careerPrediction.put(careerId, Math.random() * 100);
        }
        return careerPrediction;
    }

    public Map<Integer, Double> predictAL() {
        for (Integer careerId : Config.CAREERS.values()) {
            careerPrediction.put(careerId, Math.random() * 100);
        }
        return careerPrediction;
    }

    public Map<Integer, Double> predictUNI() {
        for (Integer careerId : Config.CAREERS.values()) {
            careerPrediction.put(careerId, Math.random() * 100);
        }
        return careerPrediction;
    }

    public void printPredictions(Map<Integer, Double> predictions) {
        System.out.println("Input Features:");
        if (olResults != null) {
            System.out.println("OL Results:");
            for (Map.Entry<Integer, Integer> entry : olResults.entrySet()) {
                System.out.println(getSubjectName(entry.getKey()) + ": " + getGradeName(entry.getValue()));
            }
        }
        if (alResults != null) {
            System.out.println("AL Results:");
            for (Map.Entry<Integer, Integer> entry : alResults.entrySet()) {
                System.out.println(getSubjectName(entry.getKey()) + ": " + getGradeName(entry.getValue()));
            }
        }
        if (zScore != 0) {
            System.out.println("Z-Score: " + zScore);
        }
        if (gpa != 0) {
            System.out.println("GPA: " + gpa);
        }

        System.out.println("\nPredicted Career Probabilities:");
        for (Map.Entry<Integer, Double> entry : predictions.entrySet()) {
            String careerName = getCareerName(entry.getKey());
            System.out.println(careerName + ": " + entry.getValue() + "%");
        }
    }

    private String getSubjectName(int subjectId) {
        for (Map.Entry<String, Integer> entry : Config.OL_SUBJECTS.entrySet()) {
            if (entry.getValue() == subjectId) {
                return entry.getKey();
            }
        }
        for (Map.Entry<String, Integer> entry : Config.AL_SUBJECTS.entrySet()) {
            if (entry.getValue() == subjectId) {
                return entry.getKey();
            }
        }
        return "Unknown Subject";
    }

    private String getGradeName(int gradeId) {
        for (Map.Entry<String, Integer> entry : Config.GRADES.entrySet()) {
            if (entry.getValue() == gradeId) {
                return entry.getKey();
            }
        }
        return "Unknown Grade";
    }

    private String getCareerName(int careerId) {
        for (Map.Entry<String, Integer> entry : Config.CAREERS.entrySet()) {
            if (entry.getValue() == careerId) {
                return entry.getKey();
            }
        }
        return "Unknown Career";
    }
}