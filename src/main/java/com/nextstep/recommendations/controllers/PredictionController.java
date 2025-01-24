package com.nextstep.recommendations.controllers;

import com.nextstep.recommendations.model.PredictionRequest;
import com.nextstep.recommendations.service.PredictionService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/predictions")
public class PredictionController {

    private final PredictionService predictionService;

    public PredictionController(PredictionService predictionService) {
        this.predictionService = predictionService;
    }

    @PostMapping("/{id}")
    public Map<String, Double> getCareerProbabilities(@PathVariable UUID id, @RequestBody PredictionRequest request) throws Exception {
        return predictionService.getCareerProbabilities(id, request);
    }
}