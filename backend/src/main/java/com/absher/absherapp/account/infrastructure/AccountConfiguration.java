package com.absher.absherapp.account.infrastructure;

import com.absher.absherapp.account.application.CreateAccountService;
import com.absher.absherapp.account.application.CreateAccountUseCase;
import com.absher.absherapp.account.application.CitizenProfileService;
import com.absher.absherapp.account.application.CitizenSecurityService;
import com.absher.absherapp.account.application.EmployeeAccountAdministrationService;
import com.absher.absherapp.account.application.EmployeeAccountAdministrationUseCase;
import com.absher.absherapp.account.application.CreateVerifiedCitizenAccountService;
import com.absher.absherapp.account.application.CreateVerifiedCitizenAccountUseCase;
import com.absher.absherapp.account.application.LoginService;
import com.absher.absherapp.account.application.RefreshTokenService;
import com.absher.absherapp.account.application.RequestEnrollmentOtpService;
import com.absher.absherapp.account.application.RequestEnrollmentOtpUseCase;
import com.absher.absherapp.account.application.VerifyEnrollmentOtpService;
import com.absher.absherapp.account.application.VerifyEnrollmentOtpUseCase;
import com.absher.absherapp.account.application.port.out.AccountStore;
import com.absher.absherapp.account.application.port.out.AccessTokenIssuer;
import com.absher.absherapp.account.application.port.out.EnrollmentChallengeStore;
import com.absher.absherapp.account.application.port.out.OtpCodeGenerator;
import com.absher.absherapp.account.application.port.out.OtpSender;
import com.absher.absherapp.account.application.port.out.PasswordHasher;
import com.absher.absherapp.account.application.port.out.RefreshSessionStore;
import com.absher.absherapp.account.application.port.out.RefreshTokenGenerator;
import com.absher.absherapp.account.application.port.out.RefreshTokenHasher;
import com.absher.absherapp.account.application.port.out.VerificationCodeHasher;
import com.absher.absherapp.citizen.application.port.out.CitizenStore;
import com.absher.absherapp.shared.application.port.out.OperationalAuditStore;
import com.absher.absherapp.shared.security.PersonalDataProtector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class AccountConfiguration {

    @Bean
    public LoginService loginService(
            AccountStore accountStore,
            PasswordHasher passwordHasher,
            RefreshSessionStore refreshSessionStore,
            RefreshTokenGenerator refreshTokenGenerator,
            RefreshTokenHasher refreshTokenHasher,
            AccessTokenIssuer accessTokenIssuer,
            Clock clock
    ) {
        return new LoginService(
                accountStore, passwordHasher, refreshSessionStore, refreshTokenGenerator,
                refreshTokenHasher, accessTokenIssuer, clock
        );
    }

    @Bean
    public RefreshTokenService refreshTokenService(
            AccountStore accountStore,
            RefreshSessionStore refreshSessionStore,
            RefreshTokenHasher refreshTokenHasher,
            LoginService loginService,
            Clock clock
    ) {
        return new RefreshTokenService(accountStore, refreshSessionStore, refreshTokenHasher, loginService, clock);
    }

    @Bean
    public CreateAccountUseCase createAccountUseCase(
            AccountStore accountStore,
            PasswordHasher passwordHasher,
            Clock clock
    ) {
        return new CreateAccountService(accountStore, passwordHasher, clock);
    }

    @Bean
    public EmployeeAccountAdministrationUseCase employeeAccountAdministrationUseCase(
            AccountStore accountStore,
            PasswordHasher passwordHasher,
            OperationalAuditStore auditStore,
            Clock clock
    ) {
        return new EmployeeAccountAdministrationService(accountStore, passwordHasher, auditStore, clock);
    }

    @Bean
    public CreateVerifiedCitizenAccountUseCase createVerifiedCitizenAccountUseCase(
            AccountStore accountStore,
            EnrollmentChallengeStore challengeStore,
            PasswordHasher passwordHasher,
            Clock clock
    ) {
        return new CreateVerifiedCitizenAccountService(accountStore, challengeStore, passwordHasher, clock);
    }

    @Bean
    public CitizenProfileService citizenProfileService(
            AccountStore accountStore, CitizenStore citizenStore, PersonalDataProtector dataProtector
    ) {
        return new CitizenProfileService(accountStore, citizenStore, dataProtector);
    }

    @Bean
    public CitizenSecurityService citizenSecurityService(
            AccountStore accountStore,
            PasswordHasher passwordHasher,
            RefreshSessionStore refreshSessionStore,
            OperationalAuditStore auditStore,
            Clock clock
    ) {
        return new CitizenSecurityService(accountStore, passwordHasher, refreshSessionStore, auditStore, clock);
    }

    @Bean
    public RequestEnrollmentOtpUseCase requestEnrollmentOtpUseCase(
            CitizenStore citizenStore,
            EnrollmentChallengeStore challengeStore,
            OtpCodeGenerator codeGenerator,
            VerificationCodeHasher codeHasher,
            OtpSender otpSender,
            Clock clock
    ) {
        return new RequestEnrollmentOtpService(
                citizenStore, challengeStore, codeGenerator, codeHasher, otpSender, clock
        );
    }

    @Bean
    public VerifyEnrollmentOtpUseCase verifyEnrollmentOtpUseCase(
            EnrollmentChallengeStore challengeStore,
            VerificationCodeHasher codeHasher,
            Clock clock
    ) {
        return new VerifyEnrollmentOtpService(challengeStore, codeHasher, clock);
    }
}
