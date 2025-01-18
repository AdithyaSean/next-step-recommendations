package com.nextstep.recommendations.repository;

import com.nextstep.recommendations.model.CareerPrediction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CareerPredictionRepository extends JpaRepository<CareerPrediction, Long> {

}