package com.absher.absherapp.controller;

import com.absher.absherapp.dto.LoginRequest;
import com.absher.absherapp.dto.RegisterRequest;
import com.absher.absherapp.dto.UserResponse;
import com.absher.absherapp.entity.User;
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
    public String register(@RequestBody RegisterRequest request) {

        userService.register(
                request.getNationalId(),
                request.getPassword(),
                request.getEmail());

        return "User registered successfully";
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        User user = userService.login(
                request.getNationalId(),
                request.getPassword()
        );
        UserResponse response = new UserResponse(
                user.getId(),
                user.getNationalIdNumber(),
                user.getEmail()
        );
        return ResponseEntity.ok(response);
    }
}