package com.nextstep.recommendations.controllers;

import com.nextstep.recommendations.model.PredictionRequest;
import com.nextstep.recommendations.generator.Generator;
import com.nextstep.recommendations.predictor.Predictor;
import com.nextstep.recommendations.trainer.Trainer;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/model")
public class ModelController {

    private final Predictor predictor;
    private final Trainer trainer;

    public ModelController(Predictor predictor, Trainer trainer) {
        this.predictor = predictor;
        this.trainer = trainer;
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