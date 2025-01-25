package com.nextstep.recommendations.utils;

import com.nextstep.recommendations.config.Config;
import com.nextstep.recommendations.model.StudentProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.SerializationHelper;

import java.util.*;

@Component
public class Predictor {
    private final Classifier model;

    @Autowired
    public Predictor(@Value("${model.path}") String modelPath) throws Exception {
        this.model = (Classifier) SerializationHelper.read(modelPath);
    }

    public StudentProfile updateCareerProbabilities(StudentProfile studentProfile) throws Exception {
        Instances dataset = createDataset();
        DenseInstance instance = createInstance(studentProfile, dataset);

        double[] probabilities = model.distributionForInstance(instance);

        Map<String, Double> careerProbabilities = new HashMap<>();
        for (int i = 0; i < probabilities.length; i++) {
            String careerName = getCareerNameByIndex(i);
            careerProbabilities.put(careerName, probabilities[i]);
        }

        studentProfile.setCareerProbabilities(careerProbabilities);

        return studentProfile;
    }

    private Instances createDataset() {
        ArrayList<Attribute> attributes = new ArrayList<>();

        for (String subject : Config.OL_SUBJECTS.keySet()) {
            attributes.add(new Attribute("OL_" + subject));
        }

        attributes.add(new Attribute("AL_Stream"));

        for (String subject : Config.AL_SUBJECTS.keySet()) {
            attributes.add(new Attribute("AL_" + subject));
        }

        attributes.add(new Attribute("GPA"));

        List<String> careerNames = new ArrayList<>(Config.CAREERS.keySet());
        attributes.add(new Attribute("Career", careerNames));

        Instances dataset = new Instances("StudentProfileDataset", attributes, 0);
        dataset.setClassIndex(dataset.numAttributes() - 1);

        return dataset;
    }

    private DenseInstance createInstance(StudentProfile studentProfile, Instances dataset) {
        DenseInstance instance = new DenseInstance(dataset.numAttributes());

        for (Map.Entry<String, Double> entry : studentProfile.getOlResults().entrySet()) {
            String subject = entry.getKey();
            double grade = entry.getValue();
            instance.setValue(dataset.attribute("OL_" + subject), grade);
        }

        if (studentProfile.getAlStream() != null) {
            instance.setValue(dataset.attribute("AL_Stream"), studentProfile.getAlStream());
        }

        if (studentProfile.getAlResults() != null) {
            for (Map.Entry<String, Double> entry : studentProfile.getAlResults().entrySet()) {
                String subject = entry.getKey();
                double grade = entry.getValue();
                instance.setValue(dataset.attribute("AL_" + subject), grade);
            }
        }

        if (studentProfile.getGpa() != null) {
            instance.setValue(dataset.attribute("GPA"), studentProfile.getGpa());
        }

        instance.setDataset(dataset);

        return instance;
    }

    private String getCareerNameByIndex(int index) {
        for (Map.Entry<String, Integer> entry : Config.CAREERS.entrySet()) {
            if (entry.getValue() == index) {
                return entry.getKey();
            }
        }
        return null;
    }
}