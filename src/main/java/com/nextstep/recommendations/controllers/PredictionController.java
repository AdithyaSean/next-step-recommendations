package com.nextstep.recommendations.controllers;

import com.nextstep.recommendations.dto.StudentProfileDTO;
import com.nextstep.recommendations.service.PredictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/predictions")
public class PredictionController {

    private final PredictionService predictionService;

    @Autowired
    public PredictionController(PredictionService predictionService) {
        this.predictionService = predictionService;
    }

    @PostMapping("/")
    public StudentProfileDTO updateStudentProfile(@RequestBody StudentProfileDTO studentProfileDTO) throws Exception {
        return predictionService.updateStudentProfile(studentProfileDTO);
    }
}