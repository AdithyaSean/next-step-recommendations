package com.nextstep.recommendations.service;

import com.nextstep.recommendations.model.PredictionRequest;
import com.nextstep.recommendations.model.StudentProfile;
import com.nextstep.recommendations.dto.StudentProfileDTO;
import com.nextstep.recommendations.predictor.Predictor;
import com.nextstep.recommendations.repository.RecommendationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

@Service
public class PredictionService {

    private final RecommendationRepository recommendationRepository;
    private final Predictor predictor;
    private final RestTemplate restTemplate = new RestTemplate();

    public PredictionService(RecommendationRepository recommendationRepository, Predictor predictor) {
        this.recommendationRepository = recommendationRepository;
        this.predictor = predictor;
    }

    @Transactional
    public Map<String, Double> getCareerProbabilities(UUID id, PredictionRequest request) throws Exception {

        Map<String, Double> careerProbabilities = predictor.predict(
                request.getEducationLevel(),
                request.getOlResults(),
                request.getAlStream(),
                request.getAlResults(),
                request.getGpa()
        );

        // Save the updated profile to the database
        StudentProfile studentProfile = new StudentProfile();
        studentProfile.setId(id);
        studentProfile.setEducationLevel(request.getEducationLevel());
        studentProfile.setOlResults(request.getOlResults());
        studentProfile.setAlStream(request.getAlStream());
        studentProfile.setAlResults(request.getAlResults());
        studentProfile.setGpa(request.getGpa());
        studentProfile.setCareerProbabilities(careerProbabilities);
        StudentProfile savedProfile = recommendationRepository.save(studentProfile);

        // Convert the saved profile to DTO
        StudentProfileDTO updatedProfileDTO = mapToDTO(savedProfile);

        String url = "http://localhost:8080/users/students/profiles/" + id;
        restTemplate.put(url, updatedProfileDTO);

        return careerProbabilities;
    }

    private StudentProfileDTO mapToDTO(StudentProfile studentProfile) {
        StudentProfileDTO dto = new StudentProfileDTO();
        dto.setId(studentProfile.getId());
        dto.setEducationLevel(studentProfile.getEducationLevel());
        dto.setOlResults(studentProfile.getOlResults());
        dto.setAlStream(studentProfile.getAlStream());
        dto.setAlResults(studentProfile.getAlResults());
        dto.setGpa(studentProfile.getGpa());
        dto.setCareerProbabilities(studentProfile.getCareerProbabilities());
        dto.setCreatedAt(studentProfile.getCreatedAt());
        dto.setUpdatedAt(studentProfile.getUpdatedAt());
        return dto;
    }
}