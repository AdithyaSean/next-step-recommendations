package com.nextstep.recommendations.controllers;

import com.nextstep.recommendations.dto.StudentProfileDTO;
import com.nextstep.recommendations.service.PredictionService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/predictions")
public class PredictionController {

    private final PredictionService predictionService;

    @PostMapping("/")
    public StudentProfileDTO updateStudentProfile(@RequestBody StudentProfileDTO studentProfileDTO) throws Exception {
        return predictionService.updateStudentProfile(studentProfileDTO);
    }
}