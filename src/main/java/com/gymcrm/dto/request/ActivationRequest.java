package com.gymcrm.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class ActivationRequest {

    @NotBlank
    private String username;

    @NotNull
    private Boolean isActive;

    public ActivationRequest() {}

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
