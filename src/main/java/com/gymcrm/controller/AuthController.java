package com.gymcrm.controller;

import com.gymcrm.dto.request.ChangePasswordRequest;
import com.gymcrm.facade.GymFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

/**
 * Handles login verification and password changes.
 *
 * GET  /login           — AuthenticationFilter validates the Basic credentials;
 *                         if it passes the request through, we simply return 200 OK.
 * PUT  /change-password — same: filter has already validated credentials.
 */
@RestController
@Tag(name = "Authentication")
public class AuthController {

    private final GymFacade facade;

    @Autowired
    public AuthController(GymFacade facade) {
        this.facade = facade;
    }

    @GetMapping("/login")
        @Operation(summary = "Login", description = "Requires HTTP Basic auth. Send credentials via Authorization: Basic <base64(username:password)>." )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Unauthorized — missing or invalid Basic credentials")
    })
    public ResponseEntity<Void> login() {
        return ResponseEntity.ok().build();
    }

    @PutMapping("/change-password")
        @Operation(summary = "Change password", description = "Requires HTTP Basic auth. New password length should be at least 8 characters. Send credentials via Authorization: Basic <base64(username:password)>." )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized — missing or invalid Basic credentials")
    })
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest req) {
        facade.changePassword(req);
        return ResponseEntity.ok().build();
    }
}
