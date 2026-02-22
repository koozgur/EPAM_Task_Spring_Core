package com.gymcrm.mapper;

import com.gymcrm.dto.response.TraineeSummaryResponse;
import com.gymcrm.model.Trainee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

// TraineeSummaryMapper.java
@Mapper(componentModel = "spring")
public interface TraineeSummaryMapper {
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    TraineeSummaryResponse toSummary(Trainee trainee);
}
