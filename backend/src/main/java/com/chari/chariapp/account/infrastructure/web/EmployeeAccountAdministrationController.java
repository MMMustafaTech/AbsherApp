package com.chari.chariapp.account.infrastructure.web;

import com.chari.chariapp.account.application.ChangeEmployeeAccountStatusCommand;
import com.chari.chariapp.account.application.EmployeeAccountAdministrationUseCase;
import com.chari.chariapp.account.application.ProvisionEmployeeAccountCommand;
import com.chari.chariapp.account.domain.AccountId;
import com.chari.chariapp.account.domain.AccountStatus;
import com.chari.chariapp.shared.security.PersonalDataProtector;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/staff-accounts")
public class EmployeeAccountAdministrationController {
    private final EmployeeAccountAdministrationUseCase administration;
    private final PersonalDataProtector dataProtector;

    public EmployeeAccountAdministrationController(EmployeeAccountAdministrationUseCase administration, PersonalDataProtector dataProtector) {
        this.administration = administration;
        this.dataProtector = dataProtector;
    }

    @PostMapping
    public ResponseEntity<StaffAccountResponse> provisionEmployee(
            @Valid @RequestBody ProvisionEmployeeRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        AccountId accountId = administration.provisionEmployee(new ProvisionEmployeeAccountCommand(
                administratorId(jwt), dataProtector.lookup(email), dataProtector.encrypt(email), request.password()
        ));
        return ResponseEntity.status(HttpStatus.CREATED).body(new StaffAccountResponse(accountId.value(), AccountStatus.ACTIVE));
    }

    @PatchMapping("/{accountId}/status")
    public ResponseEntity<Void> changeStatus(
            @PathVariable UUID accountId,
            @Valid @RequestBody ChangeStatusRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        administration.changeEmployeeStatus(new ChangeEmployeeAccountStatusCommand(
                administratorId(jwt), new AccountId(accountId), request.status()
        ));
        return ResponseEntity.noContent().build();
    }

    private static AccountId administratorId(Jwt jwt) {
        return new AccountId(UUID.fromString(jwt.getSubject()));
    }

    public record ProvisionEmployeeRequest(
            @NotBlank @Email @Size(max = 254) String email,
            @NotBlank @Size(min = 12, max = 128) String password
    ) {
    }

    public record ChangeStatusRequest(@NotNull AccountStatus status) {
    }

    public record StaffAccountResponse(UUID accountId, AccountStatus status) {
    }
}
