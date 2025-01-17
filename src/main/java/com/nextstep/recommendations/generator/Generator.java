package com.nextstep.recommendations.generator;

import com.nextstep.recommendations.config.Config;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSink;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Generator {

    public List<Map<String, Object>> generate() {
        List<Map<String, Object>> data = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < Config.NUM_STUDENTS; i++) {
            Map<String, Object> profile = new HashMap<>();
            profile.put("profile_id", i + 1);

            int eduType = random.nextInt(Config.EDUCATION_LEVELS.size());
            profile.put("education_level", eduType);

            if (eduType == Config.EDUCATION_LEVELS.get("OL") || eduType == Config.EDUCATION_LEVELS.get("AL") || eduType == Config.EDUCATION_LEVELS.get("UNI")) {
                for (Map.Entry<String, Integer> entry : Config.OL_SUBJECTS.entrySet()) {
                    profile.put("OL_subject_" + entry.getValue() + "_score", random.nextInt(100));
                }
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
                profile.put("AL_stream", null);
        for (Map.Entry<String, Integer> entry : Config.AL_SUBJECTS.entrySet()) {
                    profile.put("AL_subject_" + entry.getValue() + "_score", null);
        }
            }

            if (eduType == Config.EDUCATION_LEVELS.get("UNI")) {
                profile.put("university_score", random.nextInt(40) + 60);
                double gpa = random.nextGaussian() * 0.4 + 3.2;
                gpa = Math.max(Config.GPA_RANGE[0], Math.min(Config.GPA_RANGE[1], gpa));
                profile.put("gpa", Math.round(gpa * 100.0) / 100.0);
                } else {
                profile.put("university_score", null);
                profile.put("gpa", null);
                }

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

        Instances dataset = new Instances("student_profiles", attributes, data.size());
        dataset.setClassIndex(-1);

        for (Map<String, Object> profile : data) {
            DenseInstance instance = new DenseInstance(attributes.size());
            instance.setValue(attributes.get(0), (int) profile.get("profile_id"));
            instance.setValue(attributes.get(1), (int) profile.get("education_level"));

            int index = 2;
            for (Map.Entry<String, Integer> entry : Config.OL_SUBJECTS.entrySet()) {
                instance.setValue(attributes.get(index++), (int) profile.get("OL_subject_" + entry.getValue() + "_score"));
            }
            Integer alStream = (Integer) profile.get("AL_stream");
            if (alStream != null) {
                instance.setValue(attributes.get(index++), alStream);
            } else {
                instance.setValue(attributes.get(index++), 0);
        }

            for (Map.Entry<String, Integer> entry : Config.AL_SUBJECTS.entrySet()) {
                Integer subjectScore = (Integer) profile.get("AL_subject_" + entry.getValue() + "_score");
                if (subjectScore != null) {
                    instance.setValue(attributes.get(index++), subjectScore);
            } else {
                    instance.setValue(attributes.get(index++), 0);
        }
    }

            Integer universityScore = (Integer) profile.get("university_score");
            if (universityScore != null) {
                instance.setValue(attributes.get(index++), universityScore);
            } else {
                instance.setValue(attributes.get(index++), 0);
    }

            Double gpa = (Double) profile.get("gpa");
            if (gpa != null) {
                instance.setValue(attributes.get(index), gpa);
            } else {
                instance.setValue(attributes.get(index), 0.0);
    }

            dataset.add(instance);
    }

        File dataDir = new File(Config.DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
}
        DataSink.write(Config.DATA_DIR + "/student_profiles.arff", dataset);
    }

    public static void main(String[] args) throws Exception {
        Generator generator = new Generator();
        List<Map<String, Object>> data = generator.generate();
        generator.saveToARFF(data);
    }
}
