package com.gymcrm.mapper;

import com.gymcrm.dto.response.TrainingTypeResponse;
import com.gymcrm.model.TrainingType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TrainingTypeMapper {

    @Mapping(source = "trainingTypeName", target = "trainingType")
    TrainingTypeResponse toResponse(TrainingType trainingType);
}
