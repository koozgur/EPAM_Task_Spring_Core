package com.gymcrm.controller;

import com.gymcrm.dto.request.ChangePasswordRequest;
import com.gymcrm.dto.request.LoginRequest;
import com.gymcrm.dto.response.LoginResponse;
import com.gymcrm.facade.GymFacade;
import com.gymcrm.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

/**
 * Handles login verification and password changes.
 *
 * POST /login           — validates credentials and returns a JWT Bearer token.
 * PUT  /change-password — changes the authenticated user's password.
 *                         Identity comes from the Bearer token in the Authorization header.
 */
@RestController
@Tag(name = "Authentication")
public class AuthController {

    private final GymFacade facade;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public AuthController(GymFacade facade,
                          AuthenticationManager authenticationManager,
                          JwtTokenProvider jwtTokenProvider) {
        this.facade = facade;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // POST is used for login because credentials must be sent in the request body.
    // GET would expose username/password in the URL
    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticates with username/password and returns a JWT Bearer token.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK — JWT token returned"),
            @ApiResponse(responseCode = "401", description = "Unauthorized — invalid credentials"),
            @ApiResponse(responseCode = "423", description = "Locked — account is inactive")
    })
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
        } catch (LockedException | DisabledException e) {
            return ResponseEntity.status(423).build();
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).build();
        }

        String token = jwtTokenProvider.generateToken(req.getUsername());
        return ResponseEntity.ok(new LoginResponse(token, req.getUsername()));
    }

    @PutMapping("/change-password")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Change password",
            description = "Changes the authenticated user's password. Requires a valid Bearer token. " +
                          "The username in the request body must match the token's subject.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Validation error or wrong old password"),
            @ApiResponse(responseCode = "401", description = "Unauthorized — missing or invalid Bearer token"),
            @ApiResponse(responseCode = "403", description = "Forbidden — token subject does not match request username")
    })
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody ChangePasswordRequest req,
            @AuthenticationPrincipal UserDetails principal) {

        // Prevent changing another user's password via a valid token
        if (!principal.getUsername().equals(req.getUsername())) {
            return ResponseEntity.status(403).build();
        }

        facade.changePassword(req);
        return ResponseEntity.ok().build();
    }
}
