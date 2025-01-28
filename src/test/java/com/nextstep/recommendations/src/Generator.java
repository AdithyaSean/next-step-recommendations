package com.nextstep.recommendations.src;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Generator {
    private static final Random random = new Random();
    private static final String CSV_HEADER_OL = "Maths,Science,English,Sinhala,History,Religion,EngineeringProb,MedicineProb,ITProb,BusinessProb,TeachingProb,ResearchProb\n";
    private static final String CSV_HEADER_AL = "OLMaths,OLScience,OLEnglish,ZScore,Physics,Chemistry,Maths,Biology,ICT,Agriculture,EngineeringProb,MedicineProb,ITProb,BusinessProb,TeachingProb,ResearchProb\n";
    private static final String CSV_HEADER_UNI = "GPA,ZScore,Stream,EngineeringProb,MedicineProb,ITProb,BusinessProb,TeachingProb,ResearchProb\n";

    public void generateOLDataset() throws IOException {
        Path rawPath = Paths.get(Config.MODEL_DIR, "ol_raw.csv");
        Path processedPath = Paths.get(Config.MODEL_DIR, "ol_processed.csv");
        List<String> lines = new ArrayList<>();
        lines.add(CSV_HEADER_OL);

        for (int i = 0; i < Config.NUM_STUDENTS; i++) {
            Map<Integer, Integer> olGrades = generateOLGrades();
            Map<String, Double> probabilities = calculateOLProbabilities(olGrades);

            String line = Config.OL_SUBJECTS.values().stream()
                    .map(subjId -> Config.GRADES.entrySet().stream()
                            .filter(e -> e.getValue().equals(olGrades.get(subjId)))
                            .findFirst().get().getKey())
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

    public void generateALDataset() throws IOException {
        Path rawPath = Paths.get(Config.MODEL_DIR, "al_raw.csv");
        Path processedPath = Paths.get(Config.MODEL_DIR, "al_processed.csv");
        List<String> lines = new ArrayList<>();
        lines.add(CSV_HEADER_AL);

        for (int i = 0; i < Config.NUM_STUDENTS; i++) {
            Map<Integer, Integer> olGrades = generateOLGrades();
            int streamId = selectStream();
            Map<Integer, Integer> alGrades = generateALGrades(streamId);
            double zScore = generateZScore(streamId);

            String line = olGrades.values().stream()
                    .map(grade -> Config.GRADES.entrySet().stream()
                            .filter(e -> e.getValue().equals(grade))
                            .findFirst().get().getKey())
                    .collect(Collectors.joining(","));

            line += String.format(",%.2f,", zScore);

            line += Config.AL_SUBJECTS.values().stream()
                    .map(subjId -> alGrades.containsKey(subjId) ?
                            Config.GRADES.entrySet().stream()
                                    .filter(e -> e.getValue().equals(alGrades.get(subjId)))
                                    .findFirst().get().getKey() : "NA")
                    .collect(Collectors.joining(","));

            Map<String, Double> probs = calculateALProbabilities(olGrades, alGrades, zScore);
            line += String.format(",%.4f,%.4f,%.4f,%.4f,%.4f,%.4f",
                    probs.get("Engineering"), probs.get("Medicine"),
                    probs.get("IT"), probs.get("Business"),
                    probs.get("Teaching"), probs.get("Research"));

            lines.add(line);
        }

        Files.write(rawPath, lines);
        preprocessDataset(rawPath, processedPath);
    }

    public void generateUNIDataset() throws IOException {
        Path rawPath = Paths.get(Config.MODEL_DIR, "uni_raw.csv");
        Path processedPath = Paths.get(Config.MODEL_DIR, "uni_processed.csv");
        List<String> lines = new ArrayList<>();
        lines.add(CSV_HEADER_UNI);

        for (int i = 0; i < Config.NUM_STUDENTS; i++) {
            int streamId = selectStream();
            double zScore = generateZScore(streamId);
            double gpa = generateGPA(zScore);

            String line = String.format("%.2f,%.2f,%d,", gpa, zScore, streamId);

            Map<String, Double> probs = calculateUNIProbabilities(streamId, zScore, gpa);
            line += String.format("%.4f,%.4f,%.4f,%.4f,%.4f,%.4f",
                    probs.get("Engineering"), probs.get("Medicine"),
                    probs.get("IT"), probs.get("Business"),
                    probs.get("Teaching"), probs.get("Research"));

            lines.add(line);
        }

        Files.write(rawPath, lines);
        preprocessDataset(rawPath, processedPath);
    }

    private Map<Integer, Integer> generateOLGrades() {
        Map<Integer, Integer> grades = new HashMap<>();
        for (Map.Entry<String, Integer> entry : Config.OL_SUBJECTS.entrySet()) {
            String subject = entry.getKey();
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

    private Map<String, Double> calculateOLProbabilities(Map<Integer, Integer> olGrades) {
        Map<String, Double> probs = new HashMap<>();
        for (String career : Config.CAREERS.keySet()) {
            double base = Config.CAREER_COMPATIBILITY.get(Config.CAREERS.get(career));
            double bonus = calculateOLCareerBonus(career, olGrades);
            probs.put(career, base + bonus);
        }
        return normalizeProbs(probs);
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

    private Map<String, Double> calculateALProbabilities(Map<Integer, Integer> olGrades, Map<Integer, Integer> alGrades, double zScore) {
        Map<String, Double> probs = new HashMap<>();
        for (String career : Config.CAREERS.keySet()) {
            double base = Config.CAREER_COMPATIBILITY.get(Config.CAREERS.get(career));
            double olBonus = calculateOLCareerBonus(career, olGrades);
            double alBonus = calculateALCareerBonus(career, alGrades);
            double zScoreBonus = calculateZScoreBonus(career, zScore);
            probs.put(career, base + olBonus + alBonus + zScoreBonus);
        }
        return normalizeProbs(probs);
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

    private Map<String, Double> calculateUNIProbabilities(int streamId, double zScore, double gpa) {
        Map<String, Double> probs = new HashMap<>();
        for (String career : Config.CAREERS.keySet()) {
            double base = Config.CAREER_COMPATIBILITY.get(Config.CAREERS.get(career));
            double zScoreBonus = calculateZScoreBonus(career, zScore);
            double gpaBonus = calculateGPABonus(career, gpa);
            double streamBonus = calculateStreamBonus(career, streamId);
            probs.put(career, base + zScoreBonus + gpaBonus + streamBonus);
        }
        return normalizeProbs(probs);
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

    private Map<String, Double> normalizeProbs(Map<String, Double> probs) {
        double total = probs.values().stream().mapToDouble(Double::doubleValue).sum();
        return probs.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue() / total
                ));
    }

    private void preprocessDataset(Path inputPath, Path outputPath) throws IOException {
        List<String> lines = Files.readAllLines(inputPath);
        List<String> processedLines = new ArrayList<>();
        processedLines.add(lines.get(0)); // Keep header

        for (int i = 1; i < lines.size(); i++) {
            String[] parts = lines.get(i).split(",");
            List<String> processedParts = new ArrayList<>();

            for (String part : parts) {
                if (part.matches("[A-F]")) {
                    // Encode grades as numerical values
                    processedParts.add(String.valueOf(Config.GRADES.get(part)));
                } else if (part.equals("NA")) {
                    // Handle missing values
                    processedParts.add("0");
                } else {
                    // Keep numerical values as-is
                    processedParts.add(part);
                }
            }

            processedLines.add(String.join(",", processedParts));
        }

        Files.write(outputPath, processedLines);
    }
}