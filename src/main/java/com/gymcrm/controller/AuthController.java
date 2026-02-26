package com.gymcrm.controller;

import com.gymcrm.dto.request.ChangePasswordRequest;
import com.gymcrm.dto.request.LoginRequest;
import com.gymcrm.dto.response.LoginResponse;
import com.gymcrm.facade.GymFacade;
import com.gymcrm.security.JwtAuthenticationFilter;
import com.gymcrm.security.JwtTokenProvider;
import com.gymcrm.security.LoginAttemptService;
import com.gymcrm.security.TokenBlacklistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handles login (JWT issuance), logout (JWT invalidation), and password changes.
 *
 * POST /login           — validates credentials, enforces brute-force lockout, returns a JWT.
 * POST /logout          — blacklists the current JWT so it cannot be reused after logout.
 * PUT  /change-password — changes the authenticated user's password.
 */
@RestController
@Tag(name = "Authentication")
public class AuthController {

    private final GymFacade facade;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final LoginAttemptService loginAttemptService;
    private final TokenBlacklistService tokenBlacklistService;

    @Autowired
    public AuthController(GymFacade facade,
                          AuthenticationManager authenticationManager,
                          JwtTokenProvider jwtTokenProvider,
                          LoginAttemptService loginAttemptService,
                          TokenBlacklistService tokenBlacklistService) {
        this.facade = facade;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.loginAttemptService = loginAttemptService;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    // POST is used because credentials are sent in the request body.
    // GET would expose username/password in server logs and browser history via query params.
    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticates with username/password and returns a JWT Bearer token.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK — JWT token returned"),
            @ApiResponse(responseCode = "401", description = "Unauthorized — invalid credentials"),
            @ApiResponse(responseCode = "429", description = "Too Many Requests — account is temporarily locked after too many failed attempts")
    })
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        // Brute-force check — must happen before credential validation to avoid
        // revealing whether credentials are correct while the account is locked.
        if (loginAttemptService.isBlocked(req.getUsername())) {
            return ResponseEntity.status(429).build();
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
        } catch (BadCredentialsException e) {
            loginAttemptService.loginFailed(req.getUsername());
            return ResponseEntity.status(401).build();
        }

        loginAttemptService.loginSucceeded(req.getUsername());
        String token = jwtTokenProvider.generateToken(req.getUsername());
        return ResponseEntity.ok(new LoginResponse(token, req.getUsername()));
    }

    @PostMapping("/logout")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Logout", description = "Invalidates the current JWT token. " +
            "Any subsequent request with the same token will be rejected.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK — logged out"),
            @ApiResponse(responseCode = "401", description = "Unauthorized — missing or already-invalid Bearer token")
    })
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String token = JwtAuthenticationFilter.extractBearerToken(request);
        if (token != null && jwtTokenProvider.validateToken(token)) {
            String jti = jwtTokenProvider.getJtiFromToken(token);
            tokenBlacklistService.blacklist(jti, jwtTokenProvider.getExpiryFromToken(token));
        }
        return ResponseEntity.ok().build();
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