package com.gymcrm.mapper;

import com.gymcrm.dto.response.TrainerSummaryResponse;
import com.gymcrm.model.Trainer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

// TrainerSummaryMapper.java
@Mapper(componentModel = "spring")
public interface TrainerSummaryMapper {
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    TrainerSummaryResponse toSummary(Trainer trainer);
}
