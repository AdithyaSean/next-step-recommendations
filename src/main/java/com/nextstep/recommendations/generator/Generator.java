package com.nextstep.recommendations.generator;

import com.nextstep.recommendations.config.Config;

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

                for (int subjectId : Config.AL_SUBJECTS_BY_STREAM.get(streamId)) {
                    profile.put("AL_subject_" + subjectId + "_score", random.nextInt(100));
                }
            }

            if (eduType == Config.EDUCATION_LEVELS.get("UNI")) {
                profile.put("university_score", random.nextInt(40) + 60);
                double gpa = random.nextGaussian() * 0.4 + 3.2;
                gpa = Math.max(Config.GPA_RANGE[0], Math.min(Config.GPA_RANGE[1], gpa));
                profile.put("gpa", Math.round(gpa * 100.0) / 100.0);
            }

            data.add(profile);
        }

        return data;
    }

    public static void main(String[] args) {
        Generator generator = new Generator();
        List<Map<String, Object>> data = generator.generate();
        // Save data to CSV or database
    }
}
