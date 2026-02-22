package com.gymcrm.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * Trainee username is omitted: the URI path variable identifies the resource.
 * Repeating it in the body would force callers to send a field that is discarded.
 */
public class UpdateTraineeTrainersRequest {

    @NotEmpty
    private List<@NotBlank String> trainerUsernames;

    public UpdateTraineeTrainersRequest() {}

    public List<String> getTrainerUsernames() { return trainerUsernames; }
    public void setTrainerUsernames(List<String> trainerUsernames) { this.trainerUsernames = trainerUsernames; }
}

