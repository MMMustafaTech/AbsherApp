package com.chari.chariapp.account.infrastructure.web;

import com.chari.chariapp.account.application.LoginCommand;
import com.chari.chariapp.account.application.LoginUseCase;
import com.chari.chariapp.account.application.LogoutUseCase;
import com.chari.chariapp.account.application.RefreshTokenUseCase;
import com.chari.chariapp.account.application.TokenPair;
import com.chari.chariapp.shared.security.PersonalDataProtector;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;
import java.util.Locale;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final LoginUseCase loginUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final LogoutUseCase logoutUseCase;
    private final PersonalDataProtector dataProtector;

    public AuthenticationController(
            LoginUseCase loginUseCase,
            RefreshTokenUseCase refreshTokenUseCase,
            LogoutUseCase logoutUseCase,
            PersonalDataProtector dataProtector
    ) {
        this.loginUseCase = loginUseCase;
        this.refreshTokenUseCase = refreshTokenUseCase;
        this.logoutUseCase = logoutUseCase;
        this.dataProtector = dataProtector;
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase(Locale.ROOT);
        return response(loginUseCase.login(new LoginCommand(dataProtector.lookup(normalizedEmail), request.password())));
    }

    @PostMapping("/refresh")
    public TokenResponse refresh(@Valid @RequestBody RefreshRequest request) {
        return response(refreshTokenUseCase.refresh(request.refreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshRequest request) {
        logoutUseCase.logout(request.refreshToken());
        return ResponseEntity.noContent().build();
    }

    private static TokenResponse response(TokenPair tokenPair) {
        long expiresIn = Math.max(0, Duration.between(Instant.now(), tokenPair.accessTokenExpiresAt()).toSeconds());
        return new TokenResponse(tokenPair.accessToken(), tokenPair.refreshToken(), "Bearer", expiresIn);
    }

    public record LoginRequest(
            @NotBlank @Email @Size(max = 254) String email,
            @NotBlank @Size(min = 12, max = 128) String password
    ) {
    }

    public record RefreshRequest(@NotBlank @Size(max = 512) String refreshToken) {
    }

    public record TokenResponse(String accessToken, String refreshToken, String tokenType, long expiresIn) {
    }
}
