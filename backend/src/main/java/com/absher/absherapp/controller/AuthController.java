package com.absher.absherapp.controller;

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
    public String register(
            @RequestParam String nationalId,
            @RequestParam String password) {

        userService.register(nationalId, password);

        return "User registered successfully";
    }
}