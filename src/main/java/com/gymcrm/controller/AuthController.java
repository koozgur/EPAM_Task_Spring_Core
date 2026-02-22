package com.gymcrm.controller;

import com.gymcrm.dto.request.ChangePasswordRequest;
import com.gymcrm.facade.GymFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Handles login verification and password changes.
 *
 * GET  /login           — AuthenticationFilter validates the Basic credentials;
 *                         if it passes the request through, we simply return 200 OK.
 * PUT  /change-password — same: filter has already validated credentials.
 */
@RestController
@Api(tags = "Authentication")
public class AuthController {

    private final GymFacade facade;

    @Autowired
    public AuthController(GymFacade facade) {
        this.facade = facade;
    }

    @GetMapping("/login")
    @ApiOperation(value = "Login", notes = "Requires HTTP Basic auth. Send credentials via Authorization: Basic <base64(username:password)>.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized — missing or invalid Basic credentials")
    })
    public ResponseEntity<Void> login() {
        return ResponseEntity.ok().build();
    }

    @PutMapping("/change-password")
    @ApiOperation(value = "Change password", notes = "Requires HTTP Basic auth. New password length should be at least 8 characters. Send credentials via Authorization: Basic <base64(username:password)>.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "Validation error"),
            @ApiResponse(code = 401, message = "Unauthorized — missing or invalid Basic credentials")
    })
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest req) {
        facade.changePassword(req);
        return ResponseEntity.ok().build();
    }
}
