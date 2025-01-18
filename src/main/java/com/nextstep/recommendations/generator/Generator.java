package com.nextstep.recommendations.generator;

import com.nextstep.recommendations.config.Config;
import com.nextstep.recommendations.predictor.Predictor;
import com.nextstep.recommendations.preprocessor.Preprocessor;
import com.nextstep.recommendations.trainer.Trainer;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSink;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Generator {

    public List<Map<String, Object>> generate() {
        List<Map<String, Object>> data = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < Config.NUM_STUDENTS; i++) {
            Map<String, Object> profile = new HashMap<>();
            profile.put("profile_id", i + 1);

            int eduType = random.nextInt(Config.EDUCATION_LEVELS.size());
            profile.put("education_level", eduType);

                for (Map.Entry<String, Integer> entry : Config.OL_SUBJECTS.entrySet()) {
                    profile.put("OL_subject_" + entry.getValue() + "_score", random.nextInt(100));
                }

            if (eduType == Config.EDUCATION_LEVELS.get("AL") || eduType == Config.EDUCATION_LEVELS.get("UNI")) {
                int streamId = random.nextInt(Config.AL_STREAMS.size());
                profile.put("AL_stream", streamId);

                List<Integer> subjects = Config.AL_SUBJECTS_BY_STREAM.get(streamId);
                if (subjects != null) {
                    for (int subjectId : subjects) {
                    profile.put("AL_subject_" + subjectId + "_score", random.nextInt(100));
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

            data.add(profile);
            }

        return data;
    }

    public void saveToARFF(List<Map<String, Object>> data) throws Exception {
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

            String career = Config.CAREERS.keySet().toArray()[(int) profile.get("career")].toString();
            instance.setValue(attributes.get(index), career);

            dataset.add(instance);
        }

        File dataDir = new File(Config.DATA_DIR);
        if (!dataDir.exists()) {
            if (!dataDir.mkdirs()) {
                throw new IOException("Failed to create directory: " + Config.DATA_DIR);
            }
        }
        DataSink.write(Config.DATA_DIR + "/student_profiles.arff", dataset);
    }

    public static void main(String[] args) throws Exception {
        Generator generator = new Generator();
        List<Map<String, Object>> data = generator.generate();
        generator.saveToARFF(data);

        Preprocessor preprocessor = new Preprocessor();
        preprocessor.preprocess();

        Trainer trainer = new Trainer();
        trainer.train();

        Predictor predictor = new Predictor(Config.MODEL_DIR + "/career_predictor.model");

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
        System.out.println(predictor.predict(0, olResults, null, null, null));

        Map<String, Double> alResults = Map.of(
                "0", 88.0,
                "1", 82.0,
                "2", 90.0
        );

        System.out.println("\nAL Science Student Profile:");
        System.out.println("=========================");
        System.out.println("\nPredicted Career Probabilities:");
        System.out.println(predictor.predict(1, olResults, 0, alResults, null));

        System.out.println("\nUniversity Student Profile:");
        System.out.println("=========================");
        System.out.println("\nPredicted Career Probabilities:");
        System.out.println(predictor.predict(2, olResults, 0, alResults, 3.75));
    }
}
