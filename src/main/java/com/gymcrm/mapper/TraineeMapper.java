package com.gymcrm.mapper;

import com.gymcrm.dto.response.TraineeSummaryResponse;
import com.gymcrm.dto.response.TraineeProfileResponse;
import com.gymcrm.dto.response.UpdateTraineeResponse;
import com.gymcrm.model.Trainee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Uses TrainerMapper to convert the nested List<Trainer> trainers collection.
 */
@Mapper(componentModel = "spring", uses = {TrainerSummaryMapper.class})
public interface TraineeMapper {

    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    TraineeSummaryResponse toSummary(Trainee trainee);

    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    @Mapping(source = "user.isActive", target = "isActive")
    @Mapping(source = "trainers", target = "trainers")   // List<Trainer> → List<TrainerSummaryResponse> via TrainerMapper.toSummary
    TraineeProfileResponse toProfileResponse(Trainee trainee);

    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    @Mapping(source = "user.isActive", target = "isActive")
    @Mapping(source = "trainers", target = "trainers")
    UpdateTraineeResponse toUpdateResponse(Trainee trainee);
}
