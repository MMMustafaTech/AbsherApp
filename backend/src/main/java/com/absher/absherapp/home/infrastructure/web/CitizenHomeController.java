package com.absher.absherapp.home.infrastructure.web;

import com.absher.absherapp.account.domain.AccountId;
import com.absher.absherapp.home.application.CitizenHome;
import com.absher.absherapp.home.application.CitizenHomeService;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequestMapping("/api/v1/me/home") public class CitizenHomeController {
    private final CitizenHomeService home;
    public CitizenHomeController(CitizenHomeService home) { this.home = home; }
    @GetMapping public CitizenHome home(@AuthenticationPrincipal Jwt jwt) { return home.home(new AccountId(UUID.fromString(jwt.getSubject()))); }
}
