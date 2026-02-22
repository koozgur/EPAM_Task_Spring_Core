package com.gymcrm.mapper;

import com.gymcrm.dto.response.TrainerSummaryResponse;
import com.gymcrm.dto.response.TrainerProfileResponse;
import com.gymcrm.dto.response.UpdateTrainerResponse;
import com.gymcrm.model.Trainer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Uses TrainingTypeMapper to convert TrainingType → TrainingTypeResponse (specialization field).
 * Uses TraineeMapper to convert the nested List<Trainee> trainees collection.
 */
@Mapper(componentModel = "spring", uses = {TrainingTypeMapper.class, TraineeMapper.class})
public interface TrainerMapper {

    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    @Mapping(source = "specialization", target = "specialization")  // TrainingType → TrainingTypeResponse via TrainingTypeMapper.toResponse
    TrainerSummaryResponse toSummary(Trainer trainer);

    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    @Mapping(source = "specialization", target = "specialization")
    @Mapping(source = "user.isActive", target = "isActive")
    @Mapping(source = "trainees", target = "trainees")  // List<Trainee> → List<TraineeSummaryResponse> via TraineeMapper.toSummary
    TrainerProfileResponse toProfileResponse(Trainer trainer);

    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    @Mapping(source = "specialization", target = "specialization")
    @Mapping(source = "user.isActive", target = "isActive")
    @Mapping(source = "trainees", target = "trainees")
    UpdateTrainerResponse toUpdateResponse(Trainer trainer);
}
