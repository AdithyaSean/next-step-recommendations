package com.nextstep.recommendations.src;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSink;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Generator {

    public Map<String, List<Map<String, Object>>> generate() {
        Map<String, List<Map<String, Object>>> data = new HashMap<>();
        data.put("OL", new ArrayList<>());
        data.put("AL", new ArrayList<>());
        data.put("UNI", new ArrayList<>());
        Random random = new Random();

        for (int i = 0; i < Config.NUM_STUDENTS; i++) {
            Map<String, Object> profile = new HashMap<>();
            profile.put("profile_id", i + 1);

            int eduType = getRandomEducationLevel(random);
            profile.put("education_level", eduType);

            for (Map.Entry<String, Integer> entry : Config.OL_SUBJECTS.entrySet()) {
                profile.put("OL_subject_" + entry.getValue() + "_score", random.nextInt(5) + 1);
            }

            if (eduType == Config.EDUCATION_LEVELS.get("AL") || eduType == Config.EDUCATION_LEVELS.get("UNI")) {
                int streamId = random.nextInt(Config.AL_STREAMS.size());
                profile.put("AL_stream", streamId);

                List<Integer> subjects = Config.AL_SUBJECTS_BY_STREAM.get(streamId);
                if (subjects != null) {
                    for (int subjectId : subjects) {
                        profile.put("AL_subject_" + subjectId + "_score", random.nextInt(5) + 1);
                    }
                }
            } else {
                profile.put("AL_stream", -1);
                for (Map.Entry<String, Integer> entry : Config.AL_SUBJECTS.entrySet()) {
                    profile.put("AL_subject_" + entry.getValue() + "_score", -1);
                }
            }

            if (eduType == Config.EDUCATION_LEVELS.get("UNI")) {
                profile.put("university_score", random.nextInt(40) + 60);
                double gpa = random.nextGaussian() * 0.4 + 3.2;
                gpa = Math.max(Config.GPA_RANGE[0], Math.min(Config.GPA_RANGE[1], gpa));
                profile.put("gpa", Math.round(gpa * 100.0) / 100.0);
            } else {
                profile.put("university_score", -1);
                profile.put("gpa", -1.0);
            }

            List<Integer> possibleCareers = Config.CAREERS_BY_EDUCATION_LEVEL.get(eduType);
            int careerIndex = possibleCareers.get(random.nextInt(possibleCareers.size()));
            profile.put("career", careerIndex);

            if (eduType == Config.EDUCATION_LEVELS.get("OL")) {
                data.get("OL").add(profile);
            } else if (eduType == Config.EDUCATION_LEVELS.get("AL")) {
                data.get("AL").add(profile);
            } else {
                data.get("UNI").add(profile);
            }
        }

        return data;
    }

    private int getRandomEducationLevel(Random random) {
        double rand = random.nextDouble();
        double cumulativeProbability = 0.0;
        for (Map.Entry<Integer, Double> entry : Config.EDUCATION_LEVEL_DIST.entrySet()) {
            cumulativeProbability += entry.getValue();
            if (rand <= cumulativeProbability) {
                return entry.getKey();
            }
        }
        return Config.EDUCATION_LEVELS.get("OL");
    }

    public void saveToARFF(Map<String, List<Map<String, Object>>> data) throws Exception {
        saveDatasetToARFF(data.get("OL"), "OL_student_profiles.arff");
        saveDatasetToARFF(data.get("AL"), "AL_student_profiles.arff");
        saveDatasetToARFF(data.get("UNI"), "UNI_student_profiles.arff");
    }

    private void saveDatasetToARFF(List<Map<String, Object>> data, String fileName) throws Exception {
        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("profile_id"));
        attributes.add(new Attribute("education_level"));

        for (Map.Entry<String, Integer> entry : Config.OL_SUBJECTS.entrySet()) {
            attributes.add(new Attribute("OL_subject_" + entry.getValue() + "_score"));
        }

        attributes.add(new Attribute("AL_stream"));
        for (Map.Entry<String, Integer> entry : Config.AL_SUBJECTS.entrySet()) {
            attributes.add(new Attribute("AL_subject_" + entry.getValue() + "_score"));
        }

        attributes.add(new Attribute("university_score"));
        attributes.add(new Attribute("gpa"));

        List<String> careerValues = new ArrayList<>(Config.CAREERS.keySet());
        attributes.add(new Attribute("career", careerValues));

        Instances dataset = new Instances("student_profiles", attributes, data.size());
        dataset.setClassIndex(dataset.numAttributes() - 1);

        for (Map<String, Object> profile : data) {
            DenseInstance instance = new DenseInstance(attributes.size());
            instance.setValue(attributes.get(0), (int) profile.get("profile_id"));
            instance.setValue(attributes.get(1), (int) profile.get("education_level"));

            int index = 2;
            for (Map.Entry<String, Integer> entry : Config.OL_SUBJECTS.entrySet()) {
                instance.setValue(attributes.get(index++), (int) profile.get("OL_subject_" + entry.getValue() + "_score"));
            }
            Integer alStream = (Integer) profile.get("AL_stream");
            instance.setValue(attributes.get(index++), Objects.requireNonNullElse(alStream, 0));

            for (Map.Entry<String, Integer> entry : Config.AL_SUBJECTS.entrySet()) {
                Integer subjectScore = (Integer) profile.get("AL_subject_" + entry.getValue() + "_score");
                instance.setValue(attributes.get(index++), Objects.requireNonNullElse(subjectScore, 0));
            }

            Integer universityScore = (Integer) profile.get("university_score");
            instance.setValue(attributes.get(index++), Objects.requireNonNullElse(universityScore, 0));

            Double gpa = (Double) profile.get("gpa");
            instance.setValue(attributes.get(index++), Objects.requireNonNullElse(gpa, 0.0));

            String career = Config.CAREERS.entrySet().stream()
                    .filter(entry -> entry.getValue().equals(profile.get("career")))
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse("Unknown");
            instance.setValue(attributes.get(index), career);

            dataset.add(instance);
        }

        File dataDir = new File(Config.MODEL_DIR);
        if (!dataDir.exists()) {
            if (!dataDir.mkdirs()) {
                throw new IOException("Failed to create directory: " + Config.MODEL_DIR);
            }
        }
        DataSink.write(Config.MODEL_DIR + "/" + fileName, dataset);
    }
}