package com.gymcrm.dto.request;

import javax.validation.constraints.NotNull;

/**
 * Username is omitted: the URI path variable identifies the resource.
 * Repeating it in the body would force callers to send a field that is unused
 */
public class ActivationRequest {

    @NotNull
    private Boolean isActive;

    public ActivationRequest() {}

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
