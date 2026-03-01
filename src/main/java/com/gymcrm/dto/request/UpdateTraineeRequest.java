package com.gymcrm.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * Username is omitted: the URI path variable is the resource identifier (REST convention),
   Accepting it here would imply mutability and
   force callers to send a field the server discards.
 */
public class UpdateTraineeRequest {

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @ApiModelProperty(value = "Date of birth (optional)", example = "1995-06-15")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;   // optional

    private String address;          // optional

    @NotNull
    private Boolean isActive;

    public UpdateTraineeRequest() {}

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
