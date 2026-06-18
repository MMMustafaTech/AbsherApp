package com.absher.absherapp.controller;

import com.absher.absherapp.dto.LoginRequest;
import com.absher.absherapp.dto.RegisterRequest;
import com.absher.absherapp.dto.UserResponse;
import com.absher.absherapp.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {

        UserResponse response = userService.register(
                request.getNationalId(),
                request.getPassword(),
                request.getEmail()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        UserResponse response = userService.login(
                request.getNationalId(),
                request.getPassword()
        );

        return ResponseEntity.ok(response);
    }
}