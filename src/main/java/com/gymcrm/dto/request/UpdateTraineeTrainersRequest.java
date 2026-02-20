package com.gymcrm.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

public class UpdateTraineeTrainersRequest {

    @NotBlank
    private String traineeUsername;

    @NotEmpty
    private List<@NotBlank String> trainerUsernames;

    public UpdateTraineeTrainersRequest() {}

    public String getTraineeUsername() { return traineeUsername; }
    public void setTraineeUsername(String traineeUsername) { this.traineeUsername = traineeUsername; }

    public List<String> getTrainerUsernames() { return trainerUsernames; }
    public void setTrainerUsernames(List<String> trainerUsernames) { this.trainerUsernames = trainerUsernames; }
}
