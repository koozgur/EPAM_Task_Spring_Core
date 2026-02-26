package com.gymcrm.dto.response;

/** Returned after successful trainee or trainer registration. */
public class RegistrationResponse {

    private String username;
    private String password;
    /** JWT Bearer token — client can use this immediately after registration. */
    private String token;

    public RegistrationResponse() {}

    public RegistrationResponse(String username, String password, String token) {
        this.username = username;
        this.password = password;
        this.token = token;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}
