package com.nextstep.recommendations.mapper;

import com.nextstep.recommendations.dto.StudentProfileDTO;
import com.nextstep.recommendations.model.StudentProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface StudentProfileMapper {

    StudentProfileMapper INSTANCE = Mappers.getMapper(StudentProfileMapper.class);

    @Mapping(target = "careerProbabilities", source = "careerProbabilities")
    StudentProfileDTO toDTO(StudentProfile studentProfile);

    @Mapping(target = "careerProbabilities", source = "careerProbabilities")
    StudentProfile toEntity(StudentProfileDTO studentProfileDTO);
}