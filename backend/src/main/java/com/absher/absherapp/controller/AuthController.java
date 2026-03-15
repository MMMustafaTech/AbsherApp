package com.absher.absherapp.controller;

import com.absher.absherapp.dto.RegisterRequest;
import com.absher.absherapp.service.UserService;
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

        userService.register(request.getNationalId(), request.getPassword());

        return "User registered successfully";
    }
}