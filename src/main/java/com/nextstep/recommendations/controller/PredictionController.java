package com.nextstep.recommendations.controller;

import com.nextstep.recommendations.model.StudentProfile;
import com.nextstep.recommendations.services.PredictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/predictions")
public class PredictionController {

    private final PredictionService predictionService;

    @Autowired
    public PredictionController(PredictionService predictionService) {
        this.predictionService = predictionService;
    }

    @PostMapping("/career")
    public Map<String, Double> predictCareer(@RequestBody StudentProfile profile) {
        return predictionService.predictCareerProbabilities(profile);
    }
}
