package com.chari.chariapp.account.infrastructure.web;

import com.chari.chariapp.account.application.CitizenProfile;
import com.chari.chariapp.account.application.CitizenProfileService;
import com.chari.chariapp.account.application.CitizenSecurityService;
import com.chari.chariapp.account.domain.AccountId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/me/profile")
public class CitizenProfileController {

    private final CitizenProfileService profileService;
    private final CitizenSecurityService securityService;

    public CitizenProfileController(CitizenProfileService profileService, CitizenSecurityService securityService) {
        this.profileService = profileService;
        this.securityService = securityService;
    }

    @GetMapping
    public ProfileResponse profile(@AuthenticationPrincipal Jwt jwt) {
        CitizenProfile profile = profileService.profile(accountId(jwt));
        return new ProfileResponse(
                profile.email(), profile.maskedNationalId(), profile.maskedVerifiedPhone(),
                profile.phoneVerified(), profile.phoneVerifiedAt(), profile.accountCreatedAt()
        );
    }

    @PatchMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody ChangePasswordRequest request) {
        securityService.changePassword(accountId(jwt), request.currentPassword(), request.newPassword());
    }

    @PostMapping("/logout-all")
    public LogoutAllDevicesResponse logoutAllDevices(@AuthenticationPrincipal Jwt jwt) {
        return new LogoutAllDevicesResponse(securityService.logoutAllDevices(accountId(jwt)));
    }

    private static AccountId accountId(Jwt jwt) {
        return new AccountId(UUID.fromString(jwt.getSubject()));
    }

    public record ChangePasswordRequest(
            @NotBlank @Size(min = 12, max = 128) String currentPassword,
            @NotBlank @Size(min = 12, max = 128) String newPassword
    ) {
    }

    public record ProfileResponse(
            String email,
            String maskedNationalId,
            String maskedVerifiedPhone,
            boolean phoneVerified,
            Instant phoneVerifiedAt,
            Instant accountCreatedAt
    ) {
    }

    public record LogoutAllDevicesResponse(int revokedSessions) {
    }
}
