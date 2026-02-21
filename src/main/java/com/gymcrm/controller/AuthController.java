package com.gymcrm.controller;

import com.gymcrm.dto.request.ChangePasswordRequest;
import com.gymcrm.facade.GymFacade;
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
public class AuthController {

    private final GymFacade facade;

    @Autowired
    public AuthController(GymFacade facade) {
        this.facade = facade;
    }

    @GetMapping("/login")
    public ResponseEntity<Void> login() {
        return ResponseEntity.ok().build();
    }

    @PutMapping("/change-password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest req) {
        facade.changePassword(req);
        return ResponseEntity.ok().build();
    }
}
