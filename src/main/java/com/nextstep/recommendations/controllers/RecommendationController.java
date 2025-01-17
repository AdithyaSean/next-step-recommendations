package com.nextstep.recommendations.controllers;

import com.nextstep.recommendations.generator.Generator;
import com.nextstep.recommendations.model.PredictionRequest;
import com.nextstep.recommendations.predictor.Predictor;
import com.nextstep.recommendations.trainer.Trainer;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Map;

@RestController
@RequestMapping("/recommendations")
public class RecommendationController {

    private final Predictor predictor;

    public RecommendationController(Predictor predictor) {
        this.predictor = predictor;
    }

    @PostMapping("/predict")
    public Map<String, Double> predict(@RequestBody PredictionRequest request) throws Exception {
        return predictor.predict(
            request.getEducationLevel(),
            request.getOlResults(),
            request.getAlStream(),
            request.getAlResults(),
            request.getGpa()
        );
    }

    @PostMapping("/train")
    public String train() throws Exception {
        Trainer trainer = new Trainer();
        trainer.train();
        return "Model trained successfully!";
    }

    @PostMapping("/generate")
    public String generate() {
        Generator generator = new Generator();
        generator.generate();
        return "Data generated successfully!";
    }
}
