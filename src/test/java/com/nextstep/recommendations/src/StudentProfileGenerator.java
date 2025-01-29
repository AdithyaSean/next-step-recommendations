package com.nextstep.recommendations.src;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class StudentProfileGenerator {
    private final StudentProfileRepository repository;
    private static final Random random = new Random();

    @Autowired
    public StudentProfileGenerator(StudentProfileRepository repository) {
        this.repository = repository;
    }

    public void generateStudentProfiles(int numberOfStudents) {
        for (int i = 0; i < numberOfStudents; i++) {
            StudentProfile profile = generateStudentProfile();
            repository.save(profile);
            repository.findById(profile.getId(i)).orElseThrow(() -> new RuntimeException("Profile not found"));
        }
    }

    public StudentProfile generateStudentProfile() {
        int educationLevel = getRandomEducationLevel();
        Map<String, Double> olGrades = generateOLGrades();

        Integer alStream = null;
        Map<String, Double> alGrades = null;
        Double zScore = null;
        Double gpa = null;

        if (educationLevel >= Config.EDUCATION_LEVELS.get("AL")) {
            alStream = getALStream();
            alGrades = generateALGrades(alStream);
            zScore = generateZScore(alStream);
        }

        if (educationLevel == Config.EDUCATION_LEVELS.get("UNI")) {
            gpa = generateGPA(zScore);
        }

        Map<String, Double> probabilities = calculateProbabilities(educationLevel, olGrades, alGrades, alStream, zScore, gpa);

        StudentProfile studentProfile = createStudentProfile(educationLevel, olGrades, alStream, alGrades, zScore, gpa);
        studentProfile.setCareerProbabilities(probabilities);

        return studentProfile;
    }

    private int getRandomEducationLevel() {
        double rand = random.nextDouble();
        double cumulative = 0.0;
        for (Map.Entry<Integer, Double> entry : Config.EDUCATION_LEVEL_DIST.entrySet()) {
            cumulative += entry.getValue();
            if (rand <= cumulative) {
                return entry.getKey();
            }
        }
        return Config.EDUCATION_LEVELS.get("OL");
    }

    private Map<String, Double> generateOLGrades() {
        Map<String, Double> grades = new HashMap<>();
        for (String subject : Config.OL_SUBJECTS.keySet()) {
            int subjId = Config.OL_SUBJECTS.get(subject);
            double difficulty = Config.SUBJECT_DIFFICULTY.get(subjId);
            int grade = generateGrade(difficulty);
            grades.put(subject, (double) grade);
        }
        return grades;
    }

    private Map<String, Double> generateALGrades(int streamId) {
        Map<String, Double> grades = new HashMap<>();
        List<Integer> subjectIds = Config.AL_SUBJECTS_BY_STREAM.get(streamId);
        for (int subjId : subjectIds) {
            String subjectName = Config.AL_SUBJECTS.entrySet().stream()
                    .filter(entry -> entry.getValue() == subjId)
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse(null);
            if (subjectName == null) continue;

            Map<String, Double> dist = Config.GRADE_DISTRIBUTIONS.get(subjId);
            String selectedGrade = selectGrade(dist);
            int gradeValue = Config.GRADES.get(selectedGrade);
            grades.put(subjectName, (double) gradeValue);
        }
        return grades;
    }

    private String selectGrade(Map<String, Double> dist) {
        double rand = random.nextDouble();
        double cumulative = 0.0;
        for (Map.Entry<String, Double> entry : dist.entrySet()) {
            cumulative += entry.getValue();
            if (rand <= cumulative) {
                return entry.getKey();
            }
        }
        return "F";
    }

    private int generateGrade(double difficulty) {
        double rand = random.nextDouble();
        Map<String, Double> distribution = getGradeDistribution(difficulty);
        double cumulative = 0.0;
        for (Map.Entry<String, Double> entry : distribution.entrySet()) {
            cumulative += entry.getValue();
            if (rand <= cumulative) {
                return Config.GRADES.get(entry.getKey());
            }
        }
        return Config.GRADES.get("F");
    }

    private Map<String, Double> getGradeDistribution(double difficulty) {
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
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue() / total));
    }

    private int getALStream() {
        double rand = random.nextDouble();
        if (rand < 0.4) return Config.AL_STREAMS.get("Physical Science");
        if (rand < 0.7) return Config.AL_STREAMS.get("Biological Science");
        if (rand < 0.85) return Config.AL_STREAMS.get("Physical Science with ICT");
        return Config.AL_STREAMS.get("Bio Science with Agriculture");
    }

    private double generateZScore(int streamId) {
        double[] range = Config.Z_SCORE_RANGE.get(streamId);
        return range[0] + (range[1] - range[0]) * random.nextDouble();
    }

    private double generateGPA(Double zScore) {
        if (zScore == null) return 0.0;
        int tier = zScore > 3.0 ? 0 : zScore > 2.5 ? 1 : 2;
        double[] range = Config.GPA_RANGE.get(tier);
        return range[0] + (range[1] - range[0]) * random.nextDouble();
    }

    private Map<String, Double> calculateProbabilities(int educationLevel, Map<String, Double> olGrades,
                                                       Map<String, Double> alGrades, Integer alStream,
                                                       Double zScore, Double gpa) {
        Map<Integer, Integer> olGradesById = convertOLGradesToIds(olGrades);
        Map<Integer, Integer> alGradesById = alGrades != null ? convertALGradesToIds(alGrades) : new HashMap<>();

        return switch (educationLevel) {
            case 0 -> calculateCareerCompatibilityProbabilitiesForOL(olGradesById);
            case 1 -> calculateCareerCompatibilityProbabilitiesForAL(olGradesById, alGradesById, alStream, zScore);
            case 2 -> calculateCareerCompatibilityProbabilitiesForUNI(olGradesById, alGradesById, alStream, zScore, gpa);
            default -> throw new IllegalArgumentException("Invalid education level");
        };
    }

    private Map<Integer, Integer> convertOLGradesToIds(Map<String, Double> olGrades) {
        Map<Integer, Integer> converted = new HashMap<>();
        for (Map.Entry<String, Double> entry : olGrades.entrySet()) {
            String subject = entry.getKey();
            int subjId = Config.OL_SUBJECTS.get(subject);
            converted.put(subjId, entry.getValue().intValue());
        }
        return converted;
    }

    private Map<Integer, Integer> convertALGradesToIds(Map<String, Double> alGrades) {
        Map<Integer, Integer> converted = new HashMap<>();
        if (alGrades == null) return converted;
        for (Map.Entry<String, Double> entry : alGrades.entrySet()) {
            String subject = entry.getKey();
            int subjId = Config.AL_SUBJECTS.get(subject);
            converted.put(subjId, entry.getValue().intValue());
        }
        return converted;
    }

    private Map<String, Double> calculateCareerCompatibilityProbabilitiesForOL(Map<Integer, Integer> olGrades) {
        Map<String, Double> probabilities = new HashMap<>();
        for (String career : Config.CAREERS.keySet()) {
            double base = Config.CAREER_COMPATIBILITY.get(Config.CAREERS.get(career));
            double bonus = calculateOLCareerBonus(career, olGrades);
            probabilities.put(career, base + bonus);
        }
        return normalizeProbabilities(probabilities);
    }

    private double calculateOLCareerBonus(String career, Map<Integer, Integer> olGrades) {
        double bonus = 0.0;
        Map<Integer, Double> careerBonus = Config.CAREER_COMPATIBILITY_BONUS.get(Config.CAREERS.get(career));
        for (Map.Entry<Integer, Double> entry : careerBonus.entrySet()) {
            if (entry.getKey() == Config.Z_SCORE_FACTOR || entry.getKey() == Config.GPA_FACTOR) continue;
            if (olGrades.containsKey(entry.getKey())) {
                int grade = olGrades.get(entry.getKey());
                double gradeWeight = 1.0 - (grade * 0.2);
                bonus += entry.getValue() * gradeWeight;
            }
        }
        return bonus;
    }

    private Map<String, Double> calculateCareerCompatibilityProbabilitiesForAL(Map<Integer, Integer> olGrades,
                                                                               Map<Integer, Integer> alGrades,
                                                                               Integer alStream,
                                                                               Double zScore) {
        Map<String, Double> probabilities = new HashMap<>();
        for (String career : Config.CAREERS.keySet()) {
            double base = Config.CAREER_COMPATIBILITY.get(Config.CAREERS.get(career));
            double olBonus = calculateOLCareerBonus(career, olGrades);
            double alBonus = calculateALCareerBonus(career, alGrades);
            double zScoreBonus = calculateZScoreBonus(career, zScore != null ? zScore : 0.0);
            double streamBonus = calculateStreamBonus(career, alStream);
            probabilities.put(career, base + olBonus + alBonus + zScoreBonus + streamBonus);
        }
        return normalizeProbabilities(probabilities);
    }

    private double calculateStreamBonus(String career, Integer alStream) {
        if (alStream == null) return 0.0;
        Map<Integer, Double> careerBonus = Config.CAREER_COMPATIBILITY_BONUS.get(Config.CAREERS.get(career));
        return careerBonus.getOrDefault(alStream, 0.0);
    }

    private Map<String, Double> calculateCareerCompatibilityProbabilitiesForUNI(Map<Integer, Integer> olGrades,
                                                                                Map<Integer, Integer> alGrades,
                                                                                Integer alStream,
                                                                                Double zScore,
                                                                                Double gpa) {
        Map<String, Double> probabilities = new HashMap<>();
        for (String career : Config.CAREERS.keySet()) {
            double base = Config.CAREER_COMPATIBILITY.get(Config.CAREERS.get(career));
            double olBonus = calculateOLCareerBonus(career, olGrades);
            double alBonus = calculateALCareerBonus(career, alGrades);
            double zScoreBonus = calculateZScoreBonus(career, zScore != null ? zScore : 0.0);
            double gpaBonus = calculateGPABonus(career, gpa != null ? gpa : 0.0);
            double streamBonus = calculateStreamBonus(career, alStream);
            probabilities.put(career, base + olBonus + alBonus + zScoreBonus + gpaBonus + streamBonus);
        }
        return normalizeProbabilities(probabilities);
    }

    private double calculateALCareerBonus(String career, Map<Integer, Integer> alGrades) {
        double bonus = 0.0;
        Map<Integer, Double> careerBonus = Config.CAREER_COMPATIBILITY_BONUS.get(Config.CAREERS.get(career));
        for (Map.Entry<Integer, Integer> entry : alGrades.entrySet()) {
            if (careerBonus.containsKey(entry.getKey())) {
                int grade = entry.getValue();
                double gradeWeight = 1.0 - (grade * 0.2);
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

    private Map<String, Double> normalizeProbabilities(Map<String, Double> probabilities) {
        double total = probabilities.values().stream().mapToDouble(Double::doubleValue).sum();
        return probabilities.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue() / total));
    }

    private StudentProfile createStudentProfile(int educationLevel, Map<String, Double> olGrades,
                                                Integer alStream, Map<String, Double> alGrades,
                                                Double zScore, Double gpa) {
        if (educationLevel == Config.EDUCATION_LEVELS.get("OL")) {
            return new StudentProfile(educationLevel, olGrades);
        } else if (educationLevel == Config.EDUCATION_LEVELS.get("AL")) {
            return new StudentProfile(educationLevel, olGrades, alStream, alGrades, zScore);
        } else {
            return new StudentProfile(educationLevel, olGrades, alStream, alGrades, zScore, gpa);
        }
    }
}