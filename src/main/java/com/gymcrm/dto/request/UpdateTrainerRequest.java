package com.gymcrm.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Username is omitted: the URI path variable is the resource identifier (REST convention),
   Accepting it here would imply mutability and force callers to send a field the server discards.
 */
public class UpdateTrainerRequest {

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    //TODO: Specialization is read-only — log warning will be added
    @NotNull
    private Boolean isActive;

    public UpdateTrainerRequest() {}

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
